# Hệ Thống Logging AOP - Hướng Dẫn Triển Khai

## Tổng Quan

Hệ thống logging AOP đã được triển khai đầy đủ với 3 mức độ thông tin:
- **Basic**: Thông tin cơ bản (INFO, ERROR)
- **Detailed**: Thông tin chi tiết (DEBUG, WARN) với tham số và kết quả trả về
- **Advanced**: Thông tin nâng cao (TRACE) với distributed tracing và custom tags

## Cấu Trúc Components

### 1. Core Components
```
common-module/src/main/java/com/library/common/
├── constants/
│   └── LoggingConstants.java           # Constants và MDC keys
├── enums/
│   ├── LogLevel.java                   # Enum mức độ logging
│   └── OperationType.java              # Enum loại operation
├── utils/logging/
│   ├── MDCContextManager.java          # Quản lý MDC context
│   ├── DataSanitizer.java             # Ẩn thông tin nhạy cảm
│   └── LoggingContextManager.java     # Quản lý context và sampling
├── aop/
│   ├── annotation/
│   │   └── Loggable.java              # Annotation cho AOP
│   ├── LoggingAspect.java             # Main AOP aspect
│   ├── HttpLoggingAspect.java         # HTTP request/response logging
│   └── filter/
│       └── LoggingFilter.java         # HTTP filter
└── config/
    └── LoggingConfig.java             # Configuration class
```

### 2. Configuration Files
```
├── logback-spring.xml                 # Logback configuration
└── application.yaml                   # Application settings
```

## Cách Sử Dụng

### 1. Basic Logging (Mức độ cơ bản)

```java
@RestController
public class UserController {
    
    @GetMapping("/users/{id}")
    @Loggable(level = LogLevel.BASIC, operationType = OperationType.READ)
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        // Sẽ log: method entry, exit, execution time, errors
        return userService.findById(id);
    }
}
```

**Log Output:**
```
2024-01-15 10:30:15.123 INFO  [abc123] [corr-456] --- [http-nio-8080-exec-1] c.l.u.UserController : Method entered - UserController.getUser
2024-01-15 10:30:15.145 INFO  [abc123] [corr-456] --- [http-nio-8080-exec-1] c.l.u.UserController : Method exited successfully - UserController.getUser
```

### 2. Detailed Logging (Mức độ chi tiết)

```java
@Service
public class UserService {
    
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.CREATE,
        resourceType = "User",
        logArguments = true,
        logReturnValue = true,
        messagePrefix = "USER_CREATION"
    )
    public User createUser(CreateUserRequest request) {
        // Sẽ log: arguments, return value, execution time, context info
        return userRepository.save(mapToUser(request));
    }
}
```

**Log Output (JSON):**
```json
{
  "timestamp": "2024-01-15 10:30:15.123",
  "level": "INFO",
  "logger": "com.library.user.UserService",
  "requestId": "abc123",
  "correlationId": "corr-456",
  "className": "UserService",
  "methodName": "createUser",
  "executionTime": "45",
  "message": "USER_CREATION - Method entered - UserService.createUser with args: [CreateUserRequest{name='John', email='j***n@example.com'}]"
}
```

### 3. Advanced Logging (Mức độ nâng cao)

```java
@Service
public class BookService {
    
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.BUSINESS_LOGIC,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 500L,
        customTags = {"feature=book-recommendation", "version=2.1", "critical=true"}
    )
    public List<Book> getRecommendations(String userId, RecommendationRequest request) {
        // Sẽ log: tất cả thông tin + span ID + custom tags + performance metrics
        return recommendationEngine.getRecommendations(userId, request);
    }
}
```

## Tính Năng Nâng Cao

### 1. Conditional Logging (Logging có điều kiện)

Sử dụng HTTP headers để enable detailed logging:

```bash
# Enable detailed logging cho request cụ thể
curl -H "X-Enable-Detailed-Logging: true" http://localhost:8080/api/users

# Set sampling rate
curl -H "X-Sampling-Rate: 0.1" http://localhost:8080/api/books
```

### 2. Sensitive Data Sanitization

```java
@PostMapping("/login")
@Loggable(
    level = LogLevel.DETAILED,
    operationType = OperationType.AUTHENTICATION,
    sanitizeSensitiveData = true,  // Tự động ẩn password, token, etc.
    logArguments = true
)
public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    // password sẽ được masked trong logs
    return authService.authenticate(request);
}
```

### 3. Performance Monitoring

```java
@Loggable(
    includeInPerformanceMonitoring = true,
    performanceThresholdMs = 100L  // Cảnh báo nếu > 100ms
)
public void processLargeData(List<Data> data) {
    // Sẽ log performance metrics và cảnh báo nếu chậm
}
```

### 4. Custom Tags và Context

```java
@Loggable(
    customTags = {
        "business_unit=library",
        "data_source=postgresql", 
        "cache_enabled=true"
    }
)
public List<Book> searchBooks(SearchCriteria criteria) {
    MDCContextManager.addCustomProperty("search_type", criteria.getType());
    MDCContextManager.setDataSource("database");
    // Custom properties sẽ xuất hiện trong logs
}
```

## Configuration

### 1. Application Properties

```yaml
spring:
  application:
    name: library-service

app:
  logging:
    detailed-enabled: false           # Global detailed logging
    default-sampling-rate: 0.1       # 10% sampling rate
    slow-request-threshold-ms: 1000   # Ngưỡng request chậm
    performance-monitoring-enabled: true
    sanitize-sensitive-data: true
    
logging:
  level:
    com.library: DEBUG
    org.springframework.web: WARN
```

### 2. Environment Variables

```bash
# Production settings
export DETAILED_LOGGING_ENABLED=false
export DEFAULT_SAMPLING_RATE=0.01
export SLOW_REQUEST_THRESHOLD=2000
export LOG_RETENTION_DAYS=90

# Development settings  
export DETAILED_LOGGING_ENABLED=true
export DEFAULT_SAMPLING_RATE=1.0
export SLOW_REQUEST_THRESHOLD=500
```

## Log Files Structure

```
logs/
├── library-service-all.log          # Tất cả logs (JSON format)
├── library-service-error.log        # Chỉ error logs
├── library-service-trace.log        # Trace logs (advanced)
└── archived/
    ├── library-service-all.2024-01-14.1.log
    └── library-service-error.2024-01-14.1.log
```

## Monitoring và Alerting

### 1. Key Metrics để Monitor

- **Request Performance**: Execution time > threshold
- **Error Rate**: Số lượng exceptions
- **Log Volume**: Disk space và log file sizes
- **Slow Operations**: Methods vượt performance threshold

### 2. Log Analysis với ELK Stack

```json
# Kibana Query Examples
{
  "query": {
    "bool": {
      "must": [
        {"term": {"level": "ERROR"}},
        {"range": {"timestamp": {"gte": "now-1h"}}},
        {"exists": {"field": "requestId"}}
      ]
    }
  }
}
```

## Best Practices

### 1. Sử Dụng Annotation

```java
// ✅ Good: Specific và có context
@Loggable(
    level = LogLevel.DETAILED,
    operationType = OperationType.DATABASE,
    resourceType = "User",
    performanceThresholdMs = 200L
)

// ❌ Bad: Generic và thiếu context
@Loggable
```

### 2. Performance Considerations

```java
// ✅ Good: Chỉ log chi tiết khi cần
@Loggable(level = LogLevel.ADVANCED)  // Chỉ khi advanced logging enabled

// ❌ Bad: Luôn log mọi thứ
@Loggable(level = LogLevel.BASIC, logArguments = true, logReturnValue = true)
```

### 3. Sampling Strategy

```java
// Production: Chỉ sample 1% requests cho detailed logging
app.logging.default-sampling-rate: 0.01

// Development: Log tất cả
app.logging.default-sampling-rate: 1.0
```

## Testing

### 1. Demo Endpoints

Sử dụng endpoints trong `LoggingExampleController`:

```bash
# Basic logging
curl http://localhost:8080/api/logging-demo/basic

# Detailed logging (với data)
curl -X POST http://localhost:8080/api/logging-demo/detailed \
  -H "Content-Type: application/json" \
  -d '{"name":"test","description":"demo","value":"123"}'

# Advanced logging với performance monitoring
curl -X PUT http://localhost:8080/api/logging-demo/advanced/1?slowOperation=true \
  -H "Content-Type: application/json" \
  -d '{"name":"test","description":"demo","value":"123"}'

# Error logging
curl http://localhost:8080/api/logging-demo/error?triggerError=true

# Sensitive data masking
curl -X POST http://localhost:8080/api/logging-demo/sensitive \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"secret123","token":"abc123","email":"john@example.com"}'
```

### 2. Enable Detailed Logging

```bash
# Enable cho specific request
curl -H "X-Enable-Detailed-Logging: true" http://localhost:8080/api/logging-demo/basic

# Test sampling
curl -H "X-Sampling-Rate: 1.0" http://localhost:8080/api/logging-demo/basic
```

## Troubleshooting

### 1. Common Issues

**Log không xuất hiện:**
- Kiểm tra log level configuration
- Verify AOP enable (`@EnableAspectJAutoProxy`)
- Check dependencies trong pom.xml

**Performance issues:**
- Sử dụng async appenders
- Giảm sampling rate trong production
- Tối ưu log pattern

**Memory leaks:**
- MDC context được clear tự động
- Monitor thread locals
- Review custom properties usage

### 2. Debug Commands

```bash
# View current log levels
curl http://localhost:8080/actuator/loggers

# Change log level runtime
curl -X POST http://localhost:8080/actuator/loggers/com.library.user \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "TRACE"}'

# Check metrics
curl http://localhost:8080/actuator/metrics
```

## Integration với Các Services

Để sử dụng trong các microservices khác:

### 1. Thêm Dependency

```xml
<dependency>
    <groupId>com.library</groupId>
    <artifactId>common-module</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. Enable Configuration

```java
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.library.user",           // Service package
    "com.library.common.aop",     // AOP components
    "com.library.common.config"   // Configuration
})
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
```

## Kết Luận

Hệ thống logging AOP này cung cấp:

- ✅ **3 mức độ logging** phù hợp với từng môi trường
- ✅ **Tự động ẩn dữ liệu nhạy cảm**
- ✅ **Performance monitoring** tích hợp
- ✅ **Distributed tracing** support
- ✅ **Conditional logging** và sampling
- ✅ **JSON format** cho ELK Stack
- ✅ **Human-readable** format cho development
- ✅ **High performance** với async appenders

Hệ thống này sẵn sàng cho production và có thể mở rộng theo nhu cầu của dự án. 