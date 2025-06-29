# Best Practices cho Logging Annotations trên từng Tầng

## Tổng Quan Triển Khai

Chúng ta đã triển khai logging annotations trên **3 tầng chính** với các best practices cụ thể cho từng layer:

## 1. **Controller Layer** - HTTP Entry Points

### **Đặc điểm:**
- **Level**: BASIC/DETAILED
- **Focus**: HTTP context, user interaction, API performance
- **Security**: Không log sensitive response data (tokens, user details)

### **Best Practices:**

#### **✅ Authentication Endpoints**
```java
@PostMapping("/login")
@Loggable(
    level = LogLevel.DETAILED,
    operationType = OperationType.AUTHENTICATION,
    resourceType = "UserSession",
    logArguments = true,
    logReturnValue = false, // ❗ Không log tokens
    sanitizeSensitiveData = true,
    performanceThresholdMs = 1500L,
    customTags = {"security_operation=true", "auth_type=credentials"}
)
```

#### **✅ CRUD Operations**
```java
// READ operations - có thể log return value cho single entity
@GetMapping("/{id}")
@Loggable(
    level = LogLevel.DETAILED,
    operationType = OperationType.READ,
    logReturnValue = true, // OK cho single entity
    performanceThresholdMs = 500L
)

// LIST operations - không log return value (large data)
@GetMapping
@Loggable(
    level = LogLevel.BASIC,
    operationType = OperationType.READ,
    logReturnValue = false, // ❗ Không log collections
    customTags = {"pagination=true"}
)

// CREATE operations - log input và output
@PostMapping
@Loggable(
    level = LogLevel.DETAILED,
    operationType = OperationType.CREATE,
    logArguments = true,
    logReturnValue = true,
    includeInPerformanceMonitoring = true,
    performanceThresholdMs = 2000L
)
```

#### **✅ Search Operations**
```java
@GetMapping("/search")
@Loggable(
    level = LogLevel.DETAILED,
    operationType = OperationType.SEARCH,
    logReturnValue = false, // ❗ Search results có thể lớn
    performanceThresholdMs = 3000L, // Search thường chậm hơn
    customTags = {"full_text_search=true"}
)
```

---

## 2. **Service Layer** - Business Logic

### **Đặc điểm:**
- **Level**: DETAILED/ADVANCED 
- **Focus**: Business logic, transaction boundaries, complex operations
- **Context**: Business rules, validation, data transformation

### **Best Practices:**

#### **✅ Simple Business Operations**
```java
@Loggable(
    level = LogLevel.DETAILED,
    operationType = OperationType.READ,
    resourceType = "User",
    logArguments = true,
    logReturnValue = false, // Service layer không log collections
    performanceThresholdMs = 800L,
    customTags = {"layer=service", "transaction=readonly"}
)
public PaginatedResponse<UserDTO> getAllUsers(PaginatedRequest request) {
    // Business logic
}
```

#### **✅ Complex Business Operations** 
```java
@Loggable(
    level = LogLevel.ADVANCED,
    operationType = OperationType.CREATE,
    resourceType = "Book",
    logArguments = true,
    logReturnValue = true,
    includeInPerformanceMonitoring = true,
    performanceThresholdMs = 1500L,
    customTags = {
        "layer=service", 
        "multi_entity_operation=true",
        "business_validation=true",
        "includes_relations=true"
    }
)
@Transactional
public BookDTO createBook(BookCreateDTO dto) {
    // Complex business logic với multiple validations
}
```

#### **✅ Authentication & Security Operations**
```java
@Loggable(
    level = LogLevel.ADVANCED,
    operationType = OperationType.AUTHENTICATION,
    resourceType = "UserSession",
    logArguments = true,
    logReturnValue = false, // ❗ Không log auth responses
    sanitizeSensitiveData = true,
    performanceThresholdMs = 2000L,
    customTags = {
        "security_operation=true",
        "password_encoding=true",
        "event_publishing=true"
    }
)
```

#### **✅ Search & Analytics**
```java
@Loggable(
    level = LogLevel.ADVANCED,
    operationType = OperationType.SEARCH,
    logReturnValue = false, // ❗ Không log search results
    performanceThresholdMs = 2000L,
    customTags = {
        "full_text_search=true",
        "specification_pattern=true",
        "dynamic_criteria=true"
    }
)
```

---

## 3. **Repository Layer** - Database Access

### **Đặc điểm:**
- **Level**: ADVANCED
- **Focus**: Database performance, query optimization, data access patterns
- **Context**: SQL operations, transaction management, bulk operations

### **Best Practices:**

#### **✅ Simple Database Operations**
```java
@Loggable(
    level = LogLevel.ADVANCED,
    operationType = OperationType.READ,
    resourceType = "Book",
    logArguments = true,
    logReturnValue = true, // OK cho single entity
    performanceThresholdMs = 500L,
    customTags = {
        "layer=repository",
        "fetch_join=true",
        "eager_loading=true"
    }
)
public Optional<Book> findBookWithDetails(Long bookId) {
    // Custom JPA query
}
```

#### **✅ Complex Query Operations**
```java
@Loggable(
    level = LogLevel.ADVANCED,
    operationType = OperationType.DATABASE,
    logReturnValue = false, // ❗ Không log collections
    performanceThresholdMs = 1000L,
    customTags = {
        "layer=repository",
        "complex_criteria=true",
        "multi_table_join=true",
        "performance_critical=true"
    }
)
public List<Book> findBooksByComplexCriteria(...) {
    // Complex JPQL with joins
}
```

#### **✅ Search Operations**
```java
@Loggable(
    level = LogLevel.ADVANCED,
    operationType = OperationType.SEARCH,
    logReturnValue = false, // ❗ Search results có thể lớn
    performanceThresholdMs = 2000L,
    customTags = {
        "layer=repository",
        "full_text_search=true",
        "dynamic_query=true",
        "pagination=true"
    }
)
```

#### **✅ Bulk Operations**
```java
@Loggable(
    level = LogLevel.ADVANCED,
    operationType = OperationType.UPDATE,
    logReturnValue = true, // Log số records affected
    performanceThresholdMs = 1500L,
    customTags = {
        "bulk_operation=true",
        "batch_processing=true",
        "performance_critical=true"
    }
)
public int updateBookAvailability(List<Long> bookIds, boolean available) {
    // Bulk update operation
}
```

#### **✅ Analytics Operations**
```java
@Loggable(
    level = LogLevel.ADVANCED,
    operationType = OperationType.DATABASE,
    logReturnValue = true, // OK cho aggregate results
    performanceThresholdMs = 800L,
    customTags = {
        "analytics=true",
        "aggregate_function=true",
        "reporting=true"
    }
)
public Long countBooksByCategory(Long categoryId) {
    // Analytics query
}
```

---

## 4. **Matrix quyết định Log Level**

| Tầng | Loại Operation | Log Level | Lý do |
|------|---------------|-----------|-------|
| **Controller** | Simple CRUD | BASIC | Đủ thông tin cho monitoring |
| **Controller** | Search/Complex | DETAILED | Cần context cho debugging |
| **Controller** | Authentication | DETAILED | Security audit requirements |
| **Service** | Simple Business | DETAILED | Business logic visibility |
| **Service** | Complex Business | ADVANCED | Full traceability cần thiết |
| **Service** | Security Ops | ADVANCED | Security audit + performance |
| **Repository** | All Operations | ADVANCED | Database performance critical |

---

## 5. **Performance Thresholds Guidelines**

| Loại Operation | Threshold (ms) | Rationale |
|---------------|----------------|-----------|
| **Single Entity Read** | 300-500ms | Fast DB lookup |
| **List/Pagination** | 800-1000ms | Multiple records + mapping |
| **Create/Update** | 1500-2000ms | Validation + DB write |
| **Search Operations** | 2000-3000ms | Complex queries |
| **Authentication** | 1500-2000ms | Password hashing + validation |
| **Bulk Operations** | 1500-2500ms | Multiple DB operations |

---

## 6. **Custom Tags Strategy**

### **Layer Tags** (luôn include):
```java
customTags = {"layer=controller|service|repository"}
```

### **Operation Context Tags**:
```java
// Transaction context
"transaction=readonly|write"

// Security context  
"security_operation=true"
"auth_type=credentials|token"

// Performance context
"performance_critical=true"
"bulk_operation=true"

// Technical context
"pagination=true"
"full_text_search=true"
"multi_table_join=true"
"business_validation=true"
```

---

## 7. **Sensitive Data Handling**

### **✅ Luôn Enable Sanitization cho:**
- Authentication endpoints
- User registration/profile updates  
- Any operation với PII data

```java
@Loggable(
    sanitizeSensitiveData = true, // ❗ Quan trọng
    logArguments = true,
    logReturnValue = false  // ❗ Không log sensitive responses
)
```

### **❌ Không Log Return Values cho:**
- Authentication responses (tokens)
- User profile details
- Large collections/search results
- Sensitive business data

---

## 8. **Error Handling Best Practices**

```java
@Loggable(
    logException = true, // ✅ Luôn log exceptions
    messagePrefix = "SPECIFIC_OPERATION",
    customTags = {"error_handling=true"}
)
```

---

## 9. **Integration Setup**

### **Trong từng Service Application:**

```java
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.library.book",           // Service-specific packages
    "com.library.common.aop",     // AOP components  
    "com.library.common.config"   // Configuration
})
public class BookServiceApplication {
    // Application setup
}
```

### **Dependency trong pom.xml:**
```xml
<dependency>
    <groupId>com.library</groupId>
    <artifactId>common-module</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

---

## 10. **Monitoring và Alerting**

### **Key Metrics từ Logs:**
- **Performance**: Methods vượt threshold
- **Error Rate**: Exception frequency by layer
- **Authentication**: Failed login attempts  
- **Business Logic**: Critical operation failures

### **Log Analysis Queries:**
```json
// Tìm slow operations
{
  "query": {
    "bool": {
      "must": [
        {"range": {"executionTime": {"gte": 2000}}},
        {"term": {"level": "WARN"}}
      ]
    }
  }
}

// Authentication analysis
{
  "query": {
    "bool": {
      "must": [
        {"term": {"operationType": "AUTHENTICATION"}},
        {"term": {"businessLogicStatus": "FAILURE"}}
      ]
    }
  }
}
```

---

## 🎯 **Summary: Key Takeaways**

1. **🏗️ Layer-specific Patterns**: Mỗi tầng có pattern riêng phù hợp với responsibility
2. **🔒 Security First**: Luôn sanitize sensitive data và không log tokens
3. **⚡ Performance Focus**: Monitoring execution time với appropriate thresholds  
4. **📊 Rich Context**: Sử dụng custom tags để categorize và analyze logs
5. **🎛️ Conditional Logging**: Advanced logging chỉ khi needed (headers/sampling)
6. **📈 Scalable**: Designed cho production với async processing

Hệ thống này cung cấp **comprehensive visibility** across tất cả tầng trong microservices architecture! 🚀 