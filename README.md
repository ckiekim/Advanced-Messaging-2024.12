# Advanced Messaging Lecture/Project (2024.12)

### 실시간 메세징 강좌
- Web Socket/SSE
- STOMP, RabbitMQ
- 채팅
- 주가정보(한국투자증권 KIS Open API)

#### Java Back-end School Plus 과정

멋쟁이사자처럼(Like Lion)에서 2024년12월부터 2025년2월까지 진행됩니다.

### AWS에서 실행하는 방법
<pre>
# build 및 이미지 생성
mvn clean package
docker build -t ckiekim/ws-app .

# 이미지 push & pull
docker push ckiekim/ws-app (Local)
docker pull ckiekim/ws-app (Remote)

# RabbitMQ 실행
docker ps -a
docker rm rabbitmq
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=guest -e RABBITMQ_DEFAULT_PASS=guest rabbitmq:management
docker exec -it rabbitmq rabbitmq-plugins enable rabbitmq_web_stomp

# ws-app 실행
docker rm ws-app
docker run -d --name ws-app -e SPRING_PROFILES_ACTIVE=prod -p 8080:8080 ckiekim/ws-app

# 두 컨테이너를 동일한 네트워크에 연결
docker network create ws-network
docker network connect ws-network rabbitmq
docker network connect ws-network ws-app

# 사용자 등록 및 종목 등록
http://43.203.66.227:8080/user/register
http://43.203.66.227:8080/kis/initData
</pre>
