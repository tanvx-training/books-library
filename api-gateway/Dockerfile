# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /build

# Copy entire project
COPY . .

# Build only the api gateway
RUN mvn clean package -DskipTests -B -pl api-gateway -am

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /build/api-gateway/target/api-gateway-*.jar app.jar

# Expose application port
EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]