#
# Build stage
#
FROM gradle:8.8-jdk21 AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN gradle clean bootJar --no-daemon

#
# Package stage
#
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
