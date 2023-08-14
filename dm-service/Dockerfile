FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/dm-service-dev.jar dm-service.jar

EXPOSE 8889

CMD ["java", "-jar", "dm-service.jar"]
