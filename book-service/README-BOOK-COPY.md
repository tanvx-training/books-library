# Book Copy Management API Documentation

Tài liệu này mô tả chi tiết các API để quản lý bản sao sách (Book Copy) trong hệ thống thư viện.

## Tổng quan

Book Copy là một bản sao vật lý của một đầu sách. Mỗi đầu sách có thể có nhiều bản sao khác nhau, mỗi bản sao có một số bản sao (copy number) riêng biệt, trạng thái, tình trạng vật lý và vị trí trên kệ.

### Các trạng thái của Book Copy

- `AVAILABLE`: Có sẵn (sẵn sàng cho mượn)
- `BORROWED`: Đang được mượn
- `RESERVED`: Đã được đặt trước
- `MAINTENANCE`: Đang bảo trì
- `LOST`: Bị mất
- `DAMAGED`: Bị hỏng

## Danh sách API

### 1. Lấy danh sách bản sao của một đầu sách

- **URL**: `/api/copies/{bookId}/books`
- **Method**: `GET`
- **Mô tả**: Lấy danh sách tất cả các bản sao của một đầu sách cụ thể
- **Path Parameters**:
  - `bookId`: ID của đầu sách

#### Ví dụ Response:

```json
[
  {
    "id": 1,
    "copy_number": "B001-C001",
    "status": "AVAILABLE",
    "condition": "Mới",
    "location": "Kệ A1-01"
  },
  {
    "id": 2,
    "copy_number": "B001-C002",
    "status": "BORROWED",
    "condition": "Tốt",
    "location": "Kệ A1-02"
  }
]
```

### 2. Thêm bản sao mới

- **URL**: `/api/copies`
- **Method**: `POST`
- **Mô tả**: Thêm một bản sao mới cho một đầu sách
- **Request Body**:

```json
{
  "book_id": 1,
  "copy_number": "B001-C003",
  "condition": "Mới",
  "location": "Kệ A1-03",
  "status": "AVAILABLE"
}
```

- **Lưu ý**: Trường `status` là tùy chọn. Nếu không cung cấp, giá trị mặc định là `AVAILABLE`.

#### Ví dụ Response:

```json
{
  "id": 3,
  "copy_number": "B001-C003",
  "status": "AVAILABLE",
  "condition": "Mới",
  "location": "Kệ A1-03"
}
```

### 3. Lấy thông tin một bản sao

- **URL**: `/api/copies/{bookCopyId}`
- **Method**: `GET`
- **Mô tả**: Lấy thông tin chi tiết của một bản sao cụ thể
- **Path Parameters**:
  - `bookCopyId`: ID của bản sao

#### Ví dụ Response:

```json
{
  "id": 1,
  "copy_number": "B001-C001",
  "status": "AVAILABLE",
  "condition": "Mới",
  "location": "Kệ A1-01"
}
```

### 4. Cập nhật thông tin bản sao

- **URL**: `/api/copies/{bookCopyId}`
- **Method**: `PUT`
- **Mô tả**: Cập nhật thông tin của một bản sao
- **Path Parameters**:
  - `bookCopyId`: ID của bản sao
- **Request Body**:

```json
{
  "book_id": 1,
  "copy_number": "B001-C001",
  "condition": "Cũ",
  "location": "Kệ A1-05",
  "status": "MAINTENANCE"
}
```

#### Ví dụ Response:

```json
{
  "id": 1,
  "copy_number": "B001-C001",
  "status": "MAINTENANCE",
  "condition": "Cũ",
  "location": "Kệ A1-05"
}
```

### 5. Cập nhật trạng thái bản sao

- **URL**: `/api/copies/{bookCopyId}/status`
- **Method**: `PATCH`
- **Mô tả**: Cập nhật trạng thái của một bản sao
- **Path Parameters**:
  - `bookCopyId`: ID của bản sao
- **Query Parameters**:
  - `status`: Trạng thái mới (AVAILABLE, MAINTENANCE, LOST, DAMAGED)

#### Ví dụ Request:

```
PATCH /api/copies/1/status?status=MAINTENANCE
```

#### Ví dụ Response:

```json
{
  "id": 1,
  "copy_number": "B001-C001",
  "status": "MAINTENANCE",
  "condition": "Mới",
  "location": "Kệ A1-01"
}
```

### 6. Xóa bản sao

- **URL**: `/api/copies/{bookCopyId}`
- **Method**: `DELETE`
- **Mô tả**: Xóa một bản sao khỏi hệ thống
- **Path Parameters**:
  - `bookCopyId`: ID của bản sao
- **Lưu ý**: Chỉ có thể xóa bản sao có trạng thái là `AVAILABLE` và không có đặt trước nào liên quan.

#### Ví dụ Response:

```json
{
  "timestamp": "2023-07-10 15:30:45",
  "success": true,
  "message": "Bản sao sách đã được xóa thành công",
  "data": true,
  "errors": null
}
```

## Dữ liệu mẫu để test

### 1. Thêm bản sao mới

```json
{
  "book_id": 1,
  "copy_number": "B001-C001",
  "condition": "Mới",
  "location": "Kệ A1-01"
}
```

```json
{
  "book_id": 1,
  "copy_number": "B001-C002",
  "condition": "Tốt",
  "location": "Kệ A1-02",
  "status": "AVAILABLE"
}
```

```json
{
  "book_id": 2,
  "copy_number": "B002-C001",
  "condition": "Mới",
  "location": "Kệ B2-01"
}
```

### 2. Cập nhật bản sao

```json
{
  "book_id": 1,
  "copy_number": "B001-C001",
  "condition": "Cũ",
  "location": "Kệ A1-05",
  "status": "MAINTENANCE"
}
```

### 3. Cập nhật trạng thái

Sử dụng query parameter: `?status=DAMAGED`

### 4. Các trường hợp lỗi cần test

#### Thêm bản sao với số bản sao đã tồn tại

```json
{
  "book_id": 1,
  "copy_number": "B001-C001",
  "condition": "Mới",
  "location": "Kệ A1-01"
}
```

#### Cập nhật trạng thái không hợp lệ

Sử dụng query parameter: `?status=INVALID_STATUS`

#### Xóa bản sao đang được mượn

Thử xóa bản sao có ID của bản sao đang ở trạng thái `BORROWED`

#### Cập nhật trạng thái của bản sao đang được mượn

```
PATCH /api/copies/{id}/status?status=AVAILABLE
```

## Quy tắc nghiệp vụ

1. Số bản sao (copy number) phải là duy nhất cho mỗi đầu sách.
2. Không thể xóa bản sao đang được mượn hoặc đặt trước.
3. Trạng thái "Đang mượn" (BORROWED), "Đặt trước" (RESERVED) thường được cập nhật tự động bởi chức năng mượn/trả/đặt trước, không nên cho phép cập nhật thủ công (trừ trường hợp đặc biệt).
4. Chỉ có thể xóa bản sao có trạng thái là "Có sẵn" (AVAILABLE).
5. Không thể thay đổi trạng thái của sách đang được mượn. 