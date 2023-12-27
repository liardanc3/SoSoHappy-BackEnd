FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/monolithic-2.0.jar monolithic-2.0.jar

EXPOSE 8888

CMD ["java", "-jar", "monolithic-2.0.jar"]
