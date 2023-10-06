<h1>SoSoHappy</h1> 
<img align="right" src="https://skillicons.dev/icons?i=gcp,kubernetes,docker,jenkins,prometheus,grafana,kafka,java,spring,mysql,mongodb">

![sosohappy-icon-removebg-preview](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/6c3db330-b80f-4837-b286-95f45d0cd6ae)

- - -
이 문서는 iOS 앱 SoSoHappy의 서버단 구조 및 기능을 설명합니다. <br><br>
클라이언트 3명([@suekim999](https://github.com/suekim999), [@rirupark](https://github.com/rirupark), [@kyungee](https://github.com/kyungeee)), 서버 1명([@liardanc3](https://github.com/liardanc3))이 참여한 프로젝트입니다. <br>
클라이언트단 레포지토리는 [여기서](https://github.com/So-So-Happy/SoSoHappy-iOS), API 문서는 [여기서](https://sosohappy.gitbook.io/sosohappy/) 확인하실 수 있습니다.
- - -

<br>

# 목차
- [Microservices](#microservices)
  + [구성 서버](#구성-서버)
  + [인증 서버](#인증-서버)
  + [피드 서버](#피드-서버)
  + [채팅 서버](#채팅-서버)
  + [알림 서버](#알림-서버)
- [Load Balancer](#load-balancer)
  + [frontend](#frontend)
  + [backend](#backend)
- [Message Queue](#message-queue)
  + [topic : springCloudBus](#topic--springcloudbus)
  + [topic : accessToken](#topic--accesstoken)
  + [topic : resign](#topic--resign)
  + [topic : expired](#topic--expired)
  + [topic : noticeLike](#topic--noticelike)
- [CI/CD](#cicd)
  + [build and deployment config-service](#build-and-deployment-config-service)
  + [sleep](#sleep)
  + [build and deployment other services](#build-and-deployment-other-services)
- [Monitoring](#monitoring)
  + [spring microservices](#spring-microservices)
  + [database](#database)

<br>
  
# Microservices
![microservices](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/e5b0e50a-9403-4753-98a2-cb88e28b24e0)

> 프로젝트의 서버단에 포함된 서비스들의 구조를 나타내는 그림입니다.<br><br>
> 구성, 인증, 피드, 채팅, 알림서버는 Rolling update 및 ReplicaSet 생성을 정의하기 위해 Deployment로 앱을 배포하였습니다.<br>
> 모든 deployment의 Replica는 메모리 이슈로 1개만 존재합니다.<br>
> 
> Mysql, MongoDB 서버는 마운트한 폴더를 지속적으로 사용하게 위해 StatefulSet으로 앱을 배포하였습니다.<br><br>
> Grafana, Prometheus, Jenkins, Kafka는 워커노드 메모리 이슈로 쿠버네티스에 올리지 않고 VM 내부에서 도커 컨테이너로 실행하였습니다.


<br>

###  구성 서버 
최신 구성 정보(property)를 타겟 서버에 전파하기 위해 구현한 서버입니다.
<details><summary>detail</summary>
<br>

구성 서버의 주요한 의존성 구성입니다.

``` java
runtimeOnly 'io.micrometer:micrometer-registry-prometheus'
implementation 'io.micrometer:micrometer-core'
implementation 'org.springframework.boot:spring-boot-starter-actuator'

implementation 'org.springframework.cloud:spring-cloud-starter-bus-kafka'
implementation 'org.springframework.cloud:spring-cloud-config-server'
testImplementation 'org.springframework.kafka:spring-kafka-test'
```

첫 3줄은 metric 데이터를 수집하여 [모니터링](#spring-microservices) 하기 위해 추가하였습니다.
이후 3줄은 property의 최신 정보를 전파하기 위해 추가하였습니다.

<br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/40e07af63b88a420e570178f97597584c7c70b7b/config-service/src/main/java/sosohappy/configservice/ConfigServiceApplication.java#L7-L16
`@EnableConfigServer` 애노테이션을 추가하여 이 서버를 구성 정보 전파 서버로 설정할 수 있습니다.
구성정보는 kafka의 토픽 [springCloudBus](#topic--springcloudbus)로 전파되어 타겟 서버가 구성 정보를 업데이트 할 수 있습니다.

property 파일들은 외부에 노출되면 안되는 내용을 포함하기 때문에 [서브모듈](https://github.com/So-So-Happy/SoSoHappy-BackEnd/tree/master/config-service)에 모아두고 관리합니다.
</details>

<br>

###  인증 서버 
소셜 로그인 및 유저 정보 관련 작업을 수행하는 서버입니다.
<details><summary>detail</summary>
<br>

인증 서버의 주요한 의존성 구성입니다.
```java
implementation 'org.springframework.cloud:spring-cloud-starter-config'
implementation "org.springframework.cloud:spring-cloud-starter-bus-kafka"
testImplementation 'org.springframework.kafka:spring-kafka-test'

implementation "org.springframework.boot:spring-boot-starter-actuator"
runtimeOnly 'io.micrometer:micrometer-registry-prometheus'
implementation 'io.micrometer:micrometer-core'

runtimeOnly 'com.mysql:mysql-connector-j'

implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
implementation 'com.auth0:java-jwt:4.2.1'
```
첫 3줄은 [구성 정보를 전파](#topic--springcloudbus)받거나 메시지 큐를 이용해 [JWT](#topic--accesstoken)를 전파하기 위해 추가되었습니다.
<br>이후 3줄은 metric 데이터를 수집하여 [모니터링](#spring-microservices) 하기 위해 추가하였습니다.
<br>이후 1줄은 퍼시스턴트 계층 관련 작업 및 피드 데이터를 MySQL에 저장하기 위해 추가하였습니다.
<br>마지막 3줄은 소셜 로그인 구현 및 JWT를 자체적으로 관리하기 위해 추가하였습니다.
<br>
<br>
**인증 서버  구현 API 및 주요 로직 목록.**
<details>
  <summary>
  <code><b>소셜 로그인</b></code>
  </summary>
  
https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/0f9c4ed20a606b2c5d257f11ed11c24289f549f0/auth-service/src/main/java/sosohappy/authservice/config/SecurityConfig.java#L30-L50
SecurityFilterChain을 빈으로 정의하여 Spring Security 필터 체인을 구성하는 코드입니다.
<br><br>

```java
...

  .csrf(AbstractHttpConfigurer::disable)
  .formLogin(AbstractHttpConfigurer::disable)
  .httpBasic(AbstractHttpConfigurer::disable)
  .sessionManagement(sessionConfigurer -> sessionConfigurer
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
  )

...
```
JWT 토큰을 이용하여 로그인하므로 csrf, 기본 로그인 화면, http 기본 인증 및 세션은 비활성화 하였습니다.
<br><br>

``java
.oauth2Login(loginConfigurer -> loginConfigurer
                .tokenEndpoint(tokenEndpointConfig -> tokenEndpointConfig
                    .accessTokenResponseClient(accessTokenResponseClient())
                )
                .userInfoEndpoint(userEndpointConfig -> userEndpointConfig
                    .userService(customOAuth2UserService)
                )
                .successHandler(oAuth2LoginSuccessHandler)
                .failureHandler(oAuth2LoginFailureHandler)
)                
```
OAuth2 로그인 설정 구성입니다.<br>
유저가 로그인을 한 후 받은 코드로 OAuth2 공급자에게게 액세스토큰 요청을 하기 위한 `tokenEndpoint`, 받은 토큰으로 유저 정보를 로드하기 위한 `userInfoEndpoint`, 유저 정보 로드까지 성공했을 경우 실행되는 로직인 successHandler, 실패했을 때 실행되는 로직인 failureHandler가 포함됩니다.
<br><br>




</details>
  

<details>
  <summary>
  <code><b>프로필 설정</b></code>
  </summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/auth-service/src/main/java/sosohappy/authservice/controller/UserController.java#L28-L32
`/setProfile` 경로로 API 요청이 들어오면 Service 단의 `setProfile()` 메소드를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/auth-service/src/main/java/sosohappy/authservice/service/UserService.java#L83-L98
`setProfile()` 메소드는 들어온 이메일로 유저를 찾고, 성공적으로 유저를 찾으면 해당 유저의 프로필을 수정합니다.<br>
<br><br>

</details>

<details>
  <summary>
  <code><b>프로필 사진 조회</b></code>
  </summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/auth-service/src/main/java/sosohappy/authservice/controller/UserController.java#L40-L44
`/findProfileImg` 경로로 API 호출이 들어오면 Service단의 `findProfileImg()` 메소드를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/auth-service/src/main/java/sosohappy/authservice/service/UserService.java#L100-L109
`findProfileImg()` 메소드는 닉네임으로 유저를 찾아서 해당 유저의 프로필 사진을 클라이언트에 반환합니다.<br>
<br><br>

</details>

<details>
  <summary>
  <code><b>회원 탈퇴</b></code>
  </summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/auth-service/src/main/java/sosohappy/authservice/controller/UserController.java#L34-L38
`/resign` 경로로 API가 호출되면 Service단의 `resign()` 메소드를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/auth-service/src/main/java/sosohappy/authservice/service/UserService.java#L44-L65
https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/auth-service/src/main/java/sosohappy/authservice/service/UserService.java#L113-L115
`resign()` 메소드는 `produceResign()` 메소드를 호출하고, 이후 DB에 저장된 유저 정보를 삭제합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/auth-service/src/main/java/sosohappy/authservice/kafka/KafkaProducerAspect.java#L17-L31
```java
if(kafkaProducer.topic().equals("resign")){
     String email = (String) joinPoint.getArgs()[0];
     String nickname = (String) joinPoint.getArgs()[1];
     kafkaTemplate.send(kafkaProducer.topic(), email.getBytes(), nickname.getBytes());
}
```
`produceResign()` 메소드로 전달된 파라미터를 기반으로 kafka broker에 회원탈퇴 했다는 메시지를 전달하기 위해 구현된 코드입니다.<br>
피드 서버는 해당 메시지를 수신 후 탈퇴한 유저의 피드를 삭제합니다.
<br><br>

</details>

<details>
  <summary>
  <code><b>닉네임 중복 검사</b></code>
  </summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/auth-service/src/main/java/sosohappy/authservice/controller/UserController.java#L22-L26
`/checkDuplicateNickname` 경로로 API가 호출되면 Service단의 `checkDuplicateNickname()` 메소드를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/auth-service/src/main/java/sosohappy/authservice/service/UserService.java#L67-L81
`checkDuplicateNickname()` 메소드는 닉네임으로 유저를 조회하여 해당 닉네임을 가진 유저가 있는지 확인하고 결과를 반환합니다.
<br><br>

</details>

<details>
  <summary>
  <code><b>토큰 재발급</b></code>
  </summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/auth-service/src/main/java/sosohappy/authservice/jwt/filter/JwtFilter.java#L23-L54
토큰 재발급 API는 Controller단이 아닌 Filter에 구현되었습니다.<br>
경로에 `/reIssueToken`이 포함된 경우 헤더로 넘어온 기존 accessToken과 Email이 일치하는지, refreshToken이 이 유저의 refreshToken이 맞는지 검증 후 `reIssueToken()` 메소드를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/auth-service/src/main/java/sosohappy/authservice/jwt/filter/JwtFilter.java#L56-L70
`reIssueToken()` 메소드에선 reponse Header에 새로운 accessToken과 refreshToken을 세팅하고 클라이언트에 반환합니다.<br>
<br><br>

</details>
  
</details>

<br>

###  피드 서버 
피드 추가, 수정, 삭제, 추천 등의 작업을 수행하는 서버입니다.
<details><summary>detail</summary>

<br>

피드 서버의 주요한 의존성 구성입니다.
```java
implementation 'org.springframework.cloud:spring-cloud-starter-config'
implementation "org.springframework.cloud:spring-cloud-starter-bus-kafka"
testImplementation 'org.springframework.kafka:spring-kafka-test'

implementation "org.springframework.boot:spring-boot-starter-actuator"
runtimeOnly 'io.micrometer:micrometer-registry-prometheus'
implementation 'io.micrometer:micrometer-core'

runtimeOnly 'com.mysql:mysql-connector-j'
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation "com.querydsl:querydsl-jpa:5.0.0:jakarta"
annotationProcessor "com.querydsl:querydsl-apt:5.0.0:jakarta"
```
첫 3줄은 [구성 정보를 전파](#topic--springcloudbus)받거나 메시지 큐를 이용해 [JWT](#topic--accesstoken)를 전파받기 위해 추가되었습니다.
<br>이후 3줄은 metric 데이터를 수집하여 [모니터링](#spring-microservices) 하기 위해 추가하였습니다.
<br>마지막 4줄은 퍼시스턴트 계층 관련 작업 및 피드 데이터를 MySQL에 저장하기 위해 추가하였습니다.
<br>
<br>
**피드 서버  구현 API 및 주요 로직 목록.**

<details>
  <summary>
  <code><b>피드 저장</b></code>
  </summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/5ac060daafbee1fe7d5ae3d392afa408f37f686b/feed-service/src/main/java/sosohappy/feedservice/controller/FeedController.java#L36-L39
`/saveFeed`로 API 호출이 온 경우 Controller 단을 거쳐 Service 단의 `updateFeed()` 메소드를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/5ac060daafbee1fe7d5ae3d392afa408f37f686b/feed-service/src/main/java/sosohappy/feedservice/service/FeedService.java#L42-L54
`updateFeed()` 함수에서 Repository 단의 `findByNicknameAndDate()` 메소드를 호출합니다.<br>
`findByNicknameAndDate()` 함수는 닉네임과 날짜를 입력받으면 해당 날짜에 그 유저가 작성한 피드를 반환합니다(Optional).<br>
이후 happinessService의 `updateSimilarityMatrix()` 메소드를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/5ac060daafbee1fe7d5ae3d392afa408f37f686b/feed-service/src/main/java/sosohappy/feedservice/service/HappinessService.java#L39-L56
https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/5ac060daafbee1fe7d5ae3d392afa408f37f686b/feed-service/src/main/java/sosohappy/feedservice/service/HappinessService.java#L150-L174
피드의 카테고리와 행복 지수를 기반으로 2차원 배열을 업데이트 합니다. 이 값을 기준으로 유저에게 카테고리 추천을 하게 됩니다.
<br>
이후 피드를 업데이트 합니다.
<br><br>
</details>

<details>
  <summary>
  <code><b>유저 개인 피드 조회</b></code>
  </summary>
  
https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/5ac060daafbee1fe7d5ae3d392afa408f37f686b/feed-service/src/main/java/sosohappy/feedservice/controller/FeedController.java#L26-L34
https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/5ac060daafbee1fe7d5ae3d392afa408f37f686b/feed-service/src/main/java/sosohappy/feedservice/service/FeedService.java#L32-L40
`/findDayFeed` 와 `/findMonthFeed` 경로로 API가 호출되면 Controller단과 Service단을 거쳐 Repository단의 메소드를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/5ac060daafbee1fe7d5ae3d392afa408f37f686b/feed-service/src/main/java/sosohappy/feedservice/repository/FeedQueryRepositoryImpl.java#L27-L58
Repository단에선 입력된 닉네임과 날짜를 통해 해당 유저의 피드를 반환합니다.
<br><br>

</details>

<details>
  <summary>
  <code><b>전체 피드 조회</b></code>
  </summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/5ac060daafbee1fe7d5ae3d392afa408f37f686b/feed-service/src/main/java/sosohappy/feedservice/controller/FeedController.java#L46-L51
https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/5ac060daafbee1fe7d5ae3d392afa408f37f686b/feed-service/src/main/java/sosohappy/feedservice/service/FeedService.java#L63-L65
`/findOtherFeed` 경로로 API가 호출되면 Controller단과 Service단을 거쳐 Repository단의 메소드를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/5ac060daafbee1fe7d5ae3d392afa408f37f686b/feed-service/src/main/java/sosohappy/feedservice/repository/FeedQueryRepositoryImpl.java#L118-L144
`findByNicknameAndDateWithSlicing()` 메소드가 호출되면 슬라이싱을 통해 피드를 반환합니다. 한번에 25개를 초과하는 쿼리가 날라올 경우 예외를 반환합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/5ac060daafbee1fe7d5ae3d392afa408f37f686b/feed-service/src/main/java/sosohappy/feedservice/domain/dto/SliceResponse.java#L8-L27
반환된 피드는 Slice 객체 멤버변수 중 불필요한 것들을 제외하고 위 DTO로 변환되어 클라이언트에 반환됩니다.
<br><br>

</details>

<details>
  <summary>
  <code><b>특정 유저 피드 조회</b></code>
  </summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/5ac060daafbee1fe7d5ae3d392afa408f37f686b/feed-service/src/main/java/sosohappy/feedservice/controller/FeedController.java#L58-L63
https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/5ac060daafbee1fe7d5ae3d392afa408f37f686b/feed-service/src/main/java/sosohappy/feedservice/service/FeedService.java#L67-L69
`/findUserFeed` 경로로 API가 호출되면 Controller단과 Service단을 거쳐 Repository단의 메소드를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/5ac060daafbee1fe7d5ae3d392afa408f37f686b/feed-service/src/main/java/sosohappy/feedservice/repository/FeedQueryRepositoryImpl.java#L146-L172
`findUserFeed()` 메소드가 호출되면 슬라이싱을 통해 피드를 반환합니다. 한번에 25개를 초과하는 쿼리가 날라올 경우 예외를 반환합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/5ac060daafbee1fe7d5ae3d392afa408f37f686b/feed-service/src/main/java/sosohappy/feedservice/domain/dto/SliceResponse.java#L8-L27
반환된 피드는 Slice 객체 멤버변수 중 불필요한 것들을 제외하고 위 DTO로 변환되어 클라이언트에 반환됩니다.
<br><br>

</details>

<details>
  <summary>
  <code><b>행복 분석 결과</b></code>
  </summary>

<br>
가장 긍정적으로 평가된 카테고리(운동, 여행 등) 3개와, 이를 기반으로 긍정적으로 평가 할 확률이 높은 카테고리를 추천하는 API 입니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/feed-service/src/main/java/sosohappy/feedservice/controller/HappinessController.java#L20-L23
https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/feed-service/src/main/java/sosohappy/feedservice/service/HappinessService.java#L32-L37
`/analysisHappiness` 경로로 API가 호출되면 Service단의 `analysisHappiness()` 메소드를 호출하고, 이 메소드는 `getBestCategoryList()`, `getRecommendCategoryList()` 메소드를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/feed-service/src/main/java/sosohappy/feedservice/service/HappinessService.java#L117-L138
`getBestCategoryList()`는 유저의 이번 달 피드를 조회해서 높은 점수를 받은 카테고리 최대 3개를 반환합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/feed-service/src/main/java/sosohappy/feedservice/service/HappinessService.java#L89-L115
`getRecommendCategoryList()`는 유저의 이번 달 피드 기반으로 긍정적으로 평가 할 확률이 높은 카테고리 최대 10개를 반환합니다.
<br><br>

</details>

<details>
  <summary>
  <code><b>월간 행복 수치</b></code>
  </summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/feed-service/src/main/java/sosohappy/feedservice/controller/HappinessController.java#L25-L28
`/findMonthHappiness` 경로로 API가 호출되면 Service단의 `findMonthHappiness()` 메소드를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/feed-service/src/main/java/sosohappy/feedservice/service/HappinessService.java#L58-L63
https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/feed-service/src/main/java/sosohappy/feedservice/repository/FeedQueryRepositoryImpl.java#L88-L102
이 메소드는 Repository단의 `findHappinessAndDateDtoByNicknameAndDateDto()` 메소드를 호출하여 이번 달 피드의 행복 지수를 가져옵니다.<br>
행복 지수는 날짜를 기준으로 오름차순으로 정렬되어 클라이언트에 반환됩니다.
<br><br>

</details>

<details>
  <summary>
  <code><b>연간 행복 수치</b></code>
  </summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/feed-service/src/main/java/sosohappy/feedservice/controller/HappinessController.java#L30-L33
`/findYearHappiness` 경로로 API가 호출되면 Service단의 `findYearHappiness()` 메소드를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/feed-service/src/main/java/sosohappy/feedservice/service/HappinessService.java#L65-L85
`findYearHappiness()` 함수는 Repository단의 `findMonthHappinessAvgByNicknameAndDate()` 메소드를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/feed-service/src/main/java/sosohappy/feedservice/repository/FeedQueryRepositoryImpl.java#L104-L116
`findMonthHappinessAvgByNicknameAndDate()` 메소드 구현부로, 이 메소드는 이번 달 행복지수의 평균 값을 계산하여 반환합니다.<br>
서비스단은 1~12월의 평균 행복지수를 날짜 오름차순으로 클라이언트에 반환합니다.
<br><br>
</details>

<details>
  <summary>
  <code><b>유저 피드 공개여부 변경</b></code>
  </summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/feed-service/src/main/java/sosohappy/feedservice/controller/FeedController.java#L41-L44
`/updatePublicStatus` 경로로 API가 호출되면 Service단의 `updatePublicStatus()` 메소드를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/feed-service/src/main/java/sosohappy/feedservice/service/FeedService.java#L56-L61
`updatePublicStatus()` 메소드는 닉네임과 날짜를 통해 공개 여부 업데이트가 일어난 피드를 찾아 공개 여부를 변경합니다.
<br><br>

</details>

<details>
  <summary>
  <code><b>좋아요 여부 변경</b></code>
  </summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/feed-service/src/main/java/sosohappy/feedservice/controller/FeedController.java#L53-L56
`/updateLike` 경로로 API가 호출되면 Service단의 `updateLike()` 메소드를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/feed-service/src/main/java/sosohappy/feedservice/service/FeedService.java#L71-L81
`updateLike()` 메소드에선 닉네임과 날짜로 좋아요가 눌린 피드를 찾아 좋아요 여부를 업데이트 합니다. <br>
이때 좋아요가 아닌 상태에서 좋아요가 눌렸을 경우 `produceUpdateLike()` 메소드를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/feed-service/src/main/java/sosohappy/feedservice/service/FeedService.java#L90-L93
`produceUpdateLike()` 메소드를 좋아요를 누른 사람 닉네임, 피드 주인 닉네임과 날짜를 반환합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/3c7999cc5e9534358f489ababa7985765ee09f3a/feed-service/src/main/java/sosohappy/feedservice/kafka/KafkaProducerAspect.java#L19-L30
값이 성공적으로 반환되면 위 메소드를 통해 Kafka Broker에 좋아요를 누른 사람 닉네임, 피드 주인 닉네임과 날짜 데이터를 전송합니다.
전송 된 데이터는 알림 서버에서 피드 주인에게 해당 내용을 포함하는 메시지를 보내 푸시알림을 띄울 수 있게 합니다.
<br><br>

</details>

</details>

<br>

###  채팅 서버 
 다이렉트 메시지 송수신을 위한 서버입니다.
<details><summary>detail</summary>

<br>

채팅 서버의 주요한 의존성 구성입니다.
``` java
implementation 'org.springframework.cloud:spring-cloud-starter-config'
implementation "org.springframework.cloud:spring-cloud-starter-bus-kafka"
testImplementation 'org.springframework.kafka:spring-kafka-test'

implementation "org.springframework.boot:spring-boot-starter-actuator"
runtimeOnly 'io.micrometer:micrometer-registry-prometheus'
implementation 'io.micrometer:micrometer-core'

implementation 'org.springframework.boot:spring-boot-starter-data-mongodb-reactive'
```
첫 3줄은 [구성 정보를 전파](#topic--springcloudbus)받거나 메시지 큐를 이용해 [JWT](#topic--accesstoken)를 전파받기 위해 추가되었습니다.
이후 3줄은 metric 데이터를 수집하여 [모니터링](#spring-microservices) 하기 위해 추가하였습니다.
마지막 줄은 채팅 데이터를 MongoDB에 저장하기 위해 추가하였습니다.
<br>
<br>
**채팅 서버  구현 API 및 주요 로직 목록.**

<details>
  <summary>
  <code><b>WebSocket 연결</b></code>
  </summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/81ab3fd36b9d6c71498b58a6798ba9fe7a57cc01/dm-service/src/main/java/sosohappy/dmservice/config/WebSocketConfig.java#L19-L22
다음과 같이 `/dm-service/connect-dm`을 websocket 연결 url로 설정합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/81ab3fd36b9d6c71498b58a6798ba9fe7a57cc01/dm-service/src/main/java/sosohappy/dmservice/jwt/filter/JwtFilter.java#L19-L31
JWT 토큰 검증을 위한 filter가 존재하기 때문에 HTTP 요청의 헤더를 참조하여 토큰을 검증합니다.
모니터링을 위해 `/actuator`가 경로에 포함될 경우 인증과정이 생략됩니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/81ab3fd36b9d6c71498b58a6798ba9fe7a57cc01/dm-service/src/main/java/sosohappy/dmservice/jwt/service/JwtService.java#L11-L38
토큰을 검증하는 로직이 구현된 JwtService 입니다. JWT 의존성을 끌어오지 않고 인증서버에서 보내준 Email과 AccessToken 값을 이용해서 토큰을 검증합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/81ab3fd36b9d6c71498b58a6798ba9fe7a57cc01/dm-service/src/main/java/sosohappy/dmservice/service/MessageService.java#L27-L34
처음 세션이 연결될 때 `doOnSubscribe()`를 호출합니다.
요청 파라미터에서 닉네임을 추출하여 닉네임과 SessionId, SessionId와 Session 정보를 Key, Value 쌍으로 저장합니다.
이렇게 저장된 세션 정보는 채팅을 전송할때 사용됩니다.
<br><br>

</details>

<details>
  <summary>
  <code><b>채팅 전송</b></code>
  </summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/81ab3fd36b9d6c71498b58a6798ba9fe7a57cc01/dm-service/src/main/java/sosohappy/dmservice/service/MessageService.java#L27-L34
세션이 연결된 상태에서 유저가 메시지를 보낼 경우 `sendMessage()`를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/81ab3fd36b9d6c71498b58a6798ba9fe7a57cc01/dm-service/src/main/java/sosohappy/dmservice/service/MessageService.java#L66-L75
sendMessage 함수는 채팅 데이터에서 수신자 세션 정보를 추출하여 메시지를 보냅니다.
<br><br>

``` java
{
  "sender": "sender_nickname",
  "receiver": "receiver_nickname",
  "date": 2023090901010101,
  "text": "hi~"
}
```
전달되는 메시지는 위와 같은 json 형태의 데이터 입니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/81ab3fd36b9d6c71498b58a6798ba9fe7a57cc01/dm-service/src/main/java/sosohappy/dmservice/service/MessageService.java#L53-L59
메시지 전달에 성공하면 `doOnNext(this::saveDirectMessage)`를 호출하여 DB에 채팅 데이터를 저장합니다.
<br><br>

</details>
<details>
  <summary>
  <code><b>1:1 채팅 내역 조회</b></code>
  </summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/b1d508d5a2cdfbd614261e4bc16caeb1d3f0af39/dm-service/src/main/java/sosohappy/dmservice/controller/MessageController.java#L18-L22
https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/b1d508d5a2cdfbd614261e4bc16caeb1d3f0af39/dm-service/src/main/java/sosohappy/dmservice/service/MessageService.java#L36-L38
`/findDirectMessage`로 API 호출이 온 경우 Controller 단과 Service 단을 거쳐서 Repository 단의 `findDirectMessage()` 메소드를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/b1d508d5a2cdfbd614261e4bc16caeb1d3f0af39/dm-service/src/main/java/sosohappy/dmservice/repository/MessageQueryRepositoryImpl.java#L21-L35
`findDirectMessage()` 메소드에선 `messageRoomId`와 `timeBoundary`를 기준으로 과거의 채팅을 `messageCnt` 만큼 가져와 MessageDto로 매핑 후 반환합니다.
`messageRoomId`는 1:1 채팅에 포함된 두 유저의 닉네임을 정렬하여 ','로 구분지은 String 값이므로 송신자와 수신자에 관계없이 같은 데이터를 참조할 수 있습니다.
<br><br>

```java
[
    {
        "sender": "sender_nickname",
        "receiver": "receiver1_nickname",
        "date": 2023090905010101,
        "text": "message s->r1"
    },
    {
        "sender": "sender_nickname",
        "receiver": "receiver1_nickname",
        "date": 2023090905066666,
        "text": "message s->r1"
    }
]
```
클라이언트는 다음과 같은 식의 json 형태의 데이터를 응답받습니다.

<br><br>

</details>

<details>
  <summary>
  <code><b>채팅방 목록 조회</b></code>
  </summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/b1d508d5a2cdfbd614261e4bc16caeb1d3f0af39/dm-service/src/main/java/sosohappy/dmservice/controller/MessageController.java#L25-L28
https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/b1d508d5a2cdfbd614261e4bc16caeb1d3f0af39/dm-service/src/main/java/sosohappy/dmservice/service/MessageService.java#L40-L42
`/findMultipleDirectMessage`로 API 호출이 온 경우 Controller 단과 Service 단을 거쳐서 Repository 단의 `findMultipleDirectMessage()` 메소드를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/b1d508d5a2cdfbd614261e4bc16caeb1d3f0af39/dm-service/src/main/java/sosohappy/dmservice/repository/MessageQueryRepositoryImpl.java#L37-L53
`findMultipleDirectMessage()` 메소드에선 송수신자 중 유저의 닉네임이 포함 된 경우의 문서만을 선택한 후 `messageRoomId`로 그룹화하여 최신 채팅 정보만을 루트 문서로 설정 후 반환합니다.
<br><br>

```java
[
    {
        "sender": "sender_nickname",
        "receiver": "receiver1_nickname",
        "date": 2023090905077777,
        "text": "message s->r1"
    },
    {
        "sender": "receiver2_nickname",
        "receiver": "sender_nickname",
        "date": 2023090905033333,
        "text": "message r2->s"
    }
]
```
클라이언트는 다음과 같이 본인이 포함된 채팅의 가장 최신 대화 내역을 내림차순으로 응답받습니다.

<br>
</details>

</details>
<br>

###  알림 서버 
푸시 알림 전송을 위한 서버입니다.
<details><summary>detail</summary>
<br>
  
알림 서버의 주요한 의존성 구성입니다.

``` java
implementation 'org.springframework.cloud:spring-cloud-starter-config'
implementation "org.springframework.cloud:spring-cloud-starter-bus-kafka"
testImplementation 'org.springframework.kafka:spring-kafka-test'

implementation "org.springframework.boot:spring-boot-starter-actuator"
runtimeOnly 'io.micrometer:micrometer-registry-prometheus'
implementation 'io.micrometer:micrometer-core'
```
첫 3줄은 [구성 정보](#topic--springcloudbus)를 전파받거나 메시지 큐를 이용해 [회원 탈퇴](#topic--resign)한 회원과의 세션을 끊기 위해 추가되었습니다.
이후 3줄은 metric 데이터를 수집하여 [모니터링](#spring-microservices) 하기 위해 추가하였습니다.
<br>

**알림 서버  구현 API 및 주요 로직 목록.**

<details>
  <summary>
  <code><b>WebSocket 연결</b></code>
  </summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/f401581229f8d02cb7daed86e87bfd2c4799ebb2/notice-service/src/main/java/sosohappy/noticeservice/config/WebSocketConfig.java#L19-L22
다음과 같이 `/notice-service/connect-notice`를 websocket 연결 url로 설정합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/f401581229f8d02cb7daed86e87bfd2c4799ebb2/notice-service/src/main/java/sosohappy/noticeservice/jwt/filter/JwtFilter.java#L19-L31
JWT 토큰 검증을 위한 filter가 존재하기 때문에 HTTP 요청의 헤더를 참조하여 토큰을 검증합니다.
모니터링을 위해 `/actuator`가 경로에 포함될 경우 인증과정이 생략됩니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/f401581229f8d02cb7daed86e87bfd2c4799ebb2/notice-service/src/main/java/sosohappy/noticeservice/jwt/service/JwtService.java#L11-L39
토큰을 검증하는 로직이 구현된 JwtService 입니다. JWT 의존성을 끌어오지 않고 인증서버에서 보내준 Email과 AccessToken 값을 이용해서 토큰을 검증합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/f401581229f8d02cb7daed86e87bfd2c4799ebb2/notice-service/src/main/java/sosohappy/noticeservice/handler/NoticeHandler.java#L17-L21
검증 후 이상이 없다면 Session을 연결하기 위한 함수를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/f401581229f8d02cb7daed86e87bfd2c4799ebb2/notice-service/src/main/java/sosohappy/noticeservice/service/NoticeService.java#L20-L24
세션을 연결할 때 `saveSessionInfo(session)`을 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/f401581229f8d02cb7daed86e87bfd2c4799ebb2/notice-service/src/main/java/sosohappy/noticeservice/service/NoticeService.java#L44-L47
요청 파라미터에서 닉네임을 추출하여 닉네임과 SessionId, SessionId와 Session 정보를 Key, Value 쌍으로 저장합니다.
이렇게 저장된 세션 정보는 알림 메시지를 전송할때 사용됩니다.
<br><br>

</details>

<details><summary>
  <code><b>알림 메시지 전송</b></code>
</summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/f401581229f8d02cb7daed86e87bfd2c4799ebb2/notice-service/src/main/java/sosohappy/noticeservice/kafka/KafkaConsumer.java#L30-L51
kafka broker를 통해 피드에 좋아요를 눌렀을 때 해당 알림 메시지를 보내기 위한 데이터를 가져옵니다.
가져온 데이터로 `NoticeService::sendNotice`를 호출합니다
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/f401581229f8d02cb7daed86e87bfd2c4799ebb2/notice-service/src/main/java/sosohappy/noticeservice/service/NoticeService.java#L26-L32
알림을 받을 유저의 세션을 찾아서 좋아요가 눌러졌다는 메시지를 전송합니다.
<br><br>

``` java
{
    "topic": "like",
    "data": {
        "liker": "admin",
        "date": 2023090513248392
    }
}
```
클라이언트는 이와 같은 json 메시지를 수신해서 유저의 스마트폰에 해당 내용을 포함하는 푸시알림을 띄울 수 있습니다.

</details>

</details>

<br>

# Load Balancer
![loadbalancer](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/d25c9821-b53b-49bb-8acb-06afd2ba599e)

> 트래픽을 서비스들에게 라우팅하기 위한 Load Balancer의 구조를 나타낸 그림입니다.<br><br>
> GCP 환경에서 SSL 인증서를 사용하기 위해 HTTPS Load balancer를 사용했지만, 모든 백엔드 서비스들이 1개씩만 존재하므로 로드밸런서의 부하 분산기능은 사용되지 않고 라우팅 기능만 사용되었습니다.

<br>

### frontend
![image](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/6539df7c-babd-450c-9fec-1427a0350ee3)
![image](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/a1cdc1ff-8390-4256-90a1-adaabd19c52b)

frontend는 마스터 노드가 설치된 VM으로 가는 트래픽을 정의합니다. 
SSL 인증서를 사용중이기 때문에 443포트를 사용했고, http 80포트로 들어오는 요청은 https 443으로 redirect 됩니다.

<br>

### backend
![image](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/b75f9601-2f2c-4c53-a230-5e47532ef1b1)

백엔드는 네트워크 엔드포인트 그룹으로 구성되어 있습니다.
433 포트를 통해 들어온 트래픽은 위와 같은 규칙으로 VM에 직접 라우팅됩니다. 각각 8888~8892번의 포트를 가지고 있습니다.

![image](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/e33b23d4-7e11-4d2a-b8ed-ec82e7dbe329)
라우팅 된 트래픽은 LoadBalancer로 정의된 쿠버네티스의 서비스에 들어갑니다.

<br>

# Message Queue
![messagequeue](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/49bca3a0-b7d2-4231-8659-99f7a5e6a343)

> 프로젝트의 서버단에서 사용된 미들웨어 Kafka의 토픽 및 pub/sub 구조를 나타내는 그림입니다. 
> 싱글 브로커로 구성 되어 있으며 실선은 publish, 점선은 subscribe를 의미합니다.

<br>

### topic : springCloudBus
구성서버가 각 서비스들의 구성 정보(properties, yml)를 전파하기 위해 사용되는 토픽입니다.
각 서비스들은 구성 서버가 실행중이라면 이 토픽의 메시지를 수신해서 구성 정보를 등록할 수 있고, 혹은 `/actuator/busrefresh`로 구성 서버에서 원격 업데이트 할 수 있습니다.

<br>

### topic : accessToken
인증서버가 Email, Access Token 정보를 전파하기 위해 사용되는 토픽입니다.
인증이 필요한 서비스들은 JWT 관련 의존성을 추가하지 않고 해당 토픽의 메시지를 수신해서 토큰 유효성을 검사합니다.

<details><summary>detail</summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/40e07af63b88a420e570178f97597584c7c70b7b/auth-service/src/main/java/sosohappy/authservice/jwt/service/JwtService.java#L39-L46
토큰엔 주제와 만료기간과 함께 유저의 이메일이 claim으로 포함됩니다.
커스텀 애노테이션 `@KafkaProducer` 는 createAccessToken 메소드의 인자 및 반환값으로 전파할 메시지를 설정합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/40e07af63b88a420e570178f97597584c7c70b7b/auth-service/src/main/java/sosohappy/authservice/kafka/KafkaProducerAspect.java#L10-L27
메소드가 에러없이 성공적으로 실행되면 Spring AOP의 `@AfterReturning` 애노테이션을 통해 인자 및 반환값을 가져온 후 email, access token을 byte array 형태로 브로커에 전송합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/40e07af63b88a420e570178f97597584c7c70b7b/dm-service/src/main/java/sosohappy/dmservice/kafka/KafkaConsumer.java#L10-L25
인증이 필요한 다른 서비스에서 해당 메시지를 수신해서 key, value 쌍으로 관리합니다. 이 문서에선 채팅 서버를 예시로 사용합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/40e07af63b88a420e570178f97597584c7c70b7b/dm-service/src/main/java/sosohappy/dmservice/jwt/service/JwtService.java#L17-L21
 필터에 포함되는 함수입니다. HTTP 요청에 포함된 헤더에서 Email, AccessToken을 추출하고 이 값이 브로커에게 전달받은 Email, AccessToken과 일치하는지 확인합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/40e07af63b88a420e570178f97597584c7c70b7b/dm-service/src/main/java/sosohappy/dmservice/jwt/filter/JwtFilter.java#L20-L29
 일치한다면 다음 작업을 수행하고 그렇지 않다면 403 코드와 함께 다음 작업을 수행하지 않고 반환합니다. 모니터링을 위해 "/actuator"가 경로에 포함되는 경우는 인증과정이 생략됩니다.  

</details>

<br>

### topic : resign
 인증서버가 탈퇴한 회원 정보를 전파하기 위한 토픽입니다.
 피드서버와는 데이터 정합성을 맞추고, 채팅서버 및 알림서버는 연결된 WebSocket Session을 끊기 위해 사용됩니다. 
<details><summary>detail</summary>
<br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/5913ded93c409c9b7a79f1fa72d4529ae692b6e5/feed-service/src/main/java/sosohappy/feedservice/service/FeedService.java#L90-L93
유저가 회원탈퇴 했을 때 호출되는 함수 중 하나입니다. 커스텀 애노테이션 `@KafkaProducer`을 통해 해당 함수의 리턴값을 끌어옵니다. <br>이 함수의 리턴값엔 회원탈퇴한 유저의 이메일과 닉네임이 포함됩니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/5913ded93c409c9b7a79f1fa72d4529ae692b6e5/feed-service/src/main/java/sosohappy/feedservice/kafka/KafkaProducerAspect.java#L19-L30
메소드가 에러없이 성공적으로 실행되면 Spring AOP의 `@AfterReturning` 애노테이션을 통해 인자 및 반환값을 가져옵니다.<br>
이후 이메일과 닉네임을 byte array 형태로 브로커에 전송합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/5913ded93c409c9b7a79f1fa72d4529ae692b6e5/dm-service/src/main/java/sosohappy/dmservice/kafka/KafkaConsumer.java#L27-L35
https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/5913ded93c409c9b7a79f1fa72d4529ae692b6e5/dm-service/src/main/java/sosohappy/dmservice/service/MessageService.java#L44-L49
채팅 서버나 알림 서버에선 탈퇴한 유저와의 세션 연결을 끊습니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/2f9806f2ea3568b62603bc0657dd5269c49b7246/feed-service/src/main/java/sosohappy/feedservice/kafka/KafkaConsumer.java#L26-L34
https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/2f9806f2ea3568b62603bc0657dd5269c49b7246/feed-service/src/main/java/sosohappy/feedservice/service/FeedService.java#L83-L88
피드 서버에선 탈퇴한 유저의 피드, 좋아요 목록을 삭제합니다.
<br><br>

</details>
<br>

### topic : expired
 access token이 만료되었을 때 해당 정보를 전파하기 위한 토픽입니다.
 <details><summary>detail</summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/49f7e26902ac3cf3904d969c537e177d78102efd/auth-service/src/main/java/sosohappy/authservice/config/ExecutorConfig.java#L9-L16
위와 같이 스레드 1개를 사용하는 `ScheduledExecutorService`가 Bean으로 등록되어 있습니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/49f7e26902ac3cf3904d969c537e177d78102efd/auth-service/src/main/java/sosohappy/authservice/kafka/KafkaProducerAspect.java#L22-L46
access token을 발행하고 kafka에 메시지를 보내는 시점에 만료 메시지도 보내기 위한 스케쥴을 설정합니다.<br>
36000000ms는 access token의 유효 기간입니다. 36000000ms 후 "expired" 토픽으로 이메일이 전송됩니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/49f7e26902ac3cf3904d969c537e177d78102efd/feed-service/src/main/java/sosohappy/feedservice/kafka/KafkaConsumer.java#L26-L32
피드, 채팅, 알림서버는 이 메시지를 수신하고 해당 유저의 토큰 정보를 삭제합니다. 따라서 토큰이 만료되었을 경우 접근이 허가되지 않습니다.

   
</details>

<br>

### topic : noticeLike
 피드에 좋아요를 눌렀을 때 해당 회원 정보를 전파하기 위한 토픽입니다.
<details><summary>detail</summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/c961af37a03023cd686a7edba6968bb255668f1d/feed-service/src/main/java/sosohappy/feedservice/service/FeedService.java#L83-L86
 유저가 피드에 좋아요를 누르면 호출되는 함수 중 하나입니다. 커스텀 애노테이션 `@KafkaProducer`을 통해 해당 함수의 리턴값을 끌어옵니다.
 이 함수의 리턴값엔 좋아요를 누른 유저의 닉네임과 피드 날짜, 피드 게시자의 닉네임이 포함됩니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/c961af37a03023cd686a7edba6968bb255668f1d/feed-service/src/main/java/sosohappy/feedservice/kafka/KafkaProducerAspect.java#L20-L32
 메소드가 에러없이 성공적으로 실행되면 Spring AOP의 `@AfterReturning` 애노테이션을 통해 인자 및 반환값을 가져옵니다.<br>
 이후 좋아요를 누른 유저의 닉네임과 피드 날짜, 피드 게시자의 닉네임을 byte array 형태로 브로커에 전송합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/c961af37a03023cd686a7edba6968bb255668f1d/notice-service/src/main/java/sosohappy/noticeservice/kafka/KafkaConsumer.java#L30-L51
 알림 서버가 이 데이터를 수신해서, 해당 데이터로 notice 서비스의 sendNotice 메소드를 호출합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/c961af37a03023cd686a7edba6968bb255668f1d/notice-service/src/main/java/sosohappy/noticeservice/service/NoticeService.java#L26-L31
 WebSocket으로 연결된 Session 중 좋아요 알림 메시지를 받아야 할 유저의 Session을 찾아 좋아요를 누른 유저의 닉네임과 피드에 대한 데이터를 전송합니다.

</details>

<br>

# CI/CD
![cicd](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/7e6d4bf0-6d35-4a84-b1af-49ca17f3567a)
>  Jenkins pipeline의 stages 구성을 나타내는 그림입니다.
>  Jenkins는 kubernetes에 올라가지 않으며 webhook을 사용하지 않고 수동으로 빌드합니다.

<br>

### Build and Deployment config-service
 구성 서버를 빌드하고 배포하는 stage 입니다. 

<details><summary>detail</summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/32555f21a0ba59b7eff0e3525253c08c4f4bcf0e/Jenkinsfile#L17-L35
 이 스테이지는 크게 3단계로 나누어 집니다.
<br><br>

```
sh "chmod +x gradlew"
sh "./gradlew clean"
sh "./gradlew build"
archiveArtifacts artifacts: "**/build/libs/*.jar", allowEmptyArchive: true
```
 spring project를 빌드하는 단계입니다. 이 단계에서 jar 파일을 추출합니다.
<br><br>

```
sh "docker build -t liardance/config-service:latest ./"
sh "docker push liardance/config-service:latest"
```
 도커 이미지를 생성하고 도커 허브에 push하는 단계입니다.
<br><br>

```
sh "kubectl --kubeconfig=/var/lib/jenkins/workspace/config apply -f k8s-config-service.yaml"
sh "kubectl --kubeconfig=/var/lib/jenkins/workspace/config rollout restart deployment config-deployment"
```
 도커 허브에 올라간 이미지로 kubernetes의 deployment로 서비스를 배포하는 단계입니다.
</details>

<br>

### Sleep
 구성 서버가 쿠버네티스에 올라가서 완전히 실행될 때까지 기다리는 stage 입니다.
<details><summary>detail</summary>
 
https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/32555f21a0ba59b7eff0e3525253c08c4f4bcf0e/Jenkinsfile#L37-L41
 구성서버는 다른 서버의 구성정보를 전파해야 하기 때문에 구성 서버가 완전히 로딩되지 않으면 다른 서버가 온전히 실행되지 않습니다.
</details>
<br>

### Build and Deployment other services
 인증, 피드, 채팅, 알림 서버를 빌드하고 배포하는 stage 입니다. 
<details><summary>detail</summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/32555f21a0ba59b7eff0e3525253c08c4f4bcf0e/Jenkinsfile#L43-L63
 이 스테이지 또한 크게 3단계로 나누어 집니다.
<br><br>

```
sh "chmod +x gradlew"
sh "./gradlew clean"
sh "./gradlew build"
archiveArtifacts artifacts: "**/build/libs/*.jar", allowEmptyArchive: true
```
 spring project를 빌드하는 단계입니다. 이 단계에서 jar 파일을 추출합니다.
<br><br>

```
sh "docker build -t liardance/${serv}-service:latest ./"
sh "docker push liardance/${serv}-service:latest"
```
 도커 이미지를 생성하고 도커 허브에 push하는 단계입니다.
<br><br>

```
sh "kubectl --kubeconfig=/var/lib/jenkins/workspace/config apply -f k8s-${serv}-service.yaml"
sh "kubectl --kubeconfig=/var/lib/jenkins/workspace/config rollout restart deployment ${serv}-deployment"
```
 도커 허브에 올라간 이미지로 kubernetes의 deployment로 서비스를 배포하는 단계입니다.
 해당 작업을 인증, 피드, 채팅, 알림서버가 반복하면서 서비스가 배포됩니다.
</details>
<br><br>

# Monitoring
![monitoring](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/60079483-b786-4c32-a97d-c838580107ab)
>  모니터링 환경의 구성요소를 나타내는 그림입니다.
>  Grafana와 Prometheus는 kubernetes에 올라가지 않습니다.

<br>

### spring microservices
각 서비스는 prometheus에 본인의 메트릭 정보를 전달하고 Grafana는 prometheus에서 얻은 서비스의 메트릭 정보를 시각화합니다.

<details><summary>detail</summary>
<br>
  
모든 스프링 서버는 다음과 같은 의존성을 가집니다.
``` java
implementation "org.springframework.boot:spring-boot-starter-actuator"
implementation 'io.micrometer:micrometer-core'
runtimeOnly 'io.micrometer:micrometer-registry-prometheus'
```
때문에 각 서비스들은 prometheus에 HTTP GET `serverIP:Port/actuator/prometheus`로 본인의 메트릭 정보를 전달할 수 있습니다.
<br><br>
  
![image](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/9ec58307-a644-4601-bedf-1019259e85b9)
위와 같이 prometheus로 전달된 메트릭 정보는 Grafana가 datasource로 연결하여 수집합니다.
<br><br>

![image](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/2be6f58b-aca5-4ce1-b9fd-2fa20b290a97)
수집한 데이터를 대시보드를 통해 시각화합니다.

<br><br>
![image](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/b20e083f-c632-4bed-9a29-e12d78cf731d)

  
</details>

<br>

### database
MongoDB는 [exporter](https://github.com/percona/mongodb_exporter)를 통해 본인의 메트릭 정보를 prometheus에 전달하고 Grafana는 prometheus에서 얻은 DB의 메트릭 정보를 시각화합니다.<br>
MySQL은 prometheus를 경유하지 않고 직접 Grafana와 TCP 연결을 해서 datasource를 구성합니다.

<details><summary>detail</summary>
  
<br>
  
MongoDB는 [exporter](https://github.com/percona/mongodb_exporter)를 사용해서 db의 메트릭 데이터를 prometheus에 HTTP GET `exporterIP:Port/metrics`로 전달할 수 있습니다.
<br><br>
    
![image](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/fa13e3c5-0f7d-473f-bd0d-fd89d4174876)
위와 같이 prometheus로 전달된 메트릭 정보는 Grafana가 datasource로 연결하여 수집합니다.
<br><br>
    
![image](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/2be6f58b-aca5-4ce1-b9fd-2fa20b290a97)
<br><br>
 
MySQL은 prometheus를 경유하지 않고 직접 Grafana와 TCP 연결을 해서 datasource를 구성합니다.
![image](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/3572c901-d754-454c-a33b-b53e3cf36460)
  
수집한 데이터를 대시보드를 통해 시각화합니다.
![image](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/d5d4c990-b06c-403e-a382-cfa8d1f9525b)

</details>
<br><br>
