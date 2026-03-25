# This image provides the OpenJDK 17 runtime required by the Spring Boot service.
FROM eclipse-temurin:17-jdk-jammy
# This sets the working directory inside the container.
WORKDIR /app
# This copies the already-built Spring Boot jar from the Maven target folder into the container.
COPY target/*.jar app.jar
# This documents that hotel-service listens on port 8081 inside the container.
EXPOSE 8081
# This starts the Spring Boot application when the container launches.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
