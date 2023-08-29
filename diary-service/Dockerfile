FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/diary-service-dev.jar diary-service.jar

EXPOSE 8891

CMD ["java", "-jar", "diary-service.jar"]
