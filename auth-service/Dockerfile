FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/auth-service-dev.jar auth-service.jar

EXPOSE 8890

CMD ["java", "-jar", "auth-service.jar"]
