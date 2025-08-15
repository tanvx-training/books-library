# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /build

# Copy entire project
COPY . .

# Build only the loan service and its dependencies
RUN mvn clean package -DskipTests -B -pl loan-service/loan-controller -am

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /build/loan-service/loan-controller/target/loan-controller-*.jar app.jar

# Expose application port
EXPOSE 9995

ENTRYPOINT ["java", "-jar", "app.jar"]