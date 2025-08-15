# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /build

# Copy entire project
COPY . .

# Build notification service and its dependencies
RUN mvn clean package -DskipTests -B -pl notification-service/notification-controller -am

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /build/notification-service/notification-controller/target/notification-controller-*.jar app.jar

# Expose port
EXPOSE 9994

ENTRYPOINT ["java", "-jar", "app.jar"]