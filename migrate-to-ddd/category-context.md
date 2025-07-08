# Luồng hoạt động và ý nghĩa của module Category sau khi migrate sang DDD

## 1. Tổng quan về module Category

Module Category sau khi migrate sang Domain-Driven Design (DDD) được tổ chức theo các layer rõ ràng, mỗi layer có trách nhiệm riêng biệt và giao tiếp với nhau thông qua các interface được định nghĩa rõ ràng. Điều này giúp tách biệt logic nghiệp vụ khỏi các chi tiết kỹ thuật, làm cho code dễ bảo trì và mở rộng hơn.

## 2. Cấu trúc và các thành phần chính

### 2.1. Domain Layer

Đây là lớp trung tâm chứa các business rules và logic nghiệp vụ của Category:

- **Aggregate Root**: `Category` - Đại diện cho entity chính của domain, đóng gói các business rules liên quan đến thể loại sách
- **Value Objects**:
    - `CategoryId` - Định danh duy nhất của thể loại
    - `CategoryName` - Tên thể loại với các ràng buộc nghiệp vụ (không được trống, độ dài tối đa 256 ký tự)
    - `CategorySlug` - Slug của thể loại với các ràng buộc (không được trống, chỉ chứa chữ thường, số và dấu gạch ngang)
    - `CategoryDescription` - Mô tả của thể loại
- **Domain Events**: `CategoryCreatedEvent` - Sự kiện được phát ra khi một thể loại mới được tạo
- **Domain Services**: `CategoryDomainService` - Chứa các logic nghiệp vụ phức tạp liên quan đến Category
- **Repository Interface**: `CategoryRepository` - Interface định nghĩa các phương thức để tương tác với dữ liệu Category

### 2.2. Application Layer

Lớp này đóng vai trò điều phối các use cases, kết nối domain layer với các lớp bên ngoài:

- **Application Services**: `CategoryApplicationService` - Xử lý các use cases liên quan đến Category
- **DTOs**:
    - `CategoryCreateRequest` - Chứa dữ liệu đầu vào để tạo thể loại mới
    - `CategoryResponse` - Chứa dữ liệu đầu ra để trả về cho client
- **Application Exceptions**:
    - `CategoryApplicationException` - Exception cơ sở cho các lỗi ở lớp application
    - `CategoryNotFoundException` - Phát sinh khi không tìm thấy thể loại

### 2.3. Infrastructure Layer

Lớp này cung cấp các triển khai cụ thể cho các interface được định nghĩa ở domain layer:

- **Repository Implementation**: `CategoryRepositoryImpl` - Triển khai `CategoryRepository` interface
- **JPA Entities**: `CategoryJpaEntity` - Entity JPA ánh xạ với bảng trong database
- **Mappers**: `CategoryEntityMapper` - Chuyển đổi giữa domain entity và JPA entity
- **Infrastructure Exceptions**: `CategoryPersistenceException` - Xử lý các lỗi liên quan đến persistence

### 2.4. Interface Layer

Lớp này xử lý tương tác với bên ngoài, như REST API:

- **Controllers**: `CategoryController` - Xử lý các HTTP requests liên quan đến Category
- **Exception Handlers**: `CategoryExceptionHandler` - Xử lý các exceptions và chuyển đổi thành HTTP responses

## 3. Luồng hoạt động chính

### 3.1. Tạo thể loại mới

1. Client gửi HTTP POST request đến `/api/v1/categories` với thông tin thể loại
2. `CategoryController` nhận request và chuyển đến `CategoryApplicationService`
3. `CategoryApplicationService` kiểm tra xem tên hoặc slug của thể loại đã tồn tại chưa
4. Nếu chưa tồn tại, gọi `CategoryDomainService` để tạo đối tượng domain `Category`
5. `CategoryDomainService` tạo các value objects (`CategoryName`, `CategorySlug`, `CategoryDescription`) và validate dữ liệu
6. `Category` được tạo thông qua factory method và phát sinh `CategoryCreatedEvent`
7. `CategoryApplicationService` lưu `Category` thông qua `CategoryRepository`
8. `CategoryRepositoryImpl` chuyển đổi domain entity sang JPA entity và lưu vào database
9. Kết quả được chuyển đổi thành `CategoryResponse` và trả về cho client

### 3.2. Lấy danh sách thể loại

1. Client gửi HTTP GET request đến `/api/v1/categories` với tham số phân trang
2. `CategoryController` chuyển request đến `CategoryApplicationService`
3. `CategoryApplicationService` gọi `CategoryRepository` để lấy danh sách thể loại
4. `CategoryRepositoryImpl` truy vấn database thông qua `CategoryJpaRepository`
5. Kết quả được chuyển đổi từ JPA entities sang domain entities
6. `CategoryApplicationService` chuyển đổi domain entities thành `CategoryResponse` DTOs
7. Kết quả được đóng gói trong `PaginatedResponse` và trả về cho client

### 3.3. Lấy thông tin thể loại theo ID

1. Client gửi HTTP GET request đến `/api/v1/categories/{categoryId}`
2. `CategoryController` chuyển request đến `CategoryApplicationService`
3. `CategoryApplicationService` gọi `CategoryRepository` để tìm thể loại theo ID
4. Nếu không tìm thấy, ném `CategoryNotFoundException`
5. Nếu tìm thấy, chuyển đổi domain entity thành `CategoryResponse` và trả về cho client

### 3.4. Lấy sách theo thể loại

1. Client gửi HTTP GET request đến `/api/v1/categories/{categoryId}/books`
2. `CategoryController` chuyển request đến `CategoryApplicationService`
3. `CategoryApplicationService` tìm thể loại theo ID thông qua `CategoryRepository`
4. Nếu không tìm thấy, ném `CategoryNotFoundException`
5. Nếu tìm thấy, gọi `BookRepository` để lấy sách của thể loại đó
6. Kết quả được chuyển đổi thành `BookResponse` DTOs và trả về cho client

### 3.5. Xử lý lỗi

1. Nếu có lỗi xảy ra trong quá trình xử lý, exception sẽ được ném ra
2. `CategoryExceptionHandler` bắt các exceptions và chuyển đổi thành HTTP responses phù hợp
3. Client nhận được thông báo lỗi với mã HTTP và mô tả cụ thể

## 4. Testing với cURL

Dưới đây là các lệnh cURL để test API của module Category:

### 4.1. Tạo thể loại mới

```bash
# Tạo thể loại mới với thông tin hợp lệ
curl -X POST "http://localhost:8080/api/v1/categories" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name": "Tiểu thuyết",
    "slug": "tieu-thuyet",
    "description": "Các tác phẩm tiểu thuyết"
  }'

# Tạo thể loại với tên trùng lặp (để test xử lý lỗi)
curl -X POST "http://localhost:8080/api/v1/categories" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name": "Tiểu thuyết",
    "slug": "tieu-thuyet-2",
    "description": "Mô tả khác"
  }'

# Tạo thể loại với slug trùng lặp (để test xử lý lỗi)
curl -X POST "http://localhost:8080/api/v1/categories" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name": "Tiểu thuyết mới",
    "slug": "tieu-thuyet",
    "description": "Mô tả khác"
  }'

# Tạo thể loại với tên trống (để test validation)
curl -X POST "http://localhost:8080/api/v1/categories" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name": "",
    "slug": "test-slug",
    "description": "Test description"
  }'

# Tạo thể loại với tên quá dài (để test validation)
curl -X POST "http://localhost:8080/api/v1/categories" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
    "slug": "test-long-name",
    "description": "Test description"
  }'

# Tạo thể loại với slug không hợp lệ (để test validation)
curl -X POST "http://localhost:8080/api/v1/categories" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name": "Test Category",
    "slug": "Test Slug",
    "description": "Test description"
  }'

# Tạo thể loại với slug chứa ký tự đặc biệt (để test validation)
curl -X POST "http://localhost:8080/api/v1/categories" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name": "Test Category",
    "slug": "test@slug",
    "description": "Test description"
  }'
```

### 4.2. Lấy danh sách thể loại

```bash
# Lấy danh sách thể loại với phân trang mặc định
curl -X GET "http://localhost:8080/api/v1/categories" \
  -H "Accept: application/json"

# Lấy danh sách thể loại với phân trang tùy chỉnh
curl -X GET "http://localhost:8080/api/v1/categories?page=0&size=5" \
  -H "Accept: application/json"

# Lấy danh sách thể loại trang 2, mỗi trang 10 phần tử
curl -X GET "http://localhost:8080/api/v1/categories?page=1&size=10" \
  -H "Accept: application/json"

# Lấy danh sách thể loại với tham số không hợp lệ (để test xử lý lỗi)
curl -X GET "http://localhost:8080/api/v1/categories?page=-1&size=0" \
  -H "Accept: application/json"
```

### 4.3. Lấy thông tin thể loại theo ID

```bash
# Lấy thông tin thể loại có ID = 1
curl -X GET "http://localhost:8080/api/v1/categories/1" \
  -H "Accept: application/json"

# Lấy thông tin thể loại không tồn tại (để test xử lý lỗi)
curl -X GET "http://localhost:8080/api/v1/categories/999" \
  -H "Accept: application/json"

# Lấy thông tin thể loại với ID không hợp lệ (để test xử lý lỗi)
curl -X GET "http://localhost:8080/api/v1/categories/abc" \
  -H "Accept: application/json"
```

### 4.4. Lấy sách theo thể loại

```bash
# Lấy danh sách sách của thể loại có ID = 1
curl -X GET "http://localhost:8080/api/v1/categories/1/books" \
  -H "Accept: application/json"

# Lấy danh sách sách của thể loại với phân trang
curl -X GET "http://localhost:8080/api/v1/categories/1/books?page=0&size=10" \
  -H "Accept: application/json"

# Lấy sách của thể loại không tồn tại (để test xử lý lỗi)
curl -X GET "http://localhost:8080/api/v1/categories/999/books" \
  -H "Accept: application/json"
```

## 5. Lợi ích của việc migrate sang DDD

### 5.1. Tách biệt rõ ràng các concerns

- **Domain Logic**: Được tập trung trong domain layer, không bị ảnh hưởng bởi các chi tiết kỹ thuật
- **Application Logic**: Xử lý use cases và điều phối các services
- **Infrastructure**: Triển khai các chi tiết kỹ thuật như persistence, messaging
- **Interface**: Xử lý tương tác với bên ngoài

### 5.2. Mô hình domain phong phú

- **Value Objects**: Đảm bảo tính toàn vẹn của dữ liệu (ví dụ: `CategoryName`, `CategorySlug` với validation)
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

Việc migrate module Category sang DDD đã mang lại một thiết kế rõ ràng, dễ bảo trì và mở rộng. Domain model phong phú với các value objects và business rules giúp đảm bảo tính toàn vẹn của dữ liệu. Các layer được tách biệt rõ ràng, giúp dễ dàng thay đổi và mở rộng hệ thống trong tương lai.

Các bước tiếp theo có thể bao gồm:
1. Migrate các module khác sang DDD
2. Tích hợp Domain Events với Event Bus để xử lý các sự kiện xuyên service
3. Triển khai CQRS (Command Query Responsibility Segregation) để tách biệt đọc và ghi
4. Thêm các use cases phức tạp hơn như update, delete, search