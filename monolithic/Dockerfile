FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/monolithic.jar monolithic.jar

EXPOSE 8888

CMD ["java", "-jar", "monolithic.jar"]
