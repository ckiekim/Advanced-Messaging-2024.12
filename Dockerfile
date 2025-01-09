# Base image
FROM eclipse-temurin:17-jdk-alpine
# Set the working directory
WORKDIR /app
# Copy the application JAR file
COPY target/ws-0.0.1-SNAPSHOT.jar app.jar
# Start the application
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]