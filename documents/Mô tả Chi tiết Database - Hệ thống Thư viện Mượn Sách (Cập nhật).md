# Mô tả Chi tiết Database - Hệ thống Thư viện Mượn Sách (Cập nhật)

Tài liệu này cung cấp mô tả chi tiết về cấu trúc cơ sở dữ liệu PostgreSQL cho từng microservice trong Hệ thống Thư viện Mượn Sách, bao gồm User Service, Book Service và Notification Service. **Phiên bản này đã được cập nhật để bổ sung các trường audit (`created_by`, `updated_by`) và cờ xóa mềm (`delete_flg`) theo yêu cầu.**

---

## Mô tả Database - User Service

Dưới đây là mô tả chi tiết cho các bảng trong cơ sở dữ liệu PostgreSQL của User Service, đã được cập nhật với các trường audit và cờ xóa mềm.

### Bảng: `users`

Lưu trữ thông tin cơ bản của tất cả người dùng hệ thống (Độc giả, Thủ thư, Admin).

| Tên cột         | Kiểu dữ liệu | Ràng buộc        | Ý nghĩa                                      | Ví dụ giá trị                 |
|-----------------|--------------|-----------------|----------------------------------------------|-------------------------------|
| `id`            | BIGINT       | PK              | Khóa chính, định danh duy nhất của người dùng     | `101`                         |
| `username`      | VARCHAR      | UK              | Tên đăng nhập duy nhất của người dùng         | `nguyenvana`                  |
| `email`         | VARCHAR      | UK              | Địa chỉ email duy nhất của người dùng         | `vana@example.com`            |
| `password_hash` | VARCHAR      |                 | Mật khẩu đã được băm (hashed)                 | `$2a$10$...` (bcrypt hash)    |
| `first_name`    | VARCHAR      |                 | Tên                                          | `Văn A`                       |
| `last_name`     | VARCHAR      |                 | Họ                                           | `Nguyễn`                      |
| `phone_number`  | VARCHAR      | NULL            | Số điện thoại (tùy chọn)                     | `0901234567`                  |
| `is_active`     | BOOLEAN      | DEFAULT true    | Trạng thái kích hoạt của tài khoản            | `true`                        |
| `created_at`    | TIMESTAMP    | DEFAULT NOW()   | Thời điểm tạo tài khoản                      | `2025-05-23 09:00:00+07`      |
| `updated_at`    | TIMESTAMP    | DEFAULT NOW()   | Thời điểm cập nhật tài khoản lần cuối         | `2025-05-23 10:15:00+07`      |
| `created_by`    | BIGINT       | NULL, FK(users) | ID người dùng tạo bản ghi (NULL nếu là hệ thống) | `1` (Admin ID)                |
| `updated_by`    | BIGINT       | NULL, FK(users) | ID người dùng cập nhật lần cuối (NULL nếu là hệ thống) | `1` (Admin ID)                |
| `delete_flg`    | BOOLEAN      | DEFAULT false   | Cờ xóa mềm (true = đã xóa)                   | `false`                       |

### Bảng: `roles`

Lưu trữ các vai trò khác nhau trong hệ thống.

| Tên cột     | Kiểu dữ liệu | Ràng buộc        | Ý nghĩa                               | Ví dụ giá trị                 |
|-------------|--------------|-----------------|---------------------------------------|-------------------------------|
| `id`        | INT          | PK              | Khóa chính, định danh vai trò          | `1`, `2`, `3`                 |
| `name`      | VARCHAR      | UK              | Tên vai trò (duy nhất)                 | `READER`, `LIBRARIAN`, `ADMIN` |
| `created_at`| TIMESTAMP    | DEFAULT NOW()   | Thời điểm tạo vai trò                  | `2024-10-01 09:00:00+07`      |
| `updated_at`| TIMESTAMP    | DEFAULT NOW()   | Thời điểm cập nhật vai trò lần cuối     | `2024-10-01 09:00:00+07`      |
| `created_by`| BIGINT       | NULL, FK(users) | ID người dùng tạo bản ghi             | `1` (Admin ID)                |
| `updated_by`| BIGINT       | NULL, FK(users) | ID người dùng cập nhật lần cuối         | `1` (Admin ID)                |
| `delete_flg`| BOOLEAN      | DEFAULT false   | Cờ xóa mềm (true = đã xóa)           | `false`                       |

### Bảng: `user_roles`

Bảng trung gian để quản lý mối quan hệ nhiều-nhiều giữa người dùng và vai trò.

| Tên cột     | Kiểu dữ liệu | Ràng buộc        | Ý nghĩa                               | Ví dụ giá trị                 |
|-------------|--------------|-----------------|---------------------------------------|-------------------------------|
| `user_id`   | BIGINT       | PK, FK(users)   | Khóa ngoại tham chiếu đến `users.id`  | `101`                         |
| `role_id`   | INT          | PK, FK(roles)   | Khóa ngoại tham chiếu đến `roles.id` | `1`                           |
| `created_at`| TIMESTAMP    | DEFAULT NOW()   | Thời điểm gán vai trò                 | `2025-05-23 09:05:00+07`      |
| `created_by`| BIGINT       | NULL, FK(users) | ID người dùng thực hiện gán vai trò   | `1` (Admin ID)                |

### Bảng: `library_cards`

Lưu trữ thông tin về thẻ thư viện của độc giả.

| Tên cột       | Kiểu dữ liệu | Ràng buộc        | Ý nghĩa                                      | Ví dụ giá trị                 |
|---------------|--------------|-----------------|----------------------------------------------|-------------------------------|
| `id`          | BIGINT       | PK              | Khóa chính, định danh thẻ thư viện            | `5001`                        |
| `card_number` | VARCHAR      | UK              | Số thẻ thư viện duy nhất                      | `LIB-000101`                  |
| `user_id`     | BIGINT       | FK(users)       | Khóa ngoại tham chiếu đến `users.id`         | `101`                         |
| `issue_date`  | DATE         |                 | Ngày cấp thẻ                                 | `2025-01-15`                  |
| `expiry_date` | DATE         |                 | Ngày hết hạn thẻ                             | `2026-01-15`                  |
| `status`      | VARCHAR      |                 | Trạng thái thẻ (ACTIVE, EXPIRED, LOST)     | `ACTIVE`                      |
| `created_at`  | TIMESTAMP    | DEFAULT NOW()   | Thời điểm tạo bản ghi thẻ                    | `2025-01-15 08:30:00+07`      |
| `updated_at`  | TIMESTAMP    | DEFAULT NOW()   | Thời điểm cập nhật bản ghi thẻ lần cuối      | `2025-01-15 08:30:00+07`      |
| `created_by`  | BIGINT       | NULL, FK(users) | ID người dùng tạo thẻ (Thủ thư/Admin)        | `2` (Librarian ID)            |
| `updated_by`  | BIGINT       | NULL, FK(users) | ID người dùng cập nhật thẻ lần cuối          | `2` (Librarian ID)            |
| `delete_flg`  | BOOLEAN      | DEFAULT false   | Cờ xóa mềm (true = đã xóa/hủy thẻ)         | `false`                       |




---

## Mô tả Database - Book Service

Dưới đây là mô tả chi tiết cho các bảng trong cơ sở dữ liệu PostgreSQL của Book Service, đã được cập nhật với các trường audit và cờ xóa mềm.

### Bảng: `books`

Lưu trữ thông tin về các đầu sách trong thư viện.

| Tên cột            | Kiểu dữ liệu | Ràng buộc        | Ý nghĩa                                      | Ví dụ giá trị                 |
|---------------------|--------------|-----------------|----------------------------------------------|-------------------------------|
| `id`                | BIGINT       | PK              | Khóa chính, định danh duy nhất của đầu sách   | `123`                         |
| `title`             | VARCHAR      |                 | Tên đầu sách                                 | `Lập trình Java Nâng cao`     |
| `isbn`              | VARCHAR      | UK              | Mã số sách chuẩn quốc tế (duy nhất)          | `978-604-1-23456-7`         |
| `publication_year`  | INT          |                 | Năm xuất bản                                 | `2023`                        |
| `description`       | TEXT         | NULL            | Mô tả tóm tắt nội dung sách (tùy chọn)      | `Cuốn sách cung cấp...`       |
| `cover_image_url`   | VARCHAR      | NULL            | Đường dẫn đến ảnh bìa sách (tùy chọn)       | `/images/covers/java.jpg`   |
| `publisher_id`      | BIGINT       | FK(publishers)  | Khóa ngoại tham chiếu đến `publishers.id`    | `15`                          |
| `created_at`        | TIMESTAMP    | DEFAULT NOW()   | Thời điểm tạo bản ghi sách                   | `2025-01-10 14:00:00+07`      |
| `updated_at`        | TIMESTAMP    | DEFAULT NOW()   | Thời điểm cập nhật bản ghi sách lần cuối     | `2025-03-20 11:05:00+07`      |
| `created_by`        | BIGINT       | NULL, FK(users) | ID người dùng tạo sách (Thủ thư/Admin)       | `2` (Librarian ID)            |
| `updated_by`        | BIGINT       | NULL, FK(users) | ID người dùng cập nhật sách lần cuối         | `2` (Librarian ID)            |
| `delete_flg`        | BOOLEAN      | DEFAULT false   | Cờ xóa mềm (true = đã xóa)                   | `false`                       |

### Bảng: `authors`

Lưu trữ thông tin về các tác giả.

| Tên cột     | Kiểu dữ liệu | Ràng buộc        | Ý nghĩa                               | Ví dụ giá trị                 |
|-------------|--------------|-----------------|---------------------------------------|-------------------------------|
| `id`        | BIGINT       | PK              | Khóa chính, định danh tác giả         | `101`                         |
| `name`      | VARCHAR      |                 | Tên tác giả                           | `Nguyễn Văn A`                |
| `created_at`| TIMESTAMP    | DEFAULT NOW()   | Thời điểm tạo bản ghi tác giả        | `2024-12-01 10:00:00+07`      |
| `updated_at`| TIMESTAMP    | DEFAULT NOW()   | Thời điểm cập nhật bản ghi tác giả    | `2024-12-01 10:00:00+07`      |
| `created_by`| BIGINT       | NULL, FK(users) | ID người dùng tạo tác giả             | `2` (Librarian ID)            |
| `updated_by`| BIGINT       | NULL, FK(users) | ID người dùng cập nhật tác giả        | `2` (Librarian ID)            |
| `delete_flg`| BOOLEAN      | DEFAULT false   | Cờ xóa mềm (true = đã xóa)           | `false`                       |

### Bảng: `categories`

Lưu trữ thông tin về các thể loại sách.

| Tên cột     | Kiểu dữ liệu | Ràng buộc        | Ý nghĩa                               | Ví dụ giá trị                 |
|-------------|--------------|-----------------|---------------------------------------|-------------------------------|
| `id`        | BIGINT       | PK              | Khóa chính, định danh thể loại        | `5`                           |
| `name`      | VARCHAR      | UK              | Tên thể loại (duy nhất)               | `Lập trình`                   |
| `created_at`| TIMESTAMP    | DEFAULT NOW()   | Thời điểm tạo bản ghi thể loại       | `2024-11-15 09:00:00+07`      |
| `updated_at`| TIMESTAMP    | DEFAULT NOW()   | Thời điểm cập nhật bản ghi thể loại   | `2024-11-15 09:00:00+07`      |
| `created_by`| BIGINT       | NULL, FK(users) | ID người dùng tạo thể loại            | `1` (Admin ID)                |
| `updated_by`| BIGINT       | NULL, FK(users) | ID người dùng cập nhật thể loại       | `1` (Admin ID)                |
| `delete_flg`| BOOLEAN      | DEFAULT false   | Cờ xóa mềm (true = đã xóa)           | `false`                       |

### Bảng: `publishers`

Lưu trữ thông tin về các nhà xuất bản.

| Tên cột     | Kiểu dữ liệu | Ràng buộc        | Ý nghĩa                               | Ví dụ giá trị                 |
|-------------|--------------|-----------------|---------------------------------------|-------------------------------|
| `id`        | BIGINT       | PK              | Khóa chính, định danh nhà xuất bản    | `15`                          |
| `name`      | VARCHAR      |                 | Tên nhà xuất bản                      | `Nhà xuất bản Giáo dục`       |
| `created_at`| TIMESTAMP    | DEFAULT NOW()   | Thời điểm tạo bản ghi NXB            | `2024-11-10 11:00:00+07`      |
| `updated_at`| TIMESTAMP    | DEFAULT NOW()   | Thời điểm cập nhật bản ghi NXB       | `2024-11-10 11:00:00+07`      |
| `created_by`| BIGINT       | NULL, FK(users) | ID người dùng tạo NXB                 | `1` (Admin ID)                |
| `updated_by`| BIGINT       | NULL, FK(users) | ID người dùng cập nhật NXB            | `1` (Admin ID)                |
| `delete_flg`| BOOLEAN      | DEFAULT false   | Cờ xóa mềm (true = đã xóa)           | `false`                       |

### Bảng: `book_authors`

Bảng trung gian quản lý mối quan hệ nhiều-nhiều giữa sách và tác giả.

| Tên cột     | Kiểu dữ liệu | Ràng buộc        | Ý nghĩa                               | Ví dụ giá trị                 |
|-------------|--------------|-----------------|---------------------------------------|-------------------------------|
| `book_id`   | BIGINT       | PK, FK(books)   | Khóa ngoại tham chiếu đến `books.id`  | `123`                         |
| `author_id` | BIGINT       | PK, FK(authors) | Khóa ngoại tham chiếu đến `authors.id`| `101`                         |
| `created_at`| TIMESTAMP    | DEFAULT NOW()   | Thời điểm tạo liên kết                | `2025-01-10 14:05:00+07`      |
| `created_by`| BIGINT       | NULL, FK(users) | ID người dùng tạo liên kết            | `2` (Librarian ID)            |

### Bảng: `book_categories`

Bảng trung gian quản lý mối quan hệ nhiều-nhiều giữa sách và thể loại.

| Tên cột       | Kiểu dữ liệu | Ràng buộc          | Ý nghĩa                                  | Ví dụ giá trị                 |
|----------------|--------------|-------------------|------------------------------------------|-------------------------------|
| `book_id`      | BIGINT       | PK, FK(books)     | Khóa ngoại tham chiếu đến `books.id`     | `123`                         |
| `category_id`  | BIGINT       | PK, FK(categories)| Khóa ngoại tham chiếu đến `categories.id`| `5`                           |
| `created_at`   | TIMESTAMP    | DEFAULT NOW()     | Thời điểm tạo liên kết                  | `2025-01-10 14:06:00+07`      |
| `created_by`   | BIGINT       | NULL, FK(users)   | ID người dùng tạo liên kết              | `2` (Librarian ID)            |

### Bảng: `book_copies`

Lưu trữ thông tin về từng bản sao cụ thể của một đầu sách.

| Tên cột      | Kiểu dữ liệu | Ràng buộc        | Ý nghĩa                                      | Ví dụ giá trị                 |
|---------------|--------------|-----------------|----------------------------------------------|-------------------------------|
| `id`          | BIGINT       | PK              | Khóa chính, định danh bản sao sách           | `1001`                        |
| `book_id`     | BIGINT       | FK(books)       | Khóa ngoại tham chiếu đến `books.id`         | `123`                         |
| `copy_number` | VARCHAR      | UK              | Mã định danh duy nhất cho bản sao sách       | `JAVA.ADV.001`              |
| `status`      | VARCHAR      |                 | Trạng thái bản sao (AVAILABLE, BORROWED, RESERVED, LOST, DAMAGED) | `AVAILABLE`                 |
| `location`    | VARCHAR      | NULL            | Vị trí sách trên kệ (tùy chọn)              | `Kệ A3`                       |
| `created_at`  | TIMESTAMP    | DEFAULT NOW()   | Thời điểm tạo bản ghi bản sao               | `2025-01-20 15:30:00+07`      |
| `updated_at`  | TIMESTAMP    | DEFAULT NOW()   | Thời điểm cập nhật bản ghi bản sao          | `2025-05-20 10:00:00+07`      |
| `created_by`  | BIGINT       | NULL, FK(users) | ID người dùng tạo bản sao                 | `2` (Librarian ID)            |
| `updated_by`  | BIGINT       | NULL, FK(users) | ID người dùng cập nhật bản sao             | `2` (Librarian ID)            |
| `delete_flg`  | BOOLEAN      | DEFAULT false   | Cờ xóa mềm (true = đã xóa/mất)             | `false`                       |

### Bảng: `borrowings`

Lưu trữ thông tin về các giao dịch mượn sách.

| Tên cột        | Kiểu dữ liệu | Ràng buộc        | Ý nghĩa                                      | Ví dụ giá trị                 |
|-----------------|--------------|-----------------|----------------------------------------------|-------------------------------|
| `id`            | BIGINT       | PK              | Khóa chính, định danh giao dịch mượn         | `5678`                        |
| `user_id`       | BIGINT       | FK (Logical)    | ID người dùng mượn (tham chiếu logic User Service) | `45`                          |
| `book_copy_id`  | BIGINT       | FK(book_copies) | Khóa ngoại tham chiếu đến `book_copies.id`   | `1002`                        |
| `borrow_date`   | DATE         |                 | Ngày mượn sách                               | `2025-05-23`                  |
| `due_date`      | DATE         |                 | Hạn trả sách                                 | `2025-06-15`                  |
| `return_date`   | DATE         | NULL            | Ngày trả sách thực tế (NULL nếu chưa trả)    | `NULL` hoặc `2025-06-10`      |
| `status`        | VARCHAR      |                 | Trạng thái mượn (ACTIVE, RETURNED, OVERDUE) | `ACTIVE`                      |
| `fine_amount`   | DECIMAL      | NULL            | Số tiền phạt nếu trả muộn (NULL nếu không phạt) | `NULL` hoặc `15000.00`        |
| `created_at`    | TIMESTAMP    | DEFAULT NOW()   | Thời điểm tạo giao dịch mượn                | `2025-05-23 10:30:00+07`      |
| `updated_at`    | TIMESTAMP    | DEFAULT NOW()   | Thời điểm cập nhật giao dịch mượn           | `2025-06-10 09:15:00+07`      |
| `created_by`    | BIGINT       | NULL, FK(users) | ID người dùng thực hiện mượn (Thủ thư)       | `2` (Librarian ID)            |
| `updated_by`    | BIGINT       | NULL, FK(users) | ID người dùng thực hiện trả (Thủ thư)        | `2` (Librarian ID)            |
| `delete_flg`    | BOOLEAN      | DEFAULT false   | Cờ xóa mềm (ít dùng, có thể dùng cho hủy giao dịch lỗi) | `false`                       |

### Bảng: `reservations`

Lưu trữ thông tin về các yêu cầu đặt trước sách.

| Tên cột                 | Kiểu dữ liệu | Ràng buộc        | Ý nghĩa                                      | Ví dụ giá trị                 |
|--------------------------|--------------|-----------------|----------------------------------------------|-------------------------------|
| `id`                     | BIGINT       | PK              | Khóa chính, định danh yêu cầu đặt trước      | `901`                         |
| `user_id`                | BIGINT       | FK (Logical)    | ID người dùng đặt trước (tham chiếu logic User Service) | `78`                          |
| `book_id`                | BIGINT       | FK(books)       | Khóa ngoại tham chiếu đến `books.id`         | `123`                         |
| `reservation_date`       | DATE         |                 | Ngày đặt trước                               | `2025-05-20`                  |
| `status`                 | VARCHAR      |                 | Trạng thái đặt trước (PENDING, AVAILABLE, CANCELED, EXPIRED) | `PENDING`                   |
| `notification_sent_date` | DATE         | NULL            | Ngày gửi thông báo sách có sẵn (NULL nếu chưa gửi) | `NULL` hoặc `2025-05-24`      |
| `expiry_date`            | DATE         | NULL            | Ngày hết hạn giữ sách (NULL nếu chưa có sẵn) | `NULL` hoặc `2025-05-27`      |
| `created_at`             | TIMESTAMP    | DEFAULT NOW()   | Thời điểm tạo yêu cầu đặt trước             | `2025-05-20 16:00:00+07`      |
| `updated_at`             | TIMESTAMP    | DEFAULT NOW()   | Thời điểm cập nhật yêu cầu đặt trước        | `2025-05-24 08:00:00+07`      |
| `created_by`             | BIGINT       | NULL, FK(users) | ID người dùng đặt trước (Độc giả)           | `78` (Reader ID)              |
| `updated_by`             | BIGINT       | NULL, FK(users) | ID người dùng cập nhật (Thủ thư/Hệ thống)   | `2` (Librarian ID)            |
| `delete_flg`             | BOOLEAN      | DEFAULT false   | Cờ xóa mềm (true = đã hủy bởi người dùng/admin) | `false`                       |




---

## Mô tả Database - Notification Service

Dưới đây là mô tả chi tiết cho các bảng trong cơ sở dữ liệu PostgreSQL của Notification Service, đã được cập nhật với các trường audit và cờ xóa mềm.

### Bảng: `notifications`

Lưu trữ thông tin về các thông báo đã được gửi hoặc đang chờ gửi đến người dùng.

| Tên cột       | Kiểu dữ liệu | Ràng buộc        | Ý nghĩa                                      | Ví dụ giá trị                 |
|----------------|--------------|-----------------|----------------------------------------------|-------------------------------|
| `id`           | BIGINT       | PK              | Khóa chính, định danh duy nhất của thông báo | `2001`                        |
| `user_id`      | BIGINT       | FK (Logical)    | ID người dùng nhận thông báo (tham chiếu logic User Service) | `45`                          |
| `type`         | VARCHAR      |                 | Loại thông báo (EMAIL, SMS, PUSH)            | `EMAIL`                       |
| `subject`      | VARCHAR      | NULL            | Tiêu đề thông báo (chủ yếu cho EMAIL)        | `Nhắc nhở trả sách`           |
| `content`      | TEXT         |                 | Nội dung chi tiết của thông báo              | `Sách 'Lập trình Java...' sắp hết hạn...` |
| `status`       | VARCHAR      |                 | Trạng thái gửi (PENDING, SENT, FAILED)      | `SENT`                        |
| `scheduled_at` | TIMESTAMP    | NULL            | Thời điểm dự kiến gửi (nếu có)              | `NULL`                        |
| `sent_at`      | TIMESTAMP    | NULL            | Thời điểm gửi thành công (NULL nếu chưa gửi/lỗi) | `2025-06-13 08:00:00+07`      |
| `created_at`   | TIMESTAMP    | DEFAULT NOW()   | Thời điểm tạo bản ghi thông báo             | `2025-06-12 17:00:00+07`      |
| `updated_at`   | TIMESTAMP    | DEFAULT NOW()   | Thời điểm cập nhật bản ghi thông báo        | `2025-06-13 08:00:05+07`      |
| `created_by`   | BIGINT       | NULL, FK(users) | ID người dùng/tiến trình tạo thông báo (NULL nếu là hệ thống tự động) | `NULL`                        |
| `updated_by`   | BIGINT       | NULL, FK(users) | ID người dùng/tiến trình cập nhật trạng thái | `NULL`                        |
| `delete_flg`   | BOOLEAN      | DEFAULT false   | Cờ xóa mềm (ít dùng cho thông báo)          | `false`                       |

### Bảng: `notification_templates`

Lưu trữ các mẫu (template) cho nội dung và tiêu đề thông báo.

| Tên cột           | Kiểu dữ liệu | Ràng buộc        | Ý nghĩa                                      | Ví dụ giá trị                 |
|-------------------|--------------|-----------------|----------------------------------------------|-------------------------------|
| `id`              | BIGINT       | PK              | Khóa chính, định danh mẫu thông báo          | `1`                           |
| `name`            | VARCHAR      | UK              | Tên định danh duy nhất cho mẫu (ví dụ: DUE_DATE_REMINDER) | `DUE_DATE_REMINDER`         |
| `type`            | VARCHAR      |                 | Loại thông báo mẫu áp dụng (EMAIL, SMS, PUSH) | `EMAIL`                       |
| `subject_template`| VARCHAR      | NULL            | Mẫu tiêu đề (chứa placeholder)              | `Thư viện XYZ - Nhắc nhở trả sách` |
| `content_template`| TEXT         |                 | Mẫu nội dung (chứa placeholder)             | `Xin chào {userName}, sách '{bookTitle}' sẽ hết hạn vào ngày {dueDate}...` |
| `is_active`       | BOOLEAN      | DEFAULT true    | Mẫu có đang được sử dụng hay không           | `true`                        |
| `created_at`      | TIMESTAMP    | DEFAULT NOW()   | Thời điểm tạo mẫu                           | `2024-10-01 09:00:00+07`      |
| `updated_at`      | TIMESTAMP    | DEFAULT NOW()   | Thời điểm cập nhật mẫu lần cuối             | `2025-02-15 14:30:00+07`      |
| `created_by`      | BIGINT       | NULL, FK(users) | ID người dùng tạo mẫu (Admin)               | `1` (Admin ID)                |
| `updated_by`      | BIGINT       | NULL, FK(users) | ID người dùng cập nhật mẫu lần cuối (Admin)  | `1` (Admin ID)                |
| `delete_flg`      | BOOLEAN      | DEFAULT false   | Cờ xóa mềm (true = đã xóa)                   | `false`                       |

### Bảng: `notification_preferences`

Lưu trữ tùy chọn nhận thông báo của người dùng.

| Tên cột        | Kiểu dữ liệu | Ràng buộc        | Ý nghĩa                                      | Ví dụ giá trị                 |
|-----------------|--------------|-----------------|----------------------------------------------|-------------------------------|
| `id`            | BIGINT       | PK              | Khóa chính, định danh bản ghi tùy chọn       | `301`                         |
| `user_id`       | BIGINT       | UK, FK(Logical) | ID người dùng (tham chiếu logic User Service, duy nhất) | `78`                          |
| `email_enabled` | BOOLEAN      | DEFAULT true    | Cho phép nhận thông báo qua Email            | `true`                        |
| `sms_enabled`   | BOOLEAN      | DEFAULT false   | Cho phép nhận thông báo qua SMS              | `false`                       |
| `push_enabled`  | BOOLEAN      | DEFAULT false   | Cho phép nhận thông báo qua Push Notification | `true`                        |
| `created_at`    | TIMESTAMP    | DEFAULT NOW()   | Thời điểm tạo bản ghi tùy chọn              | `2025-03-01 11:00:00+07`      |
| `updated_at`    | TIMESTAMP    | DEFAULT NOW()   | Thời điểm cập nhật bản ghi tùy chọn         | `2025-05-10 10:00:00+07`      |
| `created_by`    | BIGINT       | NULL, FK(users) | ID người dùng tạo (thường là chính người dùng đó) | `78` (Reader ID)              |
| `updated_by`    | BIGINT       | NULL, FK(users) | ID người dùng cập nhật (thường là chính người dùng đó) | `78` (Reader ID)              |
| `delete_flg`    | BOOLEAN      | DEFAULT false   | Cờ xóa mềm (ít dùng, có thể dùng khi xóa user) | `false`                       |

