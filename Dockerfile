# Use OpenJDK base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven files and build
COPY . /app

# Build the application
RUN apt-get update && apt-get install -y maven \
    && mvn clean package -DskipTests

# Run the jar
CMD ["java", "-jar", "target/weather-forecast-web-1.0.0.jar"]
