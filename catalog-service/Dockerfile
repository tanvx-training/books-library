# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /build

# Copy entire project
COPY . .

# Build only the catalog service and its dependencies
RUN mvn clean package -DskipTests -B -pl catalog-service/catalog-controller -am

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /build/catalog-service/catalog-controller/target/catalog-controller-*.jar app.jar

# Expose application port
EXPOSE 9991

ENTRYPOINT ["java", "-jar", "app.jar"]