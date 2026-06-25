# Multi-stage build for smaller image size
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage - use jammy (Ubuntu) not alpine for better compatibility
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/weather-forecast-web-1.0.0.jar app.jar

# Render assigns PORT dynamically - default to 10000 (Render's default)
EXPOSE 10000
ENV PORT=10000
ENV JAVA_OPTS="-Xmx400m -Xms200m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar"]
