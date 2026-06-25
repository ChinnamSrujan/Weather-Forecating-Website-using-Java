# Multi-stage build for smaller image size
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/weather-forecast-web-1.0.0.jar app.jar

# Render dynamically assigns PORT
EXPOSE 8081
ENV PORT=8081

CMD ["sh", "-c", "java -Dserver.port=${PORT} -Xmx512m -jar app.jar"]
