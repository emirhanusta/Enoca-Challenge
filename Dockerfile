FROM openjdk:17-jdk-slim AS build

COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:resolve

COPY src src
RUN ./mvnw package

FROM openjdk:17-jdk-slim
WORKDIR enoca-challenge
COPY --from=build target/*.jar enoca-challenge.jar
ENTRYPOINT ["java", "-jar", "enoca-challenge.jar"]