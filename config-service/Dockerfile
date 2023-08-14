FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/config-service-dev.jar config-service.jar

EXPOSE 8888

CMD ["java", "-jar", "config-service.jar"]
