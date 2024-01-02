<h1>SoSoHappy</h1> 
<img align="right" src="https://skillicons.dev/icons?i=gcp,kubernetes,docker,jenkins,prometheus,grafana,kafka,java,spring,mysql,mongodb">

![logo](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/f1c6642b-8b03-45ce-b260-8286cfdb54b0)

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

</details>

<br>

###  채팅 서버 
 다이렉트 메시지 송수신을 위한 서버입니다.
<details><summary>detail</summary>

<br>

![dm](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/89f198df-885b-4022-a3f5-2c71275a8b8c)

> 채팅 데이터가 어떻게 전달되는지 나타낸 그림입니다.<br>



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

- 첫 3줄은 [구성 정보](#topic--springcloudbus)를 전파받거나 메시지 큐를 이용해 [회원 탈퇴](#topic--resign)한 회원과의 세션을 끊기 위해 추가되었습니다.
- 이후 3줄은 metric 데이터를 수집하여 [모니터링](#spring-microservices) 하기 위해 추가하였습니다.
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
  
https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/986a587b1ea756378f4026cf2317c5ee72a824f0/notice-service/src/main/java/sosohappy/noticeservice/kafka/KafkaConsumer.java#L38-L59
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

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/986a587b1ea756378f4026cf2317c5ee72a824f0/auth-service/src/main/java/sosohappy/authservice/kafka/KafkaProducerAspect.java#L22-L44
메소드가 에러없이 성공적으로 실행되면 Spring AOP의 `@AfterReturning` 애노테이션을 통해 인자 및 반환값을 가져온 후 email, access token을 byte array 형태로 브로커에 전송합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/986a587b1ea756378f4026cf2317c5ee72a824f0/dm-service/src/main/java/sosohappy/dmservice/kafka/KafkaConsumer.java#L18-L25
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

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/986a587b1ea756378f4026cf2317c5ee72a824f0/dm-service/src/main/java/sosohappy/dmservice/kafka/KafkaConsumer.java#L35-L43
https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/5913ded93c409c9b7a79f1fa72d4529ae692b6e5/dm-service/src/main/java/sosohappy/dmservice/service/MessageService.java#L44-L49
채팅 서버나 알림 서버에선 탈퇴한 유저와의 세션 연결을 끊습니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/986a587b1ea756378f4026cf2317c5ee72a824f0/feed-service/src/main/java/sosohappy/feedservice/kafka/KafkaConsumer.java#L34-L42
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

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/986a587b1ea756378f4026cf2317c5ee72a824f0/feed-service/src/main/java/sosohappy/feedservice/kafka/KafkaProducerAspect.java#L19-L30
 메소드가 에러없이 성공적으로 실행되면 Spring AOP의 `@AfterReturning` 애노테이션을 통해 인자 및 반환값을 가져옵니다.<br>
 이후 좋아요를 누른 유저의 닉네임과 피드 날짜, 피드 게시자의 닉네임을 byte array 형태로 브로커에 전송합니다.
<br><br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/986a587b1ea756378f4026cf2317c5ee72a824f0/notice-service/src/main/java/sosohappy/noticeservice/kafka/KafkaConsumer.java#L38-L59
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
