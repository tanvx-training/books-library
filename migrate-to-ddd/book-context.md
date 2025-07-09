# Module Book - Tổng quan sau khi migrate sang Domain-Driven Design

## 1. Kiến trúc tổng thể

Module Book đã được tái cấu trúc theo mô hình Domain-Driven Design (DDD) với 4 layer chính:

### 1.1. Domain Layer
- Chứa các entity, value object, domain event và business logic
- Tập trung vào các quy tắc nghiệp vụ và ràng buộc
- Độc lập với các công nghệ bên ngoài

### 1.2. Application Layer
- Điều phối các use case và luồng nghiệp vụ
- Sử dụng domain service để thực hiện các thao tác
- Chuyển đổi giữa DTO và domain model

### 1.3. Infrastructure Layer
- Cung cấp triển khai cụ thể cho các repository
- Xử lý persistence, caching, messaging
- Chuyển đổi giữa domain model và JPA entity

### 1.4. Interfaces Layer
- Cung cấp API cho người dùng (REST API)
- Xử lý validation đầu vào
- Xử lý exception và trả về response phù hợp

## 2. Luồng hoạt động

### 2.1. Luồng tạo sách mới

1. `BookController` nhận request từ client
2. Request được validate bởi annotation trong `BookCreateRequest`
3. `BookApplicationService.createBook()` được gọi
4. Kiểm tra trùng ISBN
5. `BookDomainService.createNewBook()` tạo domain object `Book`
6. Các value object (`BookTitle`, `ISBN`, `PublicationYear`...) được tạo và validate
7. Domain event `BookCreatedEvent` được tạo
8. `BookRepository.save()` được gọi
9. `BookRepositoryImpl` chuyển đổi domain object sang JPA entity
10. `BookJpaRepository` lưu entity vào database
11. Response được trả về cho client

### 2.2. Luồng cập nhật sách

1. `BookController` nhận request từ client
2. `BookApplicationService.updateBook()` được gọi
3. `BookRepository.findById()` tìm sách theo ID
4. Domain object `Book` được cập nhật thông qua các method như `updateTitle()`, `updateISBN()`...
5. Các value object mới được tạo và validate
6. `BookRepository.save()` lưu thay đổi
7. Response được trả về cho client

### 2.3. Luồng tìm kiếm sách

1. `BookController` nhận request tìm kiếm
2. `BookApplicationService.searchBooks()` được gọi
3. `BookRepository.findAllByTitle()` thực hiện tìm kiếm
4. `BookRepositoryImpl` chuyển đổi JPA entity sang domain object
5. `BookApplicationService` chuyển đổi domain object sang DTO
6. Response được trả về cho client

## 3. Ý nghĩa của việc migrate sang DDD

### 3.1. Lợi ích

- **Tập trung vào nghiệp vụ**: Domain model phản ánh chính xác các khái niệm và quy tắc nghiệp vụ
- **Tách biệt mối quan tâm**: Mỗi layer có trách nhiệm rõ ràng
- **Bảo vệ tính toàn vẹn dữ liệu**: Validation được thực hiện trong domain model
- **Dễ dàng mở rộng**: Có thể thêm các business rule mới mà không ảnh hưởng đến các layer khác
- **Dễ dàng test**: Các layer có thể được test độc lập

### 3.2. Các thay đổi chính

- **Value Objects**: Sử dụng value object cho các thuộc tính có ý nghĩa nghiệp vụ
- **Domain Events**: Sử dụng event để thông báo về các thay đổi quan trọng
- **Repository Interface**: Định nghĩa interface trong domain layer
- **Validation**: Validation được thực hiện trong domain model
- **Exception Handling**: Xử lý exception theo từng layer

## 4. CURL Commands để test API

### 4.1. Tạo sách mới

```bash
curl -X POST http://localhost:8080/api/v1/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "title": "Domain-Driven Design: Tackling Complexity in the Heart of Software",
    "isbn": "978-0321125217",
    "publisher_id": 1,
    "publication_year": 2003,
    "description": "This book is a must-read for software developers and architects.",
    "cover_image_url": "https://example.com/ddd-book.jpg",
    "author_ids": [1],
    "category_ids": [2, 3]
  }'
```

### 4.2. Lấy thông tin sách theo ID

```bash
curl -X GET http://localhost:8080/api/v1/books/1 \
  -H "Authorization: Bearer {token}"
```

### 4.3. Tìm kiếm sách

```bash
curl -X GET "http://localhost:8080/api/v1/books/search?keyword=Domain&page=0&size=10" \
  -H "Authorization: Bearer {token}"
```

### 4.4. Lấy danh sách sách (phân trang)

```bash
curl -X GET "http://localhost:8080/api/v1/books?page=0&size=10" \
  -H "Authorization: Bearer {token}"
```

### 4.5. Cập nhật sách

```bash
curl -X PUT http://localhost:8080/api/v1/books/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "title": "Domain-Driven Design: Tackling Complexity in the Heart of Software (Updated Edition)",
    "isbn": "978-0321125217",
    "publisher_id": 1,
    "publication_year": 2003,
    "description": "This book is a must-read for software developers and architects. Updated with new insights.",
    "cover_image_url": "https://example.com/ddd-book-updated.jpg",
    "author_ids": [1],
    "category_ids": [2, 3, 4]
  }'
```

### 4.6. Xóa sách

```bash
curl -X DELETE http://localhost:8080/api/v1/books/1 \
  -H "Authorization: Bearer {token}"
```

### 4.7. Test case cho lỗi validation

```bash
# ISBN trống
curl -X POST http://localhost:8080/api/v1/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "title": "Test Book",
    "isbn": "",
    "publisher_id": 1,
    "author_ids": [1],
    "category_ids": [2]
  }'

# Tiêu đề quá dài (> 200 ký tự)
curl -X POST http://localhost:8080/api/v1/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "title": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
    "isbn": "978-1234567890",
    "publisher_id": 1,
    "author_ids": [1],
    "category_ids": [2]
  }'

# ISBN đã tồn tại
curl -X POST http://localhost:8080/api/v1/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "title": "Another Book with Same ISBN",
    "isbn": "978-0321125217",
    "publisher_id": 1,
    "author_ids": [1],
    "category_ids": [2]
  }'
```

### 4.8. Test case cho lỗi not found

```bash
# Book ID không tồn tại
curl -X GET http://localhost:8080/api/v1/books/999 \
  -H "Authorization: Bearer {token}"

# Cập nhật book không tồn tại
curl -X PUT http://localhost:8080/api/v1/books/999 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "title": "Updated Book",
    "isbn": "978-9876543210",
    "publisher_id": 1,
    "author_ids": [1],
    "category_ids": [2]
  }'
```

## 5. Các bước tiếp theo

1. **Hoàn thiện xử lý Domain Events**: Triển khai xử lý event để thông báo cho các service khác
2. **Tích hợp với Lending Service**: Xây dựng API để lending service có thể truy vấn thông tin sách
3. **Caching**: Thêm caching để cải thiện hiệu suất
4. **Testing**: Viết unit test và integration test cho từng layer
5. **Documentation**: Cập nhật tài liệu API và domain model