# Kiến trúc hệ thống thư viện mượn sách

## Tổng quan

Hệ thống thư viện mượn sách được xây dựng theo mô hình microservices, cho phép phát triển, triển khai và mở rộng độc lập các thành phần khác nhau của hệ thống. Kiến trúc này giúp tăng tính linh hoạt, khả năng chịu lỗi và khả năng mở rộng của hệ thống.

Hệ thống bao gồm ba microservice chính (User Service, Book Service, Notification Service) và ba module hỗ trợ (Eureka Server, API Gateway, Common Library). Các microservice giao tiếp với nhau thông qua API RESTful và message broker Kafka.

## Công nghệ sử dụng

### Spring Boot
Spring Boot được sử dụng làm framework chính để xây dựng các microservice. Spring Boot cung cấp nhiều tính năng hữu ích như auto-configuration, dependency injection, và tích hợp sẵn với nhiều công nghệ khác.

### Redis
Redis được sử dụng làm hệ thống cache để lưu trữ dữ liệu tạm thời, cải thiện hiệu suất hệ thống. Các trường hợp sử dụng Redis bao gồm:
- Cache thông tin người dùng và sách thường xuyên truy cập
- Lưu trữ JWT tokens và session
- Cache kết quả tìm kiếm

### Kafka
Apache Kafka được sử dụng làm hệ thống message broker để giao tiếp không đồng bộ giữa các microservice. Kafka giúp tăng tính linh hoạt và khả năng mở rộng của hệ thống. Các trường hợp sử dụng Kafka bao gồm:
- Thông báo sự kiện mượn/trả sách
- Gửi thông báo đến người dùng
- Đồng bộ hóa dữ liệu giữa các service

### PostgreSQL
PostgreSQL được sử dụng làm cơ sở dữ liệu quan hệ chính cho hệ thống. Mỗi microservice có cơ sở dữ liệu riêng, đảm bảo tính độc lập và khả năng mở rộng. PostgreSQL được chọn vì tính ổn định, hiệu suất cao và hỗ trợ tốt cho các tính năng nâng cao như JSON, full-text search.

## Kiến trúc tổng thể

![Kiến trúc tổng thể](architecture-diagram.png)

### Các thành phần chính

#### 1. API Gateway
API Gateway đóng vai trò là cổng vào duy nhất cho client, định tuyến request đến các microservice tương ứng. API Gateway cũng xử lý các vấn đề chung như authentication, logging, rate limiting và circuit breaking.

#### 2. Eureka Server
Eureka Server đóng vai trò là Service Registry, quản lý việc đăng ký và khám phá các service. Eureka giúp các service tìm thấy nhau trong môi trường phân tán và hỗ trợ cân bằng tải.

#### 3. User Service
User Service quản lý thông tin người dùng, xác thực và phân quyền. Service này cũng quản lý thẻ thư viện và thời hạn sử dụng.

#### 4. Book Service
Book Service quản lý thông tin sách, danh mục, tác giả và nhà xuất bản. Service này cũng quản lý việc mượn/trả sách, đặt trước và tính phí phạt.

#### 5. Notification Service
Notification Service gửi thông báo cho người dùng qua email, SMS hoặc push notification. Service này nhận các sự kiện từ Kafka và gửi thông báo tương ứng.

#### 6. Common Library
Common Library chứa các mã nguồn dùng chung giữa các service như DTO, exception handling, Kafka configuration và security utilities.

## Chi tiết các Microservice

### 1. User Service

User Service quản lý thông tin người dùng, xác thực và phân quyền. Service này cũng quản lý thẻ thư viện và thời hạn sử dụng.

#### Chức năng chính
- Đăng ký và đăng nhập người dùng
- Quản lý thông tin người dùng
- Phân quyền người dùng (admin, thủ thư, độc giả)
- Quản lý thẻ thư viện và thời hạn sử dụng

#### Công nghệ đặc thù
- Spring Security cho xác thực và phân quyền
- JWT (JSON Web Token) cho việc xác thực
- PostgreSQL để lưu trữ thông tin người dùng
- Redis để cache session và token

#### Cơ sở dữ liệu
User Service sử dụng PostgreSQL để lưu trữ thông tin người dùng, vai trò và thẻ thư viện. Schema bao gồm các bảng: users, roles, user_roles và library_cards.

#### API
User Service cung cấp các API cho authentication, user management và library card management. Các API này được bảo vệ bởi Spring Security và JWT.

#### Kafka Integration
User Service gửi các sự kiện như user-created, user-updated, card-created, card-renewed và card-expired đến Kafka để các service khác có thể xử lý.

### 2. Book Service

Book Service quản lý thông tin sách, danh mục, tác giả và nhà xuất bản. Service này cũng quản lý việc mượn/trả sách, đặt trước và tính phí phạt.

#### Chức năng chính
- Quản lý thông tin sách, danh mục, tác giả và nhà xuất bản
- Quản lý việc mượn/trả sách
- Quản lý đặt trước sách
- Tính phí phạt khi trả muộn

#### Công nghệ đặc thù
- PostgreSQL để lưu trữ thông tin sách và giao dịch mượn/trả
- Redis để cache thông tin sách phổ biến
- Kafka để gửi thông báo khi có sự kiện mượn/trả/quá hạn

#### Cơ sở dữ liệu
Book Service sử dụng PostgreSQL để lưu trữ thông tin sách, danh mục, tác giả, nhà xuất bản, bản sao sách, giao dịch mượn/trả và đặt trước. Schema bao gồm các bảng: books, authors, categories, publishers, book_authors, book_categories, book_copies, borrowings và reservations.

#### API
Book Service cung cấp các API cho book management, category management, author management, borrowing management và reservation management.

#### Kafka Integration
Book Service gửi các sự kiện như book-borrowed, book-returned, book-overdue, book-reserved và reservation-available đến Kafka để Notification Service có thể gửi thông báo tương ứng.

### 3. Notification Service

Notification Service gửi thông báo cho người dùng qua email, SMS hoặc push notification. Service này nhận các sự kiện từ Kafka và gửi thông báo tương ứng.

#### Chức năng chính
- Gửi thông báo cho người dùng (email, SMS, push notification)
- Quản lý tùy chọn thông báo của người dùng
- Quản lý mẫu thông báo

#### Công nghệ đặc thù
- Kafka Consumer để nhận các sự kiện cần thông báo
- Spring Mail để gửi email
- Tích hợp với các dịch vụ SMS/push notification
- PostgreSQL để lưu lịch sử thông báo

#### Cơ sở dữ liệu
Notification Service sử dụng PostgreSQL để lưu trữ thông tin thông báo, mẫu thông báo và tùy chọn thông báo của người dùng. Schema bao gồm các bảng: notifications, notification_templates và notification_preferences.

#### API
Notification Service cung cấp các API cho notification management, notification preference management và template management.

#### Kafka Integration
Notification Service lắng nghe các sự kiện từ Kafka như user-created, user-updated, card-created, card-renewed, card-expired, book-borrowed, book-returned, book-overdue, book-reserved và reservation-available để gửi thông báo tương ứng.

## Chi tiết các Module hỗ trợ

### 1. Eureka Server

Eureka Server đóng vai trò là Service Registry, quản lý việc đăng ký và khám phá các service. Eureka giúp các service tìm thấy nhau trong môi trường phân tán và hỗ trợ cân bằng tải.

#### Chức năng chính
- Đăng ký các service
- Khám phá các service
- Cân bằng tải
- Giám sát trạng thái các service

#### Cấu hình
Eureka Server được cấu hình để không đăng ký chính nó và không lấy registry từ các Eureka Server khác. Điều này phù hợp cho môi trường single-instance.

### 2. API Gateway

API Gateway đóng vai trò là cổng vào duy nhất cho client, định tuyến request đến các microservice tương ứng. API Gateway cũng xử lý các vấn đề chung như authentication, logging, rate limiting và circuit breaking.

#### Chức năng chính
- Định tuyến request đến các service tương ứng
- Xác thực và phân quyền
- Circuit breaking
- Rate limiting
- Logging

#### Cấu hình
API Gateway được cấu hình để định tuyến request đến các service tương ứng dựa trên path. Mỗi route có thể có các filter riêng như CircuitBreaker để xử lý lỗi.

### 3. Common Library

Common Library chứa các mã nguồn dùng chung giữa các service như DTO, exception handling, Kafka configuration và security utilities.

#### Thành phần chính
- DTO (Data Transfer Objects)
- Exception Handling
- Kafka Configuration
- Security Utilities
- Utilities

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

## Yêu cầu phi chức năng

### Khả năng mở rộng (Scalability)
Kiến trúc microservices cho phép mở rộng từng service độc lập dựa trên nhu cầu. Ví dụ, Book Service có thể cần nhiều instance hơn trong thời gian cao điểm như đầu học kỳ.

### Tính sẵn sàng cao (High Availability)
Mỗi service có thể có nhiều instance chạy đồng thời, kết hợp với Eureka Server và API Gateway để đảm bảo tính sẵn sàng cao. Nếu một instance gặp sự cố, các instance khác vẫn có thể phục vụ request.

### Khả năng chịu lỗi (Fault Tolerance)
Circuit Breaker được cấu hình trong API Gateway để ngăn chặn lỗi lan truyền. Nếu một service gặp sự cố, API Gateway sẽ chuyển hướng request đến fallback endpoint.

### Bảo mật (Security)
JWT được sử dụng cho việc xác thực và phân quyền. API Gateway kiểm tra token trước khi chuyển request đến các service. Mỗi service cũng có thể kiểm tra quyền truy cập dựa trên vai trò của người dùng.

### Hiệu suất (Performance)
Redis được sử dụng để cache dữ liệu thường xuyên truy cập, giảm tải cho cơ sở dữ liệu và cải thiện thời gian phản hồi. Kafka được sử dụng cho giao tiếp không đồng bộ, cho phép các service xử lý độc lập và không bị chặn.

### Khả năng theo dõi và giám sát (Monitoring)
Spring Boot Actuator được sử dụng để cung cấp các endpoint giám sát. Các metric có thể được thu thập và hiển thị bằng các công cụ như Prometheus và Grafana.

## Kết luận

Kiến trúc microservices được đề xuất cho hệ thống thư viện mượn sách cung cấp nhiều lợi ích như tính linh hoạt, khả năng mở rộng và khả năng chịu lỗi. Việc sử dụng các công nghệ như Spring Boot, Redis, Kafka và PostgreSQL giúp đáp ứng các yêu cầu chức năng và phi chức năng của hệ thống.

Mỗi microservice có trách nhiệm riêng biệt và có thể được phát triển, triển khai và mở rộng độc lập. Các service giao tiếp với nhau thông qua API RESTful và message broker Kafka, đảm bảo tính linh hoạt và khả năng mở rộng của hệ thống.

Kiến trúc này cũng hỗ trợ việc phát triển liên tục và triển khai liên tục (CI/CD), cho phép các team phát triển và triển khai các service độc lập mà không ảnh hưởng đến các service khác.
