#
# Build stage
#
FROM gradle:8.10-jdk21 AS build
WORKDIR /home/gradle/src
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar --no-daemon

#
# Package stage
#
FROM openjdk:21
ARG JAR_FILE=/home/gradle/src/build/libs/*.jar
COPY --from=build ${JAR_FILE} app.jar
EXPOSE ${PORT}
ENTRYPOINT ["java","-jar","/app.jar","--server.port=${PORT}"]
