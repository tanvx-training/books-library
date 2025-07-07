## 1. Tổng quan về module Publisher

Module Publisher sau khi migrate sang Domain-Driven Design (DDD) được tổ chức theo các layer rõ ràng, mỗi layer có trách nhiệm riêng biệt và giao tiếp với nhau thông qua các interface được định nghĩa rõ ràng. Điều này giúp tách biệt logic nghiệp vụ khỏi các chi tiết kỹ thuật, làm cho code dễ bảo trì và mở rộng hơn.

## 2. Cấu trúc và các thành phần chính

### 2.1. Domain Layer

Đây là lớp trung tâm chứa các business rules và logic nghiệp vụ của Publisher:

- **Aggregate Root**: `Publisher` - Đại diện cho entity chính của domain, đóng gói các business rules liên quan đến nhà xuất bản
- **Value Objects**:
    - `PublisherId` - Định danh duy nhất của nhà xuất bản
    - `PublisherName` - Tên nhà xuất bản với các ràng buộc nghiệp vụ (không được trống, độ dài tối đa 256 ký tự)
    - `Address` - Địa chỉ của nhà xuất bản
- **Domain Events**: `PublisherCreatedEvent` - Sự kiện được phát ra khi một nhà xuất bản mới được tạo
- **Domain Services**: `PublisherDomainService` - Chứa các logic nghiệp vụ phức tạp liên quan đến Publisher
- **Repository Interface**: `PublisherRepository` - Interface định nghĩa các phương thức để tương tác với dữ liệu Publisher

### 2.2. Application Layer

Lớp này đóng vai trò điều phối các use cases, kết nối domain layer với các lớp bên ngoài:

- **Application Services**: `PublisherApplicationService` - Xử lý các use cases liên quan đến Publisher
- **DTOs**:
    - `PublisherCreateRequest` - Chứa dữ liệu đầu vào để tạo nhà xuất bản mới
    - `PublisherResponse` - Chứa dữ liệu đầu ra để trả về cho client
- **Application Exceptions**:
    - `PublisherApplicationException` - Exception cơ sở cho các lỗi ở lớp application
    - `PublisherNotFoundException` - Phát sinh khi không tìm thấy nhà xuất bản

### 2.3. Infrastructure Layer

Lớp này cung cấp các triển khai cụ thể cho các interface được định nghĩa ở domain layer:

- **Repository Implementation**: `PublisherRepositoryImpl` - Triển khai `PublisherRepository` interface
- **JPA Entities**: `PublisherJpaEntity` - Entity JPA ánh xạ với bảng trong database
- **Mappers**: `PublisherEntityMapper` - Chuyển đổi giữa domain entity và JPA entity
- **Infrastructure Exceptions**: `PublisherPersistenceException` - Xử lý các lỗi liên quan đến persistence

### 2.4. Interface Layer

Lớp này xử lý tương tác với bên ngoài, như REST API:

- **Controllers**: `PublisherController` - Xử lý các HTTP requests liên quan đến Publisher
- **Exception Handlers**: `PublisherExceptionHandler` - Xử lý các exceptions và chuyển đổi thành HTTP responses

## 3. Luồng hoạt động chính

### 3.1. Tạo nhà xuất bản mới

1. Client gửi HTTP POST request đến `/api/v1/publishers` với thông tin nhà xuất bản
2. `PublisherController` nhận request và chuyển đến `PublisherApplicationService`
3. `PublisherApplicationService` kiểm tra xem tên nhà xuất bản đã tồn tại chưa
4. Nếu chưa tồn tại, gọi `PublisherDomainService` để tạo đối tượng domain `Publisher`
5. `PublisherDomainService` tạo các value objects (`PublisherName`, `Address`) và validate dữ liệu
6. `Publisher` được tạo thông qua factory method và phát sinh `PublisherCreatedEvent`
7. `PublisherApplicationService` lưu `Publisher` thông qua `PublisherRepository`
8. `PublisherRepositoryImpl` chuyển đổi domain entity sang JPA entity và lưu vào database
9. Kết quả được chuyển đổi thành `PublisherResponse` và trả về cho client

### 3.2. Lấy danh sách nhà xuất bản

1. Client gửi HTTP GET request đến `/api/v1/publishers` với tham số phân trang
2. `PublisherController` chuyển request đến `PublisherApplicationService`
3. `PublisherApplicationService` gọi `PublisherRepository` để lấy danh sách nhà xuất bản
4. `PublisherRepositoryImpl` truy vấn database thông qua `PublisherJpaRepository`
5. Kết quả được chuyển đổi từ JPA entities sang domain entities
6. `PublisherApplicationService` chuyển đổi domain entities thành `PublisherResponse` DTOs
7. Kết quả được đóng gói trong `PaginatedResponse` và trả về cho client

### 3.3. Lấy sách theo nhà xuất bản

1. Client gửi HTTP GET request đến `/api/v1/publishers/{publisherId}/books`
2. `PublisherController` chuyển request đến `PublisherApplicationService`
3. `PublisherApplicationService` tìm nhà xuất bản theo ID thông qua `PublisherRepository`
4. Nếu không tìm thấy, ném `PublisherNotFoundException`
5. Nếu tìm thấy, gọi `BookRepository` để lấy sách của nhà xuất bản đó
6. Kết quả được chuyển đổi thành `BookResponse` DTOs và trả về cho client

### 3.4. Xử lý lỗi

1. Nếu có lỗi xảy ra trong quá trình xử lý, exception sẽ được ném ra
2. `PublisherExceptionHandler` bắt các exceptions và chuyển đổi thành HTTP responses phù hợp
3. Client nhận được thông báo lỗi với mã HTTP và mô tả cụ thể

## 4. Testing với cURL

Dưới đây là các lệnh cURL để test API của module Publisher:

### 4.1. Tạo nhà xuất bản mới

```bash
# Tạo nhà xuất bản với thông tin hợp lệ
curl -X POST "http://localhost:8080/api/v1/publishers" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name": "NXB Kim Đồng",
    "address": "55 Quang Trung, Hai Bà Trưng, Hà Nội"
  }'

# Tạo nhà xuất bản với tên trùng lặp (để test xử lý lỗi)
curl -X POST "http://localhost:8080/api/v1/publishers" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name": "NXB Kim Đồng",
    "address": "Địa chỉ khác"
  }'

# Tạo nhà xuất bản với tên trống (để test validation)
curl -X POST "http://localhost:8080/api/v1/publishers" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name": "",
    "address": "Test address"
  }'

# Tạo nhà xuất bản với tên quá dài (để test validation)
curl -X POST "http://localhost:8080/api/v1/publishers" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
    "address": "Test address"
  }'
```

### 4.2. Lấy danh sách nhà xuất bản

```bash
# Lấy danh sách nhà xuất bản với phân trang mặc định
curl -X GET "http://localhost:8080/api/v1/publishers" \
  -H "Accept: application/json"

# Lấy danh sách nhà xuất bản với phân trang tùy chỉnh
curl -X GET "http://localhost:8080/api/v1/publishers?page=0&size=5" \
  -H "Accept: application/json"

# Lấy danh sách nhà xuất bản trang 2, mỗi trang 10 phần tử
curl -X GET "http://localhost:8080/api/v1/publishers?page=1&size=10" \
  -H "Accept: application/json"
```

### 4.3. Lấy sách theo nhà xuất bản

```bash
# Lấy danh sách sách của nhà xuất bản có ID = 1
curl -X GET "http://localhost:8080/api/v1/publishers/1/books" \
  -H "Accept: application/json"

# Lấy danh sách sách của nhà xuất bản với phân trang
curl -X GET "http://localhost:8080/api/v1/publishers/1/books?page=0&size=10" \
  -H "Accept: application/json"

# Lấy sách của nhà xuất bản không tồn tại (để test xử lý lỗi)
curl -X GET "http://localhost:8080/api/v1/publishers/999/books" \
  -H "Accept: application/json"
```

### 4.4. Lấy thông tin nhà xuất bản theo ID

```bash
# Lấy thông tin nhà xuất bản có ID = 1
curl -X GET "http://localhost:8080/api/v1/publishers/1" \
  -H "Accept: application/json"

# Lấy thông tin nhà xuất bản không tồn tại (để test xử lý lỗi)
curl -X GET "http://localhost:8080/api/v1/publishers/999" \
  -H "Accept: application/json"
```

## 5. Lợi ích của việc migrate sang DDD

### 5.1. Tách biệt rõ ràng các concerns

- **Domain Logic**: Được tập trung trong domain layer, không bị ảnh hưởng bởi các chi tiết kỹ thuật
- **Application Logic**: Xử lý use cases và điều phối các services
- **Infrastructure**: Triển khai các chi tiết kỹ thuật như persistence, messaging
- **Interface**: Xử lý tương tác với bên ngoài

### 5.2. Mô hình domain phong phú

- **Value Objects**: Đảm bảo tính toàn vẹn của dữ liệu (ví dụ: `PublisherName` với validation)
- **Domain Events**: Cho phép giao tiếp lỏng lẻo giữa các bounded contexts
- **Aggregate Root**: Đảm bảo tính nhất quán của dữ liệu

### 5.3. Khả năng mở rộng và bảo trì

- **Dễ thêm tính năng mới**: Chỉ cần thêm use cases mới trong application layer
- **Dễ thay đổi chi tiết kỹ thuật**: Có thể thay đổi infrastructure mà không ảnh hưởng đến domain logic
- **Dễ test**: Các layer có thể được test độc lập

### 5.4. Xử lý lỗi tốt hơn

- **Exception hierarchy**: Các exceptions được tổ chức theo layer
- **Centralized exception handling**: Xử lý lỗi tập trung tại interface layer

## 6. Kết luận

Việc migrate module Publisher sang DDD đã mang lại một thiết kế rõ ràng, dễ bảo trì và mở rộng. Domain model phong phú với các value objects và business rules giúp đảm bảo tính toàn vẹn của dữ liệu. Các layer được tách biệt rõ ràng, giúp dễ dàng thay đổi và mở rộng hệ thống trong tương lai.