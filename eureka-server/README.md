# Eureka Server

This is a Spring Cloud Netflix Eureka Server for service discovery in microservices architecture.

## Configuration

The server is configured with the following properties:

- Server port: 8761 (default Eureka port)
- Self-registration disabled: The server won't register itself as a client
- Registry fetching disabled: The server won't fetch registry information from other Eureka servers
- Self-preservation mode disabled: Enables faster detection of service unavailability

## How to Use

### Starting the Server

1. Run the application using Maven:
   ```
   cd eureka-server
   mvn spring:run
   ```

2. Or build and run as a JAR:
   ```
   mvn clean package
   java -jar target/eureka-server-0.0.1-SNAPSHOT.jar
   ```

### Accessing the Dashboard

Once running, you can access the Eureka dashboard at:
```
http://localhost:8761
```

### Registering Services with Eureka

For client services to register with this Eureka server:

1. Add the required dependencies to the client service:
   ```xml
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
   </dependency>
   ```

2. Add the `@EnableEurekaClient` annotation to the client service's main application class.

3. Configure the client service's application.properties/yaml:
   ```yaml
   eureka:
     client:
       service-url:
         defaultZone: http://localhost:8761/eureka/
   ```

## Security Considerations

This implementation does not include security features. For production use, consider:

1. Enabling HTTPS
2. Adding authentication
3. Configuring a proper network setup with firewalls 