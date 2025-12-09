## Multi-stage Dockerfile for Spring Boot Application
#
## Stage 1: Build
#FROM maven:3.9-eclipse-temurin-23-alpine AS build
#WORKDIR /app
#
## Copy pom.xml and download dependencies (cached layer)
#COPY /pom.xml .
#RUN mvn dependency:go-offline -B
#
## Copy source code and build
#COPY /src/ ./src
#
#RUN mvn clean package -Pdocker -DskipTests
#
#
#
## Stage 2: Runtime
#FROM eclipse-temurin:23-jre-alpine as runner
#WORKDIR /app
#
## Create non-root user for security
#RUN addgroup -S spring && adduser -S spring -G spring
#USER spring:spring
#
## Copy JAR from build stage
#COPY --from=build /app/target/gallery-*.jar app.jar
#
#
## Expose port
#EXPOSE 8080
#
## Health check
#HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
#  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1
#
## Run application
#ENTRYPOINT ["java", "-jar", "/app/app.jar"]
#CMD ["--spring.profiles.active=docker"]




FROM eclipse-temurin:23-jre-alpine as runner
EXPOSE 8080
ARG JAR_FILE=target/gallery-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
CMD ["--spring.profiles.active=docker"]