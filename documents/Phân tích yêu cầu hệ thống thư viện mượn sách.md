# Phân tích yêu cầu hệ thống thư viện mượn sách

## Tổng quan hệ thống
Hệ thống thư viện mượn sách được xây dựng theo mô hình microservices với các công nghệ: Spring Boot, Redis, Kafka, PostgreSQL. Hệ thống bao gồm 3 services chính và các module hỗ trợ.

## Công nghệ sử dụng
- **Spring Boot**: Framework để xây dựng các microservices
- **Redis**: Cache và lưu trữ dữ liệu tạm thời
- **Kafka**: Hệ thống message broker để giao tiếp giữa các services
- **PostgreSQL**: Cơ sở dữ liệu quan hệ

## Các Microservices

### 1. User Service
**Chức năng chính:**
- Quản lý thông tin người dùng (đăng ký, đăng nhập, cập nhật thông tin)
- Phân quyền người dùng (admin, thủ thư, độc giả)
- Xác thực và ủy quyền (authentication & authorization)
- Quản lý thẻ thư viện và thời hạn sử dụng

**Công nghệ đặc thù:**
- Spring Security cho xác thực và phân quyền
- JWT (JSON Web Token) cho việc xác thực
- PostgreSQL để lưu trữ thông tin người dùng
- Redis để cache session và token

### 2. Book Service
**Chức năng chính:**
- Quản lý thông tin sách (thêm, sửa, xóa, tìm kiếm)
- Quản lý danh mục sách (thể loại, tác giả, nhà xuất bản)
- Quản lý việc mượn/trả sách
- Theo dõi tình trạng sách (có sẵn, đã mượn, đang bảo trì)
- Quản lý hạn trả sách và tính phí phạt nếu trả muộn

**Công nghệ đặc thù:**
- PostgreSQL để lưu trữ thông tin sách và giao dịch mượn/trả
- Redis để cache thông tin sách phổ biến
- Kafka để gửi thông báo khi có sự kiện mượn/trả/quá hạn

### 3. Notification Service
**Chức năng chính:**
- Gửi thông báo cho người dùng (email, SMS, push notification)
- Thông báo về việc mượn sách thành công
- Nhắc nhở trả sách trước hạn
- Thông báo về phí phạt nếu trả muộn
- Thông báo về sách mới, sự kiện của thư viện

**Công nghệ đặc thù:**
- Kafka Consumer để nhận các sự kiện cần thông báo
- Spring Mail để gửi email
- Tích hợp với các dịch vụ SMS/push notification
- PostgreSQL để lưu lịch sử thông báo

## Các Module hỗ trợ

### 1. Eureka Server
- Đóng vai trò là Service Registry
- Quản lý việc đăng ký và khám phá các services
- Hỗ trợ cân bằng tải (load balancing)
- Giúp các services tìm thấy nhau trong môi trường phân tán

### 2. API Gateway
- Đóng vai trò là cổng vào duy nhất cho client
- Định tuyến request đến các services tương ứng
- Xử lý cross-cutting concerns như authentication, logging
- Cung cấp API documentation (Swagger/OpenAPI)
- Rate limiting và circuit breaking

### 3. Common Library
- Chứa các mã nguồn dùng chung giữa các services
- Định nghĩa các DTO (Data Transfer Objects)
- Xử lý exception chung
- Utility classes
- Security configurations
- Kafka configurations

## Luồng dữ liệu và tương tác giữa các services

1. **Luồng mượn sách:**
   - User đăng nhập qua API Gateway
   - API Gateway chuyển request đến User Service để xác thực
   - Sau khi xác thực, request mượn sách được chuyển đến Book Service
   - Book Service kiểm tra tình trạng sách và thực hiện giao dịch mượn
   - Book Service gửi event đến Kafka
   - Notification Service nhận event và gửi thông báo xác nhận

2. **Luồng nhắc nhở trả sách:**
   - Book Service định kỳ kiểm tra các sách sắp đến hạn trả
   - Gửi event đến Kafka
   - Notification Service nhận event và gửi thông báo nhắc nhở

3. **Luồng trả sách:**
   - User trả sách thông qua API Gateway
   - Book Service cập nhật trạng thái sách và tính phí phạt nếu có
   - Book Service gửi event đến Kafka
   - Notification Service nhận event và gửi thông báo xác nhận

## Yêu cầu phi chức năng
- Khả năng mở rộng (scalability)
- Tính sẵn sàng cao (high availability)
- Khả năng chịu lỗi (fault tolerance)
- Bảo mật (security)
- Hiệu suất (performance)
- Khả năng theo dõi và giám sát (monitoring)
