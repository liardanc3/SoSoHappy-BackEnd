# SoSoHappy
<img src="https://img.shields.io/badge/Google Cloud Platform-4285F4?style=flat&logo=Google Cloud&logoColor=white"/> <img src="https://img.shields.io/badge/kubernetes-326CE5?style=flat&logo=kubernetes&logoColor=white"/> <img src="https://img.shields.io/badge/Docker-2496ED?style=flat&logo=Docker&logoColor=white"/> <img src="https://img.shields.io/badge/Git-F05032?style=flat&logo=Git&logoColor=white"/> <img src="https://img.shields.io/badge/Github-181717?style=flat&logo=Github&logoColor=white"/> <img src="https://img.shields.io/badge/Jenkins-D24939?style=flat&logo=Jenkins&logoColor=white"/> <img src="https://img.shields.io/badge/Prometheus-E6522C?style=flat&logo=prometheus&logoColor=white"/> <img src="https://img.shields.io/badge/Grafana-F46800?style=flat&logo=grafana&logoColor=white"/> <img src="https://img.shields.io/badge/Apache Kafka-231F20?style=flat&logo=apachekafka&logoColor=white"/> <img src="https://img.shields.io/badge/MariaDB-003545?style=flat&logo=mariadb&logoColor=white"/> <img src="https://img.shields.io/badge/MongoDB-47A248?style=flat&logo=mongodb&logoColor=white"/> <img src="https://img.shields.io/badge/Spring Data JPA-6DB33F?style=flat&logo=Databricks&logoColor=white"> <img src="https://img.shields.io/badge/Spring Data MongoDB-6DB33F?style=flat&logo=Databricks&logoColor=white"> <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat&logo=Spring Boot&logoColor=white"/> <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=flat&logo=springsecurity&logoColor=white"/> <img src="https://img.shields.io/badge/Spring WebFlux-6DB33F?style=flat&logo=spring&logoColor=white"/><br><br>
![sosohappy-icon-removebg-preview](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/6c3db330-b80f-4837-b286-95f45d0cd6ae)
- - -
###### 이 문서는 [클라이언트](https://github.com/So-So-Happy/SoSoHappy-iOS) 3명, 서버 1명 총 4명의 팀원이 진행한 SNS 성격의 iOS 앱 SoSoHappy의 서버단 구조 및 기능을 설명합니다.
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
- [Monitoring](#monitoring)
- [CI/CD](#cicd)
<br>
  
# Microservices
![microservices](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/cc7f1911-f5f6-42d9-87ba-7fc379de7e93)

> ###### 프로젝트의 서버단에 포함된 서비스들의 구조를 나타내는 그림입니다.<br><br>
> ###### 구성, 인증, 피드, 채팅, 알림서버는 Rolling update 및 ReplicaSet 생성을 정의하기 위해 Deployment로 앱을 배포하였습니다.<br>
> ###### Mysql, MongoDB 서버는 마운트한 폴더를 지속적으로 사용하게 위해 StatefulSet으로 앱을 배포하였습니다.<br><br>
> ###### Grafana, Prometheus, Jenkins, Kafka는 워커노드 메모리 이슈로 쿠버네티스에 올리지 않고 VM 내부에서 도커 컨테이너로 실행하였습니다.

<br>

###  구성 서버 
###### 외부에 노출시키면 안되는 property 파일들을 [서브모듈](https://github.com/So-So-Happy/SoSoHappy-BackEnd/tree/master/config-service)에 모아두고 타겟 서버에 전파하기 위해 구현한 서버입니다.
<details><summary>
  
###### 자세히
 </summary>

내용
  
</details>

<br>

###  인증 서버 
###### 소셜 로그인 및 유저 정보 관련 작업을 수행하는 서버입니다.
<details><summary>
  
###### 자세히
 </summary>

내용
  
</details>

<br>

###  피드 서버 
###### 피드 추가, 수정, 삭제, 추천 등의 작업을 수행하는 서버입니다.
<details><summary>
  
###### 자세히
 </summary>

내용
  
</details>

<br>

###  채팅 서버 
###### 다이렉트 메시지 송수신을 위한 서버입니다.
<details><summary>
  
###### 자세히
 </summary>

내용
  
</details>
<br>

###  알림 서버 
###### 푸시 알림 전송을 위한 서버입니다.
<details><summary>
  
###### 자세히
 </summary>

내용
  
</details>

<br>

# Load Balancer
![loadbalancer](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/d25c9821-b53b-49bb-8acb-06afd2ba599e)

> ###### 트래픽을 서비스들에게 라우팅하기 위한 Load Balancer의 구조를 나타낸 그림입니다.<br><br>
> ###### GCP 환경에서 SSL 인증서를 사용하기 위해 HTTPS Load balancer를 사용했지만, 모든 백엔드 서비스들이 1개씩만 존재하므로 로드밸런서의 부하 분산기능은 사용되지 않고 라우팅 기능만 사용되었습니다.

<br>

### frontend
![image](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/6539df7c-babd-450c-9fec-1427a0350ee3)
![image](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/a1cdc1ff-8390-4256-90a1-adaabd19c52b)

###### frontend는 마스터 노드가 설치된 VM으로 가는 트래픽을 정의합니다. 
###### SSL 인증서를 사용중이기 때문에 443포트를 사용했고, http 80포트로 들어오는 요청은 https 443으로 redirect 됩니다.

<br>

### backend
![image](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/b75f9601-2f2c-4c53-a230-5e47532ef1b1)

###### 백엔드는 네트워크 엔드포인트 그룹으로 구성되어 있습니다.
###### 433 포트를 통해 들어온 트래픽은 위와 같은 규칙으로 VM에 직접 라우팅됩니다. 각각 8888~8892번의 포트를 가지고 있습니다.

![image](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/e33b23d4-7e11-4d2a-b8ed-ec82e7dbe329)
###### 라우팅 된 트래픽은 LoadBalancer로 정의된 쿠버네티스의 서비스에 들어갑니다.

<br>

# Message Queue
![messagequeue](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/aba78396-19f2-40c2-b3f4-44ec31b50c5a)

> ###### 프로젝트의 서버단에서 사용된 미들웨어 Kafka의 토픽 및 pub/sub 구조를 나타내는 그림입니다. 
> ###### 싱글 브로커로 구성 되어 있으며 실선은 publish, 점선은 subscribe를 의미합니다.

<br>

### topic : springCloudBus
###### 구성서버가 각 서비스들의 구성 정보(properties, yml)를 전파하기 위해 사용되는 토픽입니다.
###### 각 서비스들은 구성 서버가 실행중이라면 이 토픽의 메시지를 수신해서 구성 정보를 등록할 수 있고, 혹은 `/actuator/busrefresh`로 구성 서버에서 원격 업데이트 할 수 있습니다.

<br>

### topic : accessToken
###### 인증서버가 Email, Access Token 정보를 전파하기 위해 사용되는 토픽입니다.
###### 인증이 필요한 서비스들은 JWT 관련 의존성을 추가하지 않고 해당 토픽의 메시지를 수신해서 토큰 유효성을 검사합니다.

<details><summary>
  
###### 자세히
 </summary>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/40e07af63b88a420e570178f97597584c7c70b7b/auth-service/src/main/java/sosohappy/authservice/jwt/service/JwtService.java#L39-L46
###### 토큰엔 주제와 만료기간과 함께 유저의 이메일이 claim으로 포함됩니다.
###### 커스텀 애노테이션 `@KafkaProducer` 는 createAccessToken 메소드의 인자 및 반환값으로 전파할 메시지를 설정합니다.

<br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/40e07af63b88a420e570178f97597584c7c70b7b/auth-service/src/main/java/sosohappy/authservice/kafka/KafkaProducerAspect.java#L10-L27
###### 메소드가 에러없이 성공적으로 실행되면 Spring AOP의 `@AfterReturning` 애노테이션을 통해 인자 및 반환값을 가져온 후 email, access token을 byte array 형태로 브로커에 전송합니다.

<br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/40e07af63b88a420e570178f97597584c7c70b7b/dm-service/src/main/java/sosohappy/dmservice/kafka/KafkaConsumer.java#L10-L25
###### 인증이 필요한 다른 서비스에서 해당 메시지를 수신해서 key, value 쌍으로 관리합니다. 이 문서에선 채팅 서버를 예시로 사용합니다.

<br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/40e07af63b88a420e570178f97597584c7c70b7b/dm-service/src/main/java/sosohappy/dmservice/jwt/service/JwtService.java#L17-L21
###### 필터에 포함되는 함수입니다. HTTP 요청에 포함된 헤더에서 Email, AccessToken을 추출하고 이 값이 브로커에게 전달받은 Email, AccessToken과 일치하는지 확인합니다.

<br>

https://github.com/So-So-Happy/SoSoHappy-BackEnd/blob/40e07af63b88a420e570178f97597584c7c70b7b/dm-service/src/main/java/sosohappy/dmservice/jwt/filter/JwtFilter.java#L20-L29
###### 일치한다면 다음 작업을 수행하고 그렇지 않다면 403 코드와 함께 다음 작업을 수행하지 않고 반환합니다. 모니터링을 위해 "/actuator"가 경로에 포함되는 경우는 인증과정이 생략됩니다.  

</details>

<br>

### topic : resign

<br>

### topic : noticeLike

<br>

# CI/CD
![cicd](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/7e6d4bf0-6d35-4a84-b1af-49ca17f3567a)
설명.

<br>

# Monitoring
![monitoring](https://github.com/So-So-Happy/SoSoHappy-BackEnd/assets/85429793/c0684412-8fe0-462e-868d-30522ca77800)

설명.

