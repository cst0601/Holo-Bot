FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /usr/holo-bot

COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

FROM openjdk:26-ea-slim
WORKDIR /usr/holo-bot
COPY --from=build /usr/holo-bot/target/*.jar .
COPY config ./config
CMD ["java", "-jar", "Holo_Bot.jar"]