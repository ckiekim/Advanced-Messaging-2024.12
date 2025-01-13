# Base image
FROM eclipse-temurin:17-jdk-alpine
# Set the working directory
WORKDIR /app
# Copy the application JAR file
COPY target/ws-0.0.1-SNAPSHOT.jar app.jar
# Expose the port
EXPOSE 8080
# Start the application
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:prod}", "-jar", "app.jar"]