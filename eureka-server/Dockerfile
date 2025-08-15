# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /build

# Copy entire project
COPY . .

# Build only the eureka server
RUN mvn clean package -DskipTests -B -pl eureka-server -am

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /build/eureka-server/target/eureka-server-*.jar app.jar

# Expose application port
EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]