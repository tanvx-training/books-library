# Thiết kế chi tiết các Microservices và Module hỗ trợ

## Cấu trúc thư mục dự án

```
library-system/
├── api-gateway/
├── eureka-server/
├── common-library/
├── user-service/
├── book-service/
├── notification-service/
└── docs/
```

## 1. User Service

### Cấu trúc thư mục
```
user-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── library/
│   │   │           └── userservice/
│   │   │               ├── config/
│   │   │               ├── controller/
│   │   │               ├── dto/
│   │   │               ├── entity/
│   │   │               ├── exception/
│   │   │               ├── repository/
│   │   │               ├── security/
│   │   │               ├── service/
│   │   │               └── UserServiceApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── bootstrap.yml
│   └── test/
├── Dockerfile
└── pom.xml
```

### Entities
- `User`: Thông tin người dùng
- `Role`: Vai trò người dùng (ADMIN, LIBRARIAN, READER)
- `LibraryCard`: Thông tin thẻ thư viện

### APIs
1. **Authentication API**
   - `POST /api/auth/register`: Đăng ký người dùng mới
   - `POST /api/auth/login`: Đăng nhập và nhận JWT token
   - `POST /api/auth/refresh`: Làm mới token
   - `POST /api/auth/logout`: Đăng xuất

2. **User Management API**
   - `GET /api/users`: Lấy danh sách người dùng (Admin/Librarian)
   - `GET /api/users/{id}`: Lấy thông tin người dùng theo ID
   - `PUT /api/users/{id}`: Cập nhật thông tin người dùng
   - `DELETE /api/users/{id}`: Xóa người dùng (Admin)
   - `GET /api/users/me`: Lấy thông tin người dùng hiện tại

3. **Library Card API**
   - `POST /api/cards`: Tạo thẻ thư viện mới (Admin/Librarian)
   - `GET /api/cards/{id}`: Lấy thông tin thẻ
   - `PUT /api/cards/{id}`: Cập nhật thông tin thẻ
   - `PUT /api/cards/{id}/renew`: Gia hạn thẻ
   - `GET /api/cards/{id}/status`: Kiểm tra trạng thái thẻ

### Cơ sở dữ liệu
**PostgreSQL Schema**:
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) UNIQUE NOT NULL
);

CREATE TABLE user_roles (
    user_id INT REFERENCES users(id),
    role_id INT REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE library_cards (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    card_number VARCHAR(20) UNIQUE NOT NULL,
    issue_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Redis Cache
- Lưu trữ JWT tokens
- Cache thông tin người dùng thường xuyên truy cập
- Lưu trữ session và blacklist token

### Kafka Producers
- `user-created`: Khi người dùng mới được tạo
- `user-updated`: Khi thông tin người dùng được cập nhật
- `card-created`: Khi thẻ thư viện mới được tạo
- `card-renewed`: Khi thẻ thư viện được gia hạn
- `card-expired`: Khi thẻ thư viện hết hạn

## 2. Book Service

### Cấu trúc thư mục
```
book-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── library/
│   │   │           └── bookservice/
│   │   │               ├── config/
│   │   │               ├── controller/
│   │   │               ├── dto/
│   │   │               ├── entity/
│   │   │               ├── exception/
│   │   │               ├── repository/
│   │   │               ├── service/
│   │   │               └── BookServiceApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── bootstrap.yml
│   └── test/
├── Dockerfile
└── pom.xml
```

### Entities
- `Book`: Thông tin sách
- `Author`: Thông tin tác giả
- `Category`: Thể loại sách
- `Publisher`: Nhà xuất bản
- `BookCopy`: Bản sao của sách (có thể có nhiều bản sao cho một đầu sách)
- `Borrowing`: Thông tin mượn sách
- `Reservation`: Đặt trước sách

### APIs
1. **Book Management API**
   - `POST /api/books`: Thêm sách mới (Admin/Librarian)
   - `GET /api/books`: Lấy danh sách sách
   - `GET /api/books/{id}`: Lấy thông tin sách theo ID
   - `PUT /api/books/{id}`: Cập nhật thông tin sách
   - `DELETE /api/books/{id}`: Xóa sách (Admin/Librarian)
   - `GET /api/books/search`: Tìm kiếm sách theo tiêu chí

2. **Category API**
   - `POST /api/categories`: Thêm thể loại mới
   - `GET /api/categories`: Lấy danh sách thể loại
   - `GET /api/books/category/{categoryId}`: Lấy sách theo thể loại

3. **Author API**
   - `POST /api/authors`: Thêm tác giả mới
   - `GET /api/authors`: Lấy danh sách tác giả
   - `GET /api/books/author/{authorId}`: Lấy sách theo tác giả

4. **Borrowing API**
   - `POST /api/borrowings`: Mượn sách
   - `PUT /api/borrowings/{id}/return`: Trả sách
   - `GET /api/borrowings/user/{userId}`: Lấy lịch sử mượn sách của người dùng
   - `GET /api/borrowings/overdue`: Lấy danh sách sách quá hạn

5. **Reservation API**
   - `POST /api/reservations`: Đặt trước sách
   - `DELETE /api/reservations/{id}`: Hủy đặt trước
   - `GET /api/reservations/user/{userId}`: Lấy danh sách đặt trước của người dùng

### Cơ sở dữ liệu
**PostgreSQL Schema**:
```sql
CREATE TABLE authors (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    biography TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE publishers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE books (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    publisher_id INT REFERENCES publishers(id),
    publication_year INT,
    description TEXT,
    cover_image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE book_authors (
    book_id INT REFERENCES books(id),
    author_id INT REFERENCES authors(id),
    PRIMARY KEY (book_id, author_id)
);

CREATE TABLE book_categories (
    book_id INT REFERENCES books(id),
    category_id INT REFERENCES categories(id),
    PRIMARY KEY (book_id, category_id)
);

CREATE TABLE book_copies (
    id SERIAL PRIMARY KEY,
    book_id INT REFERENCES books(id),
    copy_number VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL, -- AVAILABLE, BORROWED, RESERVED, MAINTENANCE
    condition VARCHAR(20), -- NEW, GOOD, FAIR, POOR
    location VARCHAR(50), -- Shelf location
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(book_id, copy_number)
);

CREATE TABLE borrowings (
    id SERIAL PRIMARY KEY,
    book_copy_id INT REFERENCES book_copies(id),
    user_id INT NOT NULL, -- References user_id from user-service
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    fine_amount DECIMAL(10, 2) DEFAULT 0.00,
    status VARCHAR(20) NOT NULL, -- ACTIVE, RETURNED, OVERDUE
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reservations (
    id SERIAL PRIMARY KEY,
    book_id INT REFERENCES books(id),
    user_id INT NOT NULL, -- References user_id from user-service
    reservation_date TIMESTAMP NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL, -- PENDING, FULFILLED, CANCELLED, EXPIRED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Redis Cache
- Cache thông tin sách phổ biến
- Cache kết quả tìm kiếm
- Cache danh sách thể loại và tác giả

### Kafka Producers
- `book-borrowed`: Khi sách được mượn
- `book-returned`: Khi sách được trả
- `book-overdue`: Khi sách quá hạn
- `book-reserved`: Khi sách được đặt trước
- `reservation-available`: Khi sách đặt trước có sẵn

## 3. Notification Service

### Cấu trúc thư mục
```
notification-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── library/
│   │   │           └── notificationservice/
│   │   │               ├── config/
│   │   │               ├── controller/
│   │   │               ├── dto/
│   │   │               ├── entity/
│   │   │               ├── exception/
│   │   │               ├── kafka/
│   │   │               ├── repository/
│   │   │               ├── service/
│   │   │               └── NotificationServiceApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── bootstrap.yml
│   │       └── templates/
│   │           ├── email-templates/
│   │           └── sms-templates/
│   └── test/
├── Dockerfile
└── pom.xml
```

### Entities
- `Notification`: Lưu trữ thông tin thông báo
- `NotificationTemplate`: Mẫu thông báo
- `NotificationPreference`: Tùy chọn thông báo của người dùng

### APIs
1. **Notification Management API**
   - `GET /api/notifications/user/{userId}`: Lấy thông báo của người dùng
   - `PUT /api/notifications/{id}/read`: Đánh dấu thông báo đã đọc
   - `DELETE /api/notifications/{id}`: Xóa thông báo

2. **Notification Preference API**
   - `GET /api/preferences/user/{userId}`: Lấy tùy chọn thông báo của người dùng
   - `PUT /api/preferences/user/{userId}`: Cập nhật tùy chọn thông báo

3. **Template Management API**
   - `POST /api/templates`: Thêm mẫu thông báo mới (Admin)
   - `GET /api/templates`: Lấy danh sách mẫu thông báo
   - `PUT /api/templates/{id}`: Cập nhật mẫu thông báo

### Cơ sở dữ liệu
**PostgreSQL Schema**:
```sql
CREATE TABLE notification_templates (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL, -- EMAIL, SMS, PUSH
    subject VARCHAR(200),
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notification_preferences (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL, -- References user_id from user-service
    email_enabled BOOLEAN DEFAULT TRUE,
    sms_enabled BOOLEAN DEFAULT FALSE,
    push_enabled BOOLEAN DEFAULT TRUE,
    borrow_notification BOOLEAN DEFAULT TRUE,
    return_reminder BOOLEAN DEFAULT TRUE,
    overdue_notification BOOLEAN DEFAULT TRUE,
    reservation_notification BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id)
);

CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL, -- References user_id from user-service
    template_id INT REFERENCES notification_templates(id),
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(20) NOT NULL, -- EMAIL, SMS, PUSH
    status VARCHAR(20) NOT NULL, -- SENT, DELIVERED, READ, FAILED
    sent_at TIMESTAMP,
    read_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Kafka Consumers
- Lắng nghe các sự kiện từ User Service và Book Service:
  - `user-created`
  - `user-updated`
  - `card-created`
  - `card-renewed`
  - `card-expired`
  - `book-borrowed`
  - `book-returned`
  - `book-overdue`
  - `book-reserved`
  - `reservation-available`

## 4. Eureka Server

### Cấu trúc thư mục
```
eureka-server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── library/
│   │   │           └── eurekaserver/
│   │   │               └── EurekaServerApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── Dockerfile
└── pom.xml
```

### Cấu hình
**application.yml**:
```yaml
server:
  port: 8761

eureka:
  client:
    registerWithEureka: false
    fetchRegistry: false
  server:
    waitTimeInMsWhenSyncEmpty: 0
```

## 5. API Gateway

### Cấu trúc thư mục
```
api-gateway/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── library/
│   │   │           └── apigateway/
│   │   │               ├── config/
│   │   │               ├── filter/
│   │   │               └── ApiGatewayApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── Dockerfile
└── pom.xml
```

### Cấu hình
**application.yml**:
```yaml
server:
  port: 8080

spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://USER-SERVICE
          predicates:
            - Path=/api/users/**, /api/auth/**, /api/cards/**
          filters:
            - name: CircuitBreaker
              args:
                name: userServiceCircuitBreaker
                fallbackUri: forward:/fallback/user-service
        
        - id: book-service
          uri: lb://BOOK-SERVICE
          predicates:
            - Path=/api/books/**, /api/authors/**, /api/categories/**, /api/borrowings/**, /api/reservations/**
          filters:
            - name: CircuitBreaker
              args:
                name: bookServiceCircuitBreaker
                fallbackUri: forward:/fallback/book-service
        
        - id: notification-service
          uri: lb://NOTIFICATION-SERVICE
          predicates:
            - Path=/api/notifications/**, /api/preferences/**, /api/templates/**
          filters:
            - name: CircuitBreaker
              args:
                name: notificationServiceCircuitBreaker
                fallbackUri: forward:/fallback/notification-service

  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://user-service/api/auth/issuer
```

## 6. Common Library

### Cấu trúc thư mục
```
common-library/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── library/
│   │               └── common/
│   │                   ├── dto/
│   │                   ├── exception/
│   │                   ├── kafka/
│   │                   ├── security/
│   │                   └── util/
│   └── test/
└── pom.xml
```

### Các thành phần chính
1. **DTO (Data Transfer Objects)**
   - `UserDTO`: Thông tin người dùng
   - `BookDTO`: Thông tin sách
   - `BorrowingDTO`: Thông tin mượn sách
   - `NotificationDTO`: Thông tin thông báo
   - `ApiResponse`: Định dạng phản hồi API chung

2. **Exception Handling**
   - `GlobalExceptionHandler`: Xử lý exception chung
   - `ResourceNotFoundException`: Exception khi không tìm thấy tài nguyên
   - `BadRequestException`: Exception khi request không hợp lệ
   - `UnauthorizedException`: Exception khi không có quyền truy cập

3. **Kafka Configuration**
   - `KafkaProducerConfig`: Cấu hình Kafka Producer
   - `KafkaConsumerConfig`: Cấu hình Kafka Consumer
   - `KafkaTopicConfig`: Cấu hình Kafka Topic

4. **Security Utilities**
   - `JwtUtil`: Tiện ích xử lý JWT
   - `SecurityConstants`: Các hằng số bảo mật

5. **Utilities**
   - `DateUtil`: Tiện ích xử lý ngày tháng
   - `StringUtil`: Tiện ích xử lý chuỗi
   - `ValidationUtil`: Tiện ích xác thực dữ liệu

## Luồng dữ liệu và tương tác

### Luồng đăng ký và đăng nhập
1. Client gửi request đăng ký đến API Gateway
2. API Gateway chuyển request đến User Service
3. User Service tạo người dùng mới và lưu vào PostgreSQL
4. User Service gửi event `user-created` đến Kafka
5. Notification Service nhận event và gửi email chào mừng

### Luồng mượn sách
1. Client gửi request mượn sách đến API Gateway
2. API Gateway chuyển request đến Book Service
3. Book Service kiểm tra với User Service về tình trạng thẻ thư viện
4. Book Service kiểm tra tình trạng sách và thực hiện giao dịch mượn
5. Book Service gửi event `book-borrowed` đến Kafka
6. Notification Service nhận event và gửi thông báo xác nhận

### Luồng nhắc nhở trả sách
1. Book Service định kỳ kiểm tra các sách sắp đến hạn trả
2. Book Service gửi event `book-due-soon` đến Kafka
3. Notification Service nhận event và gửi thông báo nhắc nhở

### Luồng trả sách
1. Client gửi request trả sách đến API Gateway
2. API Gateway chuyển request đến Book Service
3. Book Service cập nhật trạng thái sách và tính phí phạt nếu có
4. Book Service gửi event `book-returned` đến Kafka
5. Notification Service nhận event và gửi thông báo xác nhận

### Luồng đặt trước sách
1. Client gửi request đặt trước sách đến API Gateway
2. API Gateway chuyển request đến Book Service
3. Book Service kiểm tra tình trạng sách và tạo đặt trước
4. Book Service gửi event `book-reserved` đến Kafka
5. Notification Service nhận event và gửi thông báo xác nhận
6. Khi sách được trả và có đặt trước, Book Service gửi event `reservation-available`
7. Notification Service nhận event và thông báo cho người đặt trước
