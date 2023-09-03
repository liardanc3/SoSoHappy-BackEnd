FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/feed-service-dev.jar feed-service.jar

EXPOSE 8891

CMD ["java", "-jar", "feed-service.jar"]
