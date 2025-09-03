<h1>SoSoHappy</h1> 
<img align="right" src="https://skillicons.dev/icons?i=gcp,kubernetes,docker,jenkins,prometheus,grafana,kafka,java,spring,mysql,mongodb">


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
  + [topic : noticeLike](#topic--noticelike)
  + [topic : deviceToken](#topic--deviceToken)
  + [topic : directMessage](#topic--directMessage)
  + [topic : resign](#topic--resign)
  + [topic : expired](#topic--expired)
  + [topic : emailAndNickname](#topic--emailAndNickname)
- [CI/CD](#cicd)
  + [build and deployment config-service](#build-and-deployment-config-service)
  + [sleep](#sleep)
  + [build and deployment other services](#build-and-deployment-other-services)
- [Monitoring](#monitoring)
  + [spring microservices](#spring-microservices)
  + [database](#database)

<br>
  
# Microservices
![microservices](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/48ddbdd3-39b8-4c80-9545-6a99e1b5f2d1)

> 프로젝트의 서버단에 포함된 서비스들의 구조를 나타내는 그림입니다.<br><br>
> 구성, 인증, 피드, 채팅, 알림서버는 Rolling update 및 ReplicaSet 생성을 정의하기 위해 Deployment로 앱을 배포하였습니다.<br><br>
> Mysql, MongoDB 서버는 마운트한 폴더를 지속적으로 사용하게 위해 StatefulSet으로 앱을 배포하였습니다.<br><br>
> Grafana, Prometheus, Kafka는 워커노드 메모리 이슈로 쿠버네티스에 올리지 않고 VM 내부에서 도커 컨테이너로 실행하였으며 Jenkins는 VM 위에 직접 올라갑니다.


<br>

###  구성 서버 
최신 구성 정보(property)를 타겟 서버에 전파하기 위해 구현한 서버입니다.
<details><summary>detail</summary>
<br>

![config](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/74f4df60-973c-4a3f-adb1-e32944429f7a)

> 구성 서버의 역할을 나타낸 그림입니다.

<br>

다른 비즈니스 서비스들이 공개 가능한 property를 가진 상태에서 실행되면 구성 서버가 비공개 property를 비즈니스 서비스들에 주입합니다.<br><br>
전파되는 property 정보들은 외부에 노출되면 안되는 내용을 포함하기 때문에 [서브모듈](https://github.com/So-So-Happy/SoSoHappy-BackEnd/tree/master/config-service)에 모아두고 관리합니다.
</details>

<br>

###  인증 서버 
로그인, 회원탈퇴 등 유저 정보 관련 작업을 수행하는 서버입니다.
<details><summary>detail</summary>
<br>

![auth](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/610deaf7-db95-49e4-858d-be3b2541e1c6)

> 인증 서버의 주요 기능인 회원가입(로그인) 과정을 나타낸 그림입니다.

<br>

1. Client가 랜덤한 문자열 codeVerifier를 생성합니다.<br>
2. 이를 SHA512에 적용하여 나온 값을 codeChallenge로 설정합니다.<br>
3. 이 codeChallenge를 `/getAuthorizeCode` 호출 시 파라미터로 전달합니다.<br>
4. 서버에서 랜덤한 문자열 authorizeCode를 생성 후 {authorizeCode, codeChallenge} 쌍을 HashMap에 저장합니다.<br>
5. 이 authorizeCode를 Client에서 전달받습니다.<br>
6. Client가 OAuth2 공급자에게서 유저 정보(이메일)을 받아옵니다.<br>
7. 받아온 유저 정보, authorizeCode, codeVerifier를 `/signIn` 호출 시 파라미터로 전달합니다.<br>
8. 파라미터로 넘어온 authorizeCode로 서버에 저장된 HashMap에서 codeChallenge를 가져온 후 codeChallenge = SHA512(codeVerifier)인지 검사합니다.<br>
9. 성공 시 유저 정보를 저장 후 액세스토큰, 리프레시토큰을 반환합니다.<br>

<br>

이 방식은 OAuth2의 [PKCE](https://oauth.net/2/pkce/) 동작 방식을 일부 카피하였습니다.

</details>
  
<br>

###  피드 서버 
피드 추가, 수정, 삭제, 추천 등의 작업을 수행하는 서버입니다.
<details><summary>detail</summary>

<br>

![feed](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/dc1df2ce-cdb8-4b61-9150-783949344c0d)

<br>피드 관련 CRUD API가 구현된 서버입니다.<br>
Controller, Service, Repository 총 3개의 Layer로 분리되어 있습니다.<br>

아래는 Controller단의 코드 일부입니다.

```java
@RestController
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final FeedImageRepository feedImageRepository;

    @PostMapping("/findMonthFeed")
    public List<UserFeedDto> findMonthFeed(@ModelAttribute @Valid NicknameAndDateDto nicknameAndDateDto){
        return feedService.findMonthFeed(nicknameAndDateDto);
    }

    @PostMapping("/findDayFeed")
    public UserFeedDto findDayFeed(@ModelAttribute @Valid NicknameAndDateDto nicknameAndDateDto){
        return feedService.findDayFeed(nicknameAndDateDto);
    }

    ...

}
```
Controller단에서 Request로 들어온 DTO를 Validation한 후 이상이 없으면 Service단의 메소드를 호출합니다.<br>

아래는 Service단의 일부입니다.
```java
@Service
@RequiredArgsConstructor
@Transactional
public class FeedService {

    private final FeedRepository feedRepository;
    private final FeedLikeNicknameRepository feedLikeNicknameRepository;
    private final FeedImageRepository feedImageRepository;
    private final HappinessService happinessService;
    private final KafkaDelegator kafkaDelegator;

    public List<UserFeedDto> findMonthFeed(NicknameAndDateDto nicknameAndDateDto) {
        return Optional.ofNullable(feedRepository.findMonthFeedDtoByNicknameAndDateDto(nicknameAndDateDto))
                .orElse(List.of());
    }

    public Map<String, Boolean> updateLike(String srcNickname, NicknameAndDateDto nicknameAndDateDto) {
        return feedRepository.findByNicknameAndDate(nicknameAndDateDto.getNickname(), nicknameAndDateDto.getDate())
                .map(feed ->  {
                    Map<String, Boolean> responseDto = Map.of("like", updateLike(feed, srcNickname));
                    if(responseDto.get("like")){
                        kafkaDelegator.produceUpdateLike(srcNickname, nicknameAndDateDto);
                    }
                    return responseDto;
                })
                .orElseThrow(NotFoundException::new);
    }

    ...

}
```
Service단에선 필요에 따라 Kafka와 통신하거나 Repository단을 호출하여 피드 CRUD를 위한 메소드를 실행합니다.

아래는 Repository단의 일부입니다.
```java
@Repository
@RequiredArgsConstructor
public class FeedQueryRepositoryImpl implements FeedQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserFeedDto> findMonthFeedDtoByNicknameAndDateDto(NicknameAndDateDto nicknameAndDateDto) {
        return queryFactory
                .selectFrom(feed)
                .leftJoin(feed.feedImages, feedImage)
                .leftJoin(feed.feedCategories, feedCategory)
                .leftJoin(feed.feedLikeNicknames, feedLikeNickname)
                .where(
                        monthEq(nicknameAndDateDto.getDate()),
                        nickNameEq(nicknameAndDateDto.getNickname())
                )
                .orderBy(feed.date.desc())
                .transform(
                        groupBy(feed.id).list(
                                Projections.constructor(
                                        UserFeedDto.class,
                                        feed,
                                        list(Projections.constructor(Long.class, feedImage.id)),
                                        list(Projections.constructor(FeedCategory.class, feed, feedCategory.category)),
                                        list(Projections.constructor(FeedLikeNickname.class, feed, feedLikeNickname.nickname))
                                )
                        )
                );
    }

    ...

}
```
```java

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedQueryRepository {

    @Modifying
    @Query("update Feed f set f.nickname = :after where f.nickname = :before")
    void updateFeedNickname(@Param("before") String before, @Param("after") String after);

    @Modifying
    void deleteByNickname(String nickname);

    ...

}
```
Spring data JPA, Querydsl을 의존성 추가하여 사용합니다. 이곳에서 피드 CRUD 작업을 수행 후 결과를 json으로 Client에 반환합니다.<br>
피드서버의 다른 API들도 위와 비슷한 과정을 거쳐 Client에 결과를 반환합니다.

</details>

<br>

###  채팅 서버 
 다이렉트 메시지 송수신을 위한 서버입니다.
<details><summary>detail</summary>

<br>

![dm](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/c3a8bfd6-b0b9-4b9c-85f7-eeb2e1b84c58)

> 채팅 데이터가 어떻게 전달되는지 나타낸 그림입니다.

<br>

Client에서 앱을 킨 후 채팅방 목록 탭을 누르면 서버와 WebSocket 연결이 이뤄지고, 채팅방 목록 탭에서 나가면 WebSocket 연결이 끊깁니다.<br><br>
따라서 채팅을 보내는 유저는 반드시 WebSocket 연결이 이루어 진 상태입니다.<br><br>
채팅을 받는 유저가 채팅방 목록 탭을 누른 상태이거나 채팅방에 입장한 상태이면 WebSocket 연결상태 이므로 푸시알림을 받지 않고 데이터를 전송받습니다.<br><br>
채팅을 받는 유저가 채팅방 미입장, 앱 백그라운드 실행 등 WebSocket 미연결 상태일 경우 Kafka로 채팅 데이터를 전송 후 알림서버에서 FCM을 통해 푸시알림을 전송합니다.

</details>
<br>

###  알림 서버 
푸시 알림 전송을 위한 서버입니다.
<details><summary>detail</summary>
<br>

![apns](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/a207c550-2123-4d5e-b637-7ef5bcaa6603)

> 알림 서버의 주요 기능을 나타낸 그림입니다.

<br>

알림서버는 유저에게 푸시알림을 보내는 기능을 구현한 서버입니다.<br><br>
푸시알림 데이터는 현재 채팅 데이터, 좋아요 알림 데이터 2가지가 있으며 kafka에서 해당 데이터를 불러온 후 [FCM](https://firebase.google.com/docs/cloud-messaging?hl=ko)에 이 데이터를 전달합니다.<br>
전달 된 데이터는 [APNs](https://developer.apple.com/documentation/usernotifications/setting_up_a_remote_notification_server/sending_notification_requests_to_apns)에 전달되어 유저는 푸시알림을 수신할 수 있습니다. 

</details>

<br>

# Load Balancer
![loadbalancer](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/5fb7539a-a459-4974-9b95-80fc27613824)

> 트래픽을 서비스들에게 라우팅하기 위한 Load Balancer의 트래픽 경로를 나타낸 그림입니다.<br><br>
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
![kafka](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/f84a5cab-af4c-43e7-b466-bccad935d742)

> 프로젝트의 서버단에서 사용된 미들웨어 Kafka의 토픽 및 pub/sub 구조를 대략적으로 나타낸 그림입니다.

<br>

### topic : springCloudBus
구성서버가 각 서비스들의 구성 정보(properties, yml)를 전파하기 위해 사용되는 토픽입니다.<br>
각 서비스들은 구성 서버가 실행중이라면 이 토픽의 메시지를 수신해서 구성 정보를 등록할 수 있고, 혹은 `/actuator/busrefresh`로 구성 서버에서 원격 업데이트 할 수 있습니다.

<br>

### topic : accessToken
인증서버가 Email, Access Token 정보를 전파하기 위해 사용되는 토픽입니다.<br>
인증이 필요한 서비스들은 JWT 관련 의존성을 추가하지 않고 해당 토픽의 메시지를 수신해서 토큰 유효성을 검사합니다.

<br>

### topic : noticeLike
 피드에 좋아요를 눌렀을 때 해당 회원 정보를 전파하기 위한 토픽입니다.

<br>

### topic : deviceToken
 회원가입, 로그인 시 FCM deviceToken 데이터를 전파하기 위한 토픽입니다.<br>
 이 deviceToken은 FCM에 푸시알림 데이터를 보낼 때 기기 정보를 설정하기 위해 사용됩니다.

<br>

### topic : directMessage
 채팅 데이터를 전파하기 위한 토픽입니다.<br>
 채팅서버에서 알림서버로 전달된 이 데이터는 FCM을 통해 유저에게 푸시알림을 보낼 때 사용됩니다.

<br>

### topic : resign
 인증서버가 탈퇴한 회원 정보를 전파하기 위한 토픽입니다.<br>
 피드서버와는 데이터 정합성을 맞추고, 채팅서버 및 알림서버는 연결된 WebSocket Session을 끊기 위해 사용됩니다. 

<br>

### topic : expired
 access token이 만료되었을 때 해당 정보를 전파하기 위한 토픽입니다.<br>
 피드 서버나 채팅 서버에서 이 데이터를 수신하면 가지고 있던 인증 정보를 파기합니다.

<br>

### topic : emailAndNickname
 유저의 닉네임이 변경되었을 때 해당 정보를 전파하기 위한 토픽입니다.<br>
 인증 서버와 피드 서버가 다른 DB를 사용하기 때문에 연관관계 설정이 없으므로 닉네임 변경 시 피드 서버쪽의 DB에서 직접적인 수정이 필요합니다.

</details>

<br>

# CI/CD
![cicd](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/e61a98fe-9512-437c-9060-22fe264a758b)
>  Jenkins pipeline의 stages 구성을 나타내는 그림입니다.
>  Jenkins는 kubernetes에 올라가지 않으며 webhook을 사용하지 않고 수동으로 빌드합니다.

<br>

### Build and Deployment config-service
 구성 서버를 빌드하고 배포하는 stage 입니다. 

<details><summary>detail</summary>
  <br>
  
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
  <br>
  
 ```java
 stage('Waiting config pod running') {
            steps {
                sleep(time: 120, unit: 'SECONDS')
            }
        }
```
 구성서버는 다른 서버의 구성정보를 전파해야 하기 때문에 구성 서버가 완전히 로딩되지 않으면 다른 서버가 온전히 실행되지 않습니다.
</details>
<br>

### Build and Deployment other services
 인증, 피드, 채팅, 알림 서버를 빌드하고 배포하는 stage 입니다. 
<details><summary>detail</summary>
  <br>

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
![monitoring](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/a5a14486-e61c-4716-986f-39f39bf64318)

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
