# Use OpenJDK 21 as base image (matching your project config)
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle ./
COPY settings.gradle ./

# Make gradlew executable
RUN chmod +x ./gradlew

# Copy source code
COPY src ./src

# Build the application
RUN ./gradlew build -x test

# Expose port
EXPOSE 8080

# Create directories for uploads and tokens
RUN mkdir -p /app/uploads /app/tokens

# Copy built jar
RUN cp build/libs/*.jar app.jar

# Run the application
CMD ["java", "-jar", "app.jar"]
