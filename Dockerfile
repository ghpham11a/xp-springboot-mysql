# -----------------------------------------------------
# Stage 1: Build the application using Gradle + JDK 21
# -----------------------------------------------------
FROM gradle:8.3.0-jdk17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy Gradle wrapper files (if you use them), settings, and build files first
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Copy the source code
COPY src ./src

# Make sure the Gradle wrapper is executable (if it isn't already)
RUN chmod +x gradlew

# Build the application (the 'bootJar' task creates the runnable jar;
# you can skip tests with '-x test' for faster builds if desired)
RUN ./gradlew bootJar --no-daemon

# -----------------------------------------------------
# Stage 2: Create the production-ready runtime image
# -----------------------------------------------------
FROM amazoncorretto:21

# Set the working directory
WORKDIR /app

# Copy the Spring Boot JAR file from the builder stage to the runtime image
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Define the startup command
ENTRYPOINT ["java", "-jar", "app.jar"]