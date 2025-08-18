FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /usr/holo-bot

COPY pom.xml .
COPY src ./src
RUN mvn clean compile assembly:single

FROM openjdk:25-slim-bullseye
WORKDIR /usr/holo-bot
COPY --from=build /usr/holo-bot/target/*.jar .
COPY config ./config
CMD ["java", "-jar", "Holo_Bot.jar"]