FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/dm-service.jar dm-service.jar

EXPOSE 8889

CMD ["java", "-jar", "app.jar"]
