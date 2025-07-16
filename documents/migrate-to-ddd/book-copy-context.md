# Tổng hợp Module Book Copy sau khi migrate sang DDD và hướng dẫn testing

## Mục lục
1. [Tổng quan về Module Book Copy](#tổng-quan-về-module-book-copy)
2. [Luồng hoạt động](#luồng-hoạt-động)
3. [Ý nghĩa của việc migrate sang DDD](#ý-nghĩa-của-việc-migrate-sang-ddd)
4. [Testing với cURL](#testing-với-curl)
    - [Tạo Book Copy mới](#tạo-book-copy-mới)
    - [Lấy danh sách Book Copy theo Book ID](#lấy-danh-sách-book-copy-theo-book-id)
    - [Lấy thông tin Book Copy theo ID](#lấy-thông-tin-book-copy-theo-id)
    - [Cập nhật Book Copy](#cập-nhật-book-copy)
    - [Cập nhật trạng thái Book Copy](#cập-nhật-trạng-thái-book-copy)
    - [Xóa Book Copy](#xóa-book-copy)
    - [Xử lý lỗi](#xử-lý-lỗi)

## Tổng quan về Module Book Copy

Module Book Copy sau khi migrate sang DDD quản lý các bản sao sách (Book Copy) trong hệ thống thư viện. Mỗi đầu sách (Book) có thể có nhiều bản sao, mỗi bản sao có trạng thái và thông tin riêng. Module này được tách ra từ book-service và đặt trong lending-service vì nó liên quan trực tiếp đến việc cho mượn sách.

### Các thành phần chính:

1. **BookCopy (Aggregate Root)**: Đại diện cho một bản sao sách, chứa các thông tin như ID, tham chiếu đến đầu sách, số bản sao, trạng thái, tình trạng và vị trí.

2. **Value Objects**:
    - `BookCopyId`: Định danh của bản sao sách
    - `BookReference`: Tham chiếu đến đầu sách (chứa bookId và title)
    - `CopyNumber`: Số bản sao
    - `Location`: Vị trí của bản sao
    - `BookCopyStatus`: Trạng thái của bản sao (AVAILABLE, BORROWED, RESERVED, MAINTENANCE, LOST, DAMAGED)
    - `BookCopyCondition`: Tình trạng của bản sao (NEW, EXCELLENT, GOOD, FAIR, POOR, UNUSABLE)

3. **Domain Events**:
    - `BookCopyCreatedEvent`: Phát sinh khi tạo bản sao mới
    - `BookCopyStatusChangedEvent`: Phát sinh khi trạng thái bản sao thay đổi

4. **Services**:
    - `BookCopyDomainService`: Xử lý logic nghiệp vụ liên quan đến bản sao sách
    - `BookCopyApplicationService`: Điều phối các use cases liên quan đến bản sao sách

## Luồng hoạt động

### 1. Tạo Book Copy mới

1. Client gửi request POST đến `/api/copies` với thông tin bản sao mới
2. `BookCopyController` nhận request và chuyển đến `BookCopyApplicationService`
3. `BookCopyApplicationService` gọi `BookCopyDomainService` để tạo bản sao mới
4. `BookCopyDomainService` tạo các Value Objects từ dữ liệu đầu vào
5. `BookCopyDomainService` kiểm tra tính hợp lệ của dữ liệu (ví dụ: copy number không trùng lặp)
6. `BookCopyDomainService` tạo một instance của `BookCopy` thông qua factory method `create()`
7. `BookCopy` tạo một `BookCopyCreatedEvent`
8. `BookCopyDomainService` lưu `BookCopy` thông qua `BookCopyRepository`
9. `BookCopyRepositoryImpl` chuyển đổi domain entity sang JPA entity và lưu vào database
10. Kết quả được chuyển đổi thành DTO và trả về cho client

### 2. Cập nhật trạng thái Book Copy

1. Client gửi request PATCH đến `/api/copies/{bookCopyId}/status` với trạng thái mới
2. `BookCopyController` nhận request và chuyển đến `BookCopyApplicationService`
3. `BookCopyApplicationService` gọi `BookCopyDomainService` để cập nhật trạng thái
4. `BookCopyDomainService` tìm kiếm bản sao theo ID
5. `BookCopyDomainService` gọi phương thức `updateStatus()` của `BookCopy`
6. `BookCopy` kiểm tra tính hợp lệ của trạng thái mới (ví dụ: không thể chuyển từ LOST sang AVAILABLE)
7. `BookCopy` cập nhật trạng thái và tạo một `BookCopyStatusChangedEvent`
8. `BookCopyDomainService` lưu `BookCopy` đã cập nhật
9. Kết quả được chuyển đổi thành DTO và trả về cho client

### 3. Xóa Book Copy

1. Client gửi request DELETE đến `/api/copies/{bookCopyId}`
2. `BookCopyController` nhận request và chuyển đến `BookCopyApplicationService`
3. `BookCopyApplicationService` gọi `BookCopyDomainService` để xóa bản sao
4. `BookCopyDomainService` tìm kiếm bản sao theo ID
5. `BookCopyDomainService` kiểm tra xem bản sao có thể xóa được không (ví dụ: không đang được mượn)
6. `BookCopyDomainService` gọi phương thức `markAsDeleted()` của `BookCopy`
7. `BookCopyDomainService` xóa bản sao thông qua `BookCopyRepository`
8. Kết quả được trả về cho client

## Ý nghĩa của việc migrate sang DDD

1. **Tách biệt rõ ràng giữa các concerns**:
    - Domain Layer chứa logic nghiệp vụ
    - Application Layer điều phối các use cases
    - Infrastructure Layer xử lý kỹ thuật và tương tác với database
    - Interface Layer xử lý tương tác với người dùng

2. **Mô hình phong phú**:
    - Sử dụng Value Objects thay vì primitive types
    - Encapsulate business rules trong domain entities
    - Sử dụng Domain Events để thông báo về các thay đổi quan trọng

3. **Cải thiện khả năng bảo trì**:
    - Code dễ đọc và hiểu hơn
    - Dễ dàng mở rộng và thay đổi
    - Giảm coupling giữa các components

4. **Tăng tính nhất quán**:
    - Sử dụng Ubiquitous Language trong toàn bộ code
    - Business rules được thực thi một cách nhất quán

5. **Tách biệt Book Copy khỏi Book**:
    - Book Copy được quản lý trong lending-service
    - Book được quản lý trong book-service
    - Giảm coupling giữa các services

## Testing với cURL

### Tạo Book Copy mới

```bash
# Tạo một bản sao sách mới
curl -X POST http://localhost:8085/api/copies \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "book_id": 1,
    "book_title": "Clean Code",
    "copy_number": "CC-001",
    "status": "AVAILABLE",
    "condition": "NEW",
    "location": "Shelf A-1"
  }'
```

**Kết quả thành công:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "book_id": 1,
    "book_title": "Clean Code",
    "copy_number": "CC-001",
    "status": "AVAILABLE",
    "condition": "NEW",
    "location": "Shelf A-1",
    "created_at": "2023-07-15T10:30:00",
    "updated_at": "2023-07-15T10:30:00"
  },
  "message": null,
  "errors": null
}
```

### Lấy danh sách Book Copy theo Book ID

```bash
# Lấy tất cả bản sao của một đầu sách
curl -X GET http://localhost:8085/api/copies/books/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Kết quả thành công:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "book_id": 1,
      "book_title": "Clean Code",
      "copy_number": "CC-001",
      "status": "AVAILABLE",
      "condition": "NEW",
      "location": "Shelf A-1",
      "created_at": "2023-07-15T10:30:00",
      "updated_at": "2023-07-15T10:30:00"
    },
    {
      "id": 2,
      "book_id": 1,
      "book_title": "Clean Code",
      "copy_number": "CC-002",
      "status": "BORROWED",
      "condition": "GOOD",
      "location": "Shelf A-1",
      "created_at": "2023-07-15T11:15:00",
      "updated_at": "2023-07-15T14:20:00"
    }
  ],
  "message": null,
  "errors": null
}
```

### Lấy thông tin Book Copy theo ID

```bash
# Lấy thông tin chi tiết của một bản sao
curl -X GET http://localhost:8085/api/copies/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Kết quả thành công:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "book_id": 1,
    "book_title": "Clean Code",
    "copy_number": "CC-001",
    "status": "AVAILABLE",
    "condition": "NEW",
    "location": "Shelf A-1",
    "created_at": "2023-07-15T10:30:00",
    "updated_at": "2023-07-15T10:30:00"
  },
  "message": null,
  "errors": null
}
```

### Cập nhật Book Copy

```bash
# Cập nhật thông tin của một bản sao
curl -X PUT http://localhost:8085/api/copies/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "copy_number": "CC-001",
    "condition": "GOOD",
    "location": "Shelf B-2",
    "status": "AVAILABLE"
  }'
```

**Kết quả thành công:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "book_id": 1,
    "book_title": "Clean Code",
    "copy_number": "CC-001",
    "status": "AVAILABLE",
    "condition": "GOOD",
    "location": "Shelf B-2",
    "created_at": "2023-07-15T10:30:00",
    "updated_at": "2023-07-15T15:45:00"
  },
  "message": null,
  "errors": null
}
```

### Cập nhật trạng thái Book Copy

```bash
# Cập nhật trạng thái của một bản sao
curl -X PATCH "http://localhost:8085/api/copies/1/status?status=BORROWED" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Kết quả thành công:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "book_id": 1,
    "book_title": "Clean Code",
    "copy_number": "CC-001",
    "status": "BORROWED",
    "condition": "GOOD",
    "location": "Shelf B-2",
    "created_at": "2023-07-15T10:30:00",
    "updated_at": "2023-07-15T16:20:00"
  },
  "message": null,
  "errors": null
}
```

### Xóa Book Copy

```bash
# Xóa một bản sao
curl -X DELETE http://localhost:8085/api/copies/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Kết quả thành công:**
```json
{
  "success": true,
  "data": true,
  "message": null,
  "errors": null
}
```

### Xử lý lỗi

#### Trường hợp 1: Book Copy không tồn tại

```bash
curl -X GET http://localhost:8085/api/copies/999 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Kết quả:**
```json
{
  "success": false,
  "data": null,
  "message": null,
  "errors": {
    "status": 404,
    "message": "Book copy with ID 999 not found"
  }
}
```

#### Trường hợp 2: Copy Number đã tồn tại

```bash
curl -X POST http://localhost:8085/api/copies \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "book_id": 1,
    "book_title": "Clean Code",
    "copy_number": "CC-001",
    "status": "AVAILABLE",
    "condition": "NEW",
    "location": "Shelf A-1"
  }'
```

**Kết quả:**
```json
{
  "success": false,
  "data": null,
  "message": null,
  "errors": {
    "status": 400,
    "message": "Copy number already exists for this book: CC-001",
    "field": "copyNumber"
  }
}
```

#### Trường hợp 3: Trạng thái không hợp lệ

```bash
curl -X PATCH "http://localhost:8085/api/copies/1/status?status=INVALID_STATUS" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Kết quả:**
```json
{
  "success": false,
  "data": null,
  "message": null,
  "errors": {
    "status": 400,
    "message": "No enum constant com.library.book.domain.model.bookcopy.BookCopyStatus.INVALID_STATUS",
    "field": "status"
  }
}
```

#### Trường hợp 4: Không thể xóa bản sao đang được mượn

```bash
curl -X DELETE http://localhost:8085/api/copies/2 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Kết quả:**
```json
{
  "success": false,
  "data": null,
  "message": null,
  "errors": {
    "status": 400,
    "message": "Cannot delete book copy that is not in AVAILABLE status",
    "field": "status"
  }
}
```

#### Trường hợp 5: Thay đổi trạng thái không hợp lệ

```bash
curl -X PATCH "http://localhost:8085/api/copies/3/status?status=AVAILABLE" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Kết quả:**
```json
{
  "success": false,
  "data": null,
  "message": null,
  "errors": {
    "status": 400,
    "message": "Cannot change status from LOST to AVAILABLE directly",
    "field": "status"
  }
}
```