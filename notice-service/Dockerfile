FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/notice-service-dev.jar notice-service.jar

EXPOSE 8892

CMD ["java", "-jar", "notice-service.jar"]
