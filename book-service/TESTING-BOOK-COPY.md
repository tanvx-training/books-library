# Hướng dẫn test API Quản lý Bản sao Sách

Tài liệu này hướng dẫn cách test các API quản lý bản sao sách trong hệ thống thư viện.

## Chuẩn bị

1. Đảm bảo đã khởi động các dịch vụ cần thiết:
   - PostgreSQL database
   - Eureka Server
   - API Gateway
   - Book Service

2. Import file Postman Collection `book-copy-api-postman.json` vào Postman để có sẵn các request mẫu.

3. Thiết lập biến môi trường trong Postman:
   - `base_url`: http://localhost:8082 (hoặc URL của Book Service)

## Các bước test

### 1. Tạo dữ liệu mẫu

Trước khi test các API quản lý bản sao sách, cần đảm bảo có ít nhất một đầu sách trong hệ thống. Nếu chưa có, sử dụng API tạo sách mới:

```
POST /api/books
```

Với body:

```json
{
  "title": "Sách Test",
  "isbn": "978-3-16-148410-0",
  "publisher_id": 1,
  "publication_year": 2023,
  "description": "Sách dùng để test API",
  "authors": [1],
  "categories": [1]
}
```

### 2. Test các chức năng cơ bản

#### 2.1. Thêm bản sao mới

Thực hiện request "Add New Book Copy" trong collection, thay đổi `book_id` nếu cần:

```json
{
  "book_id": 1,
  "copy_number": "B001-C001",
  "condition": "Mới",
  "location": "Kệ A1-01"
}
```

Thêm một vài bản sao nữa với các thông tin khác nhau để có dữ liệu test.

#### 2.2. Lấy danh sách bản sao của một đầu sách

Thực hiện request "Get Book Copies by Book ID" để xem danh sách các bản sao của một đầu sách.

#### 2.3. Lấy thông tin một bản sao

Thực hiện request "Get Book Copy by ID" để xem thông tin chi tiết của một bản sao cụ thể.

#### 2.4. Cập nhật thông tin bản sao

Thực hiện request "Update Book Copy" để cập nhật thông tin của một bản sao, bao gồm trạng thái, tình trạng và vị trí.

#### 2.5. Cập nhật trạng thái bản sao

Thực hiện request "Update Book Copy Status" để chỉ cập nhật trạng thái của một bản sao.

#### 2.6. Xóa bản sao

Thực hiện request "Delete Book Copy" để xóa một bản sao khỏi hệ thống.

### 3. Test các trường hợp lỗi

#### 3.1. Thêm bản sao với số bản sao đã tồn tại

Thực hiện request "Add Book Copy (Duplicate)" để thử thêm một bản sao với số bản sao đã tồn tại. Hệ thống sẽ trả về lỗi.

#### 3.2. Cập nhật trạng thái không hợp lệ

Thực hiện request "Update Status (Invalid)" để thử cập nhật một trạng thái không hợp lệ. Hệ thống sẽ trả về lỗi.

#### 3.3. Xóa bản sao đang được mượn

Để test trường hợp này:
1. Đầu tiên, thêm một bản sao mới
2. Cập nhật trạng thái của bản sao đó thành "BORROWED" (nếu có quyền admin)
3. Thực hiện request "Delete Borrowed Copy" để thử xóa bản sao đang được mượn. Hệ thống sẽ trả về lỗi.

#### 3.4. Cập nhật trạng thái của bản sao đang được mượn

Thực hiện request "Update Status of Borrowed Copy" để thử cập nhật trạng thái của bản sao đang được mượn. Hệ thống sẽ trả về lỗi.

## Kết quả mong đợi

### Các trường hợp thành công:

- Thêm bản sao mới: Status code 201 Created
- Lấy danh sách bản sao: Status code 200 OK, trả về danh sách các bản sao
- Lấy thông tin bản sao: Status code 200 OK, trả về thông tin chi tiết của bản sao
- Cập nhật bản sao: Status code 200 OK, trả về thông tin đã cập nhật
- Cập nhật trạng thái: Status code 200 OK, trả về thông tin với trạng thái mới
- Xóa bản sao: Status code 200 OK, trả về thông báo thành công

### Các trường hợp lỗi:

- Thêm bản sao trùng số: Status code 400 Bad Request, thông báo lỗi về số bản sao đã tồn tại
- Cập nhật trạng thái không hợp lệ: Status code 400 Bad Request, thông báo lỗi về trạng thái không hợp lệ
- Xóa bản sao đang mượn: Status code 400 Bad Request, thông báo lỗi không thể xóa bản sao đang được mượn
- Cập nhật trạng thái bản sao đang mượn: Status code 400 Bad Request, thông báo lỗi không thể thay đổi trạng thái

## Lưu ý

- Các trạng thái hợp lệ của bản sao sách bao gồm: AVAILABLE, BORROWED, RESERVED, MAINTENANCE, LOST, DAMAGED.
- Không thể thay đổi trạng thái BORROWED hoặc RESERVED thủ công, vì chúng được quản lý bởi hệ thống mượn/trả sách.
- Chỉ có thể xóa bản sao có trạng thái AVAILABLE và không có đặt trước nào liên quan. 