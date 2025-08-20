# Thiết kế chi tiết cơ sở dữ liệu cho hệ thống quản lý thư viện

## 1. Giới thiệu

Bản thiết kế này cung cấp một cái nhìn chi tiết về cấu trúc cơ sở dữ liệu cho hệ thống quản lý thư viện, dựa trên file `postgresql_init.sql` hiện có và các ảnh chụp màn hình chức năng được cung cấp. Mục tiêu là tối ưu hóa cấu trúc dữ liệu, áp dụng các best practices trong thiết kế cơ sở dữ liệu và giải thích rõ ràng mục đích của từng bảng và trường dữ liệu.

## 2. Phân tích các thành phần hiện có

### 2.1. Phân tích file `postgresql_init.sql`

File `postgresql_init.sql` đã cung cấp một cấu trúc cơ sở dữ liệu ban đầu được chia thành ba dịch vụ chính:

*   **User Service**: Quản lý thông tin người dùng và thẻ thư viện.
*   **Book Service**: Quản lý thông tin sách, tác giả, nhà xuất bản, danh mục, bản sao sách, mượn trả và đặt trước.
*   **Notification Service**: Quản lý các mẫu thông báo, tùy chọn thông báo và lịch sử thông báo.

Sau đây là phân tích chi tiết từng bảng:

#### 2.1.1. Bảng `users` (User Service)

```sql
create table users
(
    id           bigint      not null
        primary key,
    keycloak_id  varchar(36) not null
        unique,
    username     varchar
        unique,
    email        varchar
        unique,
    first_name   varchar,
    last_name    varchar,
    phone_number varchar,
    is_active    boolean   default true,
    created_at   timestamp default now(),
    updated_at   timestamp default now(),
    created_by   varchar(36),
    updated_by   varchar(36),
    delete_flg   boolean   default false
);
```

**Phân tích:**

*   `id`: `bigint` làm khóa chính. Tốt cho các hệ thống lớn. Tuy nhiên, `SERIAL` hoặc `BIGSERIAL` thường được ưu tiên cho các khóa chính tự tăng trong PostgreSQL để đơn giản hóa việc quản lý ID.
*   `keycloak_id`: `varchar(36)` là một `UUID` từ Keycloak, được sử dụng làm định danh duy nhất cho người dùng trong hệ thống xác thực bên ngoài. Đây là một cách tiếp cận tốt cho tích hợp SSO.
*   `username`, `email`: `varchar` với `unique` constraint. Cần xem xét độ dài tối đa cho các trường này để tối ưu hóa lưu trữ và hiệu suất.
*   `first_name`, `last_name`, `phone_number`: `varchar`. Các trường thông tin cá nhân cơ bản.
*   `is_active`: `boolean` với `default true`. Cờ trạng thái hoạt động của tài khoản.
*   `created_at`, `updated_at`: `timestamp` với `default now()`. Tốt cho việc theo dõi thời gian tạo và cập nhật bản ghi.
*   `created_by`, `updated_by`: `varchar(36)`. Lưu trữ `keycloak_id` của người dùng thực hiện thao tác. Cần đảm bảo tính nhất quán về kiểu dữ liệu nếu `keycloak_id` là `UUID`.
*   `delete_flg`: `boolean` với `default false`. Cờ xóa mềm (soft delete), một best practice tốt để duy trì lịch sử dữ liệu.

**Đề xuất cải tiến:**

*   Thay đổi `id` thành `BIGSERIAL PRIMARY KEY` để tự động quản lý ID.
*   Xác định độ dài cụ thể cho `username`, `email`, `first_name`, `last_name`, `phone_number` để tối ưu hóa lưu trữ và validation.
*   Xem xét thêm các trường như `address`, `date_of_birth` nếu cần cho nghiệp vụ quản lý người dùng chi tiết hơn.

#### 2.1.2. Bảng `library_cards` (User Service)

```sql
create table library_cards
(
    id          bigint not null
        primary key,
    card_number varchar
        unique,
    user_id     bigint
        references users,
    issue_date  date,
    expiry_date date,
    status      varchar,
    created_at  timestamp default now(),
    updated_at  timestamp default now(),
    created_by  varchar(36),
    updated_by  varchar(36),
    delete_flg  boolean   default false
);
```

**Phân tích:**

*   `id`: `bigint` làm khóa chính. Tương tự bảng `users`, nên dùng `BIGSERIAL`.
*   `card_number`: `varchar` với `unique` constraint. Số thẻ thư viện duy nhất.
*   `user_id`: `bigint` tham chiếu đến `users.id`. Thiết lập quan hệ 1-1 hoặc 1-nếu một người dùng có thể có nhiều thẻ (ví dụ: thẻ chính, thẻ phụ).
*   `issue_date`, `expiry_date`: `date`. Ngày cấp và ngày hết hạn của thẻ.
*   `status`: `varchar`. Trạng thái của thẻ (ví dụ: `ACTIVE`, `INACTIVE`, `EXPIRED`, `LOST`). Nên sử dụng `ENUM` hoặc `CHECK` constraint để giới hạn các giá trị hợp lệ.
*   Các trường `created_at`, `updated_at`, `created_by`, `updated_by`, `delete_flg` tương tự như bảng `users`.

**Đề xuất cải tiến:**

*   Thay đổi `id` thành `BIGSERIAL PRIMARY KEY`.
*   Sử dụng `ENUM` hoặc `CHECK` constraint cho trường `status` để đảm bảo tính toàn vẹn dữ liệu.
*   Xác định độ dài cụ thể cho `card_number` và `status`.

#### 2.1.3. Bảng `authors` (Book Service)

```sql
CREATE TABLE authors
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100)                          NOT NULL,
    biography  TEXT,
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at TIMESTAMP,
    updated_by VARCHAR(20),
    delete_flg BOOLEAN     DEFAULT FALSE
);
```

**Phân tích:**

*   `id`: `SERIAL PRIMARY KEY`. Tốt.
*   `name`: `VARCHAR(100) NOT NULL`. Tên tác giả. Độ dài 100 ký tự là hợp lý.
*   `biography`: `TEXT`. Thông tin tiểu sử tác giả. `TEXT` là phù hợp cho nội dung dài.
*   Các trường `created_at`, `created_by`, `updated_at`, `updated_by`, `delete_flg` tương tự các bảng trên.

**Đề xuất cải tiến:**

*   Không có đề xuất cải tiến lớn, cấu trúc này khá tốt.

#### 2.1.4. Bảng `categories` (Book Service)

```sql
CREATE TABLE categories
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(256) UNIQUE                   NOT NULL,
    slug        VARCHAR(256) UNIQUE                   NOT NULL,
    description TEXT,
    created_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by  VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at  TIMESTAMP,
    updated_by  VARCHAR(20),
    delete_flg  BOOLEAN     DEFAULT FALSE
);
```

**Phân tích:**

*   `id`: `SERIAL PRIMARY KEY`. Tốt.
*   `name`: `VARCHAR(256) UNIQUE NOT NULL`. Tên danh mục, đảm bảo duy nhất.
*   `slug`: `VARCHAR(256) UNIQUE NOT NULL`. Slug cho URL thân thiện, đảm bảo duy nhất. Đây là một best practice tốt cho các hệ thống có SEO hoặc cần định danh dễ đọc.
*   `description`: `TEXT`. Mô tả danh mục.
*   Các trường quản lý thời gian và người tạo/cập nhật tương tự.

**Đề xuất cải tiến:**

*   Không có đề xuất cải tiến lớn, cấu trúc này tốt.

#### 2.1.5. Bảng `publishers` (Book Service)

```sql
CREATE TABLE publishers
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(256)                          NOT NULL,
    address    TEXT,
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at TIMESTAMP,
    updated_by VARCHAR(20),
    delete_flg BOOLEAN     DEFAULT FALSE
);
```

**Phân tích:**

*   `id`: `SERIAL PRIMARY KEY`. Tốt.
*   `name`: `VARCHAR(256) NOT NULL`. Tên nhà xuất bản.
*   `address`: `TEXT`. Địa chỉ nhà xuất bản. `TEXT` phù hợp cho địa chỉ dài.
*   Các trường quản lý thời gian và người tạo/cập nhật tương tự.

**Đề xuất cải tiến:**

*   Không có đề xuất cải tiến lớn, cấu trúc này tốt.

#### 2.1.6. Bảng `books` (Book Service)

```sql
CREATE TABLE books
(
    id               SERIAL PRIMARY KEY,
    title            VARCHAR(256)                          NOT NULL,
    isbn             VARCHAR(20) UNIQUE,
    publisher_id     INT REFERENCES publishers (id),
    publication_year INT,
    description      TEXT,
    cover_image_url  VARCHAR(1000),
    created_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by       VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at       TIMESTAMP,
    updated_by       VARCHAR(20),
    delete_flg       BOOLEAN     DEFAULT FALSE
);
```

**Phân tích:**

*   `id`: `SERIAL PRIMARY KEY`. Tốt.
*   `title`: `VARCHAR(256) NOT NULL`. Tiêu đề sách.
*   `isbn`: `VARCHAR(20) UNIQUE`. Mã ISBN duy nhất. Độ dài 20 ký tự là đủ cho ISBN-10 và ISBN-13.
*   `publisher_id`: `INT REFERENCES publishers (id)`. Khóa ngoại đến bảng `publishers`.
*   `publication_year`: `INT`. Năm xuất bản. `INT` là phù hợp.
*   `description`: `TEXT`. Mô tả sách.
*   `cover_image_url`: `VARCHAR(1000)`. URL ảnh bìa. Độ dài 1000 ký tự là đủ cho URL.
*   Các trường quản lý thời gian và người tạo/cập nhật tương tự.

**Đề xuất cải tiến:**

*   Không có đề xuất cải tiến lớn, cấu trúc này tốt.

#### 2.1.7. Bảng `book_authors` (Book Service)

```sql
CREATE TABLE book_authors
(
    book_id   INT REFERENCES books (id),
    author_id INT REFERENCES authors (id),
    PRIMARY KEY (book_id, author_id)
);
```

**Phân tích:**

*   Bảng trung gian cho quan hệ nhiều-nhiều giữa `books` và `authors`. Khóa chính là cặp `(book_id, author_id)`. Đây là một best practice cho quan hệ nhiều-nhiều.

**Đề xuất cải tiến:**

*   Không có đề xuất cải tiến.

#### 2.1.8. Bảng `book_categories` (Book Service)

```sql
CREATE TABLE book_categories
(
    book_id   INT REFERENCES books (id),
    category_id INT REFERENCES categories (id),
    PRIMARY KEY (book_id, category_id)
);
```

**Phân tích:**

*   Bảng trung gian cho quan hệ nhiều-nhiều giữa `books` và `categories`. Khóa chính là cặp `(book_id, category_id)`. Đây là một best practice cho quan hệ nhiều-nhiều.

**Đề xuất cải tiến:**

*   Không có đề xuất cải tiến.

#### 2.1.9. Bảng `book_copies` (Book Service)

```sql
CREATE TABLE book_copies
(
    id          SERIAL PRIMARY KEY,
    book_id     INT REFERENCES books (id),
    copy_number VARCHAR(20)                           NOT NULL,
    status      VARCHAR(20)                           NOT NULL, -- AVAILABLE, BORROWED, RESERVED, MAINTENANCE
    condition   VARCHAR(20),                                    -- NEW, GOOD, FAIR, POOR
    location    VARCHAR(50),                                    -- Shelf location
    created_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by  VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at  TIMESTAMP,
    updated_by  VARCHAR(20),
    delete_flg  BOOLEAN     DEFAULT FALSE
        UNIQUE (book_id, copy_number)
);
```

**Phân tích:**

*   `id`: `SERIAL PRIMARY KEY`. Tốt.
*   `book_id`: `INT REFERENCES books (id)`. Khóa ngoại đến bảng `books`.
*   `copy_number`: `VARCHAR(20) NOT NULL`. Số bản sao của sách. `UNIQUE (book_id, copy_number)` đảm bảo mỗi bản sao là duy nhất cho một cuốn sách cụ thể.
*   `status`: `VARCHAR(20) NOT NULL`. Trạng thái của bản sao (ví dụ: `AVAILABLE`, `BORROWED`, `RESERVED`, `MAINTENANCE`). Nên sử dụng `ENUM` hoặc `CHECK` constraint.
*   `condition`: `VARCHAR(20)`. Tình trạng của bản sao (ví dụ: `NEW`, `GOOD`, `FAIR`, `POOR`). Nên sử dụng `ENUM` hoặc `CHECK` constraint.
*   `location`: `VARCHAR(50)`. Vị trí trên kệ. Độ dài 50 ký tự là hợp lý.
*   Các trường quản lý thời gian và người tạo/cập nhật tương tự.

**Đề xuất cải tiến:**

*   Sử dụng `ENUM` hoặc `CHECK` constraint cho trường `status` và `condition`.
*   Xác định độ dài cụ thể cho `copy_number` và `location`.

#### 2.1.10. Bảng `borrowings` (Book Service)

```sql
CREATE TABLE borrowings
(
    id           SERIAL PRIMARY KEY,
    book_copy_id INT REFERENCES book_copies (id),
    user_id      INT                                      NOT NULL, -- References user_id from user-service
    borrow_date  DATE                                     NOT NULL,
    due_date     DATE                                     NOT NULL,
    return_date  DATE,
    fine_amount  DECIMAL(10, 2) DEFAULT 0.00,
    status       VARCHAR(20)                              NOT NULL, -- ACTIVE, RETURNED, OVERDUE
    created_at   TIMESTAMP      DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by   VARCHAR(20)    DEFAULT 'SYSTEM'          NOT NULL,
    updated_at   TIMESTAMP,
    updated_by   VARCHAR(20),
    delete_flg   BOOLEAN        DEFAULT FALSE
);
```

**Phân tích:**

*   `id`: `SERIAL PRIMARY KEY`. Tốt.
*   `book_copy_id`: `INT REFERENCES book_copies (id)`. Khóa ngoại đến bảng `book_copies`.
*   `user_id`: `INT NOT NULL`. Tham chiếu đến `users.id` từ user-service. Cần đảm bảo kiểu dữ liệu nhất quán với `users.id` (nếu `users.id` là `BIGINT`, thì `user_id` ở đây cũng nên là `BIGINT`).
*   `borrow_date`, `due_date`: `DATE NOT NULL`. Ngày mượn và ngày đến hạn trả. `NOT NULL` là hợp lý.
*   `return_date`: `DATE`. Ngày trả thực tế. Có thể `NULL` nếu sách chưa được trả.
*   `fine_amount`: `DECIMAL(10, 2) DEFAULT 0.00`. Số tiền phạt. `DECIMAL` là phù hợp cho tiền tệ.
*   `status`: `VARCHAR(20) NOT NULL`. Trạng thái của lần mượn (ví dụ: `ACTIVE`, `RETURNED`, `OVERDUE`). Nên sử dụng `ENUM` hoặc `CHECK` constraint.
*   Các trường quản lý thời gian và người tạo/cập nhật tương tự.

**Đề xuất cải tiến:**

*   Đảm bảo kiểu dữ liệu của `user_id` khớp với `users.id` (nếu `users.id` là `BIGINT`, thì `user_id` ở đây cũng nên là `BIGINT`).
*   Sử dụng `ENUM` hoặc `CHECK` constraint cho trường `status`.
*   Xem xét thêm `borrow_type` (ví dụ: `IN_LIBRARY`, `HOME_LOAN`) nếu có các loại hình mượn khác nhau.

#### 2.1.11. Bảng `reservations` (Book Service)

```sql
CREATE TABLE reservations
(
    id               SERIAL PRIMARY KEY,
    book_id          INT REFERENCES books (id),
    user_id          INT                                   NOT NULL, -- References user_id from user-service
    reservation_date TIMESTAMP                             NOT NULL,
    expiry_date      TIMESTAMP                             NOT NULL,
    status           VARCHAR(20)                           NOT NULL, -- PENDING, FULFILLED, CANCELLED, EXPIRED
    created_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by       VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at       TIMESTAMP,
    updated_by       VARCHAR(20),
    delete_flg       BOOLEAN     DEFAULT FALSE
);
```

**Phân tích:**

*   `id`: `SERIAL PRIMARY KEY`. Tốt.
*   `book_id`: `INT REFERENCES books (id)`. Khóa ngoại đến bảng `books`.
*   `user_id`: `INT NOT NULL`. Tham chiếu đến `users.id` từ user-service. Tương tự như `borrowings`, cần đảm bảo kiểu dữ liệu nhất quán.
*   `reservation_date`, `expiry_date`: `TIMESTAMP NOT NULL`. Ngày đặt trước và ngày hết hạn đặt trước. `TIMESTAMP` là phù hợp để lưu trữ cả ngày và giờ.
*   `status`: `VARCHAR(20) NOT NULL`. Trạng thái của đặt trước (ví dụ: `PENDING`, `FULFILLED`, `CANCELLED`, `EXPIRED`). Nên sử dụng `ENUM` hoặc `CHECK` constraint.
*   Các trường quản lý thời gian và người tạo/cập nhật tương tự.

**Đề xuất cải tiến:**

*   Đảm bảo kiểu dữ liệu của `user_id` khớp với `users.id`.
*   Sử dụng `ENUM` hoặc `CHECK` constraint cho trường `status`.
*   Xem xét thêm `pickup_location` nếu thư viện có nhiều chi nhánh.

#### 2.1.12. Bảng `notification_templates` (Notification Service)

```sql
CREATE TABLE notification_templates
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100)                          NOT NULL,
    type       VARCHAR(20)                           NOT NULL, -- EMAIL, SMS, PUSH
    subject    VARCHAR(200),
    content    TEXT                                  NOT NULL,
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at TIMESTAMP,
    updated_by VARCHAR(20),
    delete_flg BOOLEAN     DEFAULT FALSE
);
```

**Phân tích:**

*   `id`: `SERIAL PRIMARY KEY`. Tốt.
*   `name`: `VARCHAR(100) NOT NULL`. Tên mẫu thông báo.
*   `type`: `VARCHAR(20) NOT NULL`. Loại thông báo (ví dụ: `EMAIL`, `SMS`, `PUSH`). Nên sử dụng `ENUM` hoặc `CHECK` constraint.
*   `subject`: `VARCHAR(200)`. Tiêu đề thông báo (chỉ áp dụng cho email).
*   `content`: `TEXT NOT NULL`. Nội dung mẫu thông báo.
*   Các trường quản lý thời gian và người tạo/cập nhật tương tự.

**Đề xuất cải tiến:**

*   Sử dụng `ENUM` hoặc `CHECK` constraint cho trường `type`.

#### 2.1.13. Bảng `notification_preferences` (Notification Service)

```sql
CREATE TABLE notification_preferences
(
    id                       SERIAL PRIMARY KEY,
    user_id                  INT                                   NOT NULL, -- References user_id from user-service
    email_enabled            BOOLEAN     DEFAULT TRUE,
    sms_enabled              BOOLEAN     DEFAULT FALSE,
    push_enabled             BOOLEAN     DEFAULT TRUE,
    borrow_notification      BOOLEAN     DEFAULT TRUE,
    return_reminder          BOOLEAN     DEFAULT TRUE,
    overdue_notification     BOOLEAN     DEFAULT TRUE,
    reservation_notification BOOLEAN     DEFAULT TRUE,
    created_at               TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by               VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at               TIMESTAMP,
    updated_by               VARCHAR(20),
    delete_flg               BOOLEAN     DEFAULT FALSE
        UNIQUE (user_id)
);
```

**Phân tích:**

*   `id`: `SERIAL PRIMARY KEY`. Tốt.
*   `user_id`: `INT NOT NULL` với `UNIQUE (user_id)`. Tham chiếu đến `users.id` và đảm bảo mỗi người dùng chỉ có một bộ tùy chọn thông báo. Tương tự như các bảng khác, cần đảm bảo kiểu dữ liệu nhất quán.
*   `email_enabled`, `sms_enabled`, `push_enabled`: `BOOLEAN` cờ bật/tắt kênh thông báo.
*   `borrow_notification`, `return_reminder`, `overdue_notification`, `reservation_notification`: `BOOLEAN` cờ bật/tắt từng loại thông báo cụ thể.
*   Các trường quản lý thời gian và người tạo/cập nhật tương tự.

**Đề xuất cải tiến:**

*   Đảm bảo kiểu dữ liệu của `user_id` khớp với `users.id`.

#### 2.1.14. Bảng `notifications` (Notification Service)

```sql
CREATE TABLE notifications
(
    id          SERIAL PRIMARY KEY,
    user_id     INT                                   NOT NULL, -- References user_id from user-service
    template_id INT REFERENCES notification_templates (id),
    title       VARCHAR(200)                          NOT NULL,
    content     TEXT                                  NOT NULL,
    type        VARCHAR(20)                           NOT NULL, -- EMAIL, SMS, PUSH
    status      VARCHAR(20)                           NOT NULL, -- SENT, DELIVERED, READ, FAILED
    sent_at     TIMESTAMP,
    read_at     TIMESTAMP,
    created_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by  VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at  TIMESTAMP,
    updated_by  VARCHAR(20),
    delete_flg  BOOLEAN     DEFAULT FALSE
);
```

**Phân tích:**

*   `id`: `SERIAL PRIMARY KEY`. Tốt.
*   `user_id`: `INT NOT NULL`. Tham chiếu đến `users.id`. Cần đảm bảo kiểu dữ liệu nhất quán.
*   `template_id`: `INT REFERENCES notification_templates (id)`. Khóa ngoại đến bảng `notification_templates`.
*   `title`: `VARCHAR(200) NOT NULL`. Tiêu đề thông báo thực tế được gửi.
*   `content`: `TEXT NOT NULL`. Nội dung thông báo thực tế được gửi.
*   `type`: `VARCHAR(20) NOT NULL`. Loại thông báo (ví dụ: `EMAIL`, `SMS`, `PUSH`). Nên sử dụng `ENUM` hoặc `CHECK` constraint.
*   `status`: `VARCHAR(20) NOT NULL`. Trạng thái gửi thông báo (ví dụ: `SENT`, `DELIVERED`, `READ`, `FAILED`). Nên sử dụng `ENUM` hoặc `CHECK` constraint.
*   `sent_at`, `read_at`: `TIMESTAMP`. Thời gian gửi và thời gian đọc thông báo.
*   Các trường quản lý thời gian và người tạo/cập nhật tương tự.

**Đề xuất cải tiến:**

*   Đảm bảo kiểu dữ liệu của `user_id` khớp với `users.id`.
*   Sử dụng `ENUM` hoặc `CHECK` constraint cho trường `type` và `status`.

### 2.2. Phân tích ảnh màn hình chức năng

Tôi sẽ phân tích các ảnh màn hình được cung cấp để xác định các thực thể, thuộc tính và mối quan hệ bổ sung cần thiết cho cơ sở dữ liệu. Các ảnh màn hình được chia thành hai nhóm chính: 




**Dashboard quản lý thư viện** và **Ứng dụng khách hàng**.

#### 2.2.1. Dashboard quản lý thư viện

*   **Dashboard chính**: Hiển thị các thống kê tổng quan như tổng số sách, tổng số thành viên, tổng số sách đã mượn, tổng số sách quá hạn. Các thống kê này có thể được tính toán từ các bảng hiện có (`books`, `users`, `borrowings`).
*   **Quản lý sách**: Giao diện thêm, sửa, xóa sách. Các trường thông tin trên giao diện khớp với các trường trong bảng `books`, `authors`, `categories`, `publishers`. Có chức năng tìm kiếm và lọc sách.
*   **Quản lý thành viên**: Giao diện thêm, sửa, xóa thành viên. Các trường thông tin trên giao diện khớp với các trường trong bảng `users` và `library_cards`.
*   **Quản lý mượn trả**: Giao diện tạo phiếu mượn, trả sách, gia hạn. Các trường thông tin trên giao diện khớp với các trường trong bảng `borrowings`.
*   **Quản lý đặt trước**: Giao diện quản lý các yêu cầu đặt trước sách. Các trường thông tin trên giao diện khớp với các trường trong bảng `reservations`.
*   **Quản lý danh mục**: Giao diện thêm, sửa, xóa danh mục. Các trường thông tin trên giao diện khớp với các trường trong bảng `categories`.
*   **Quản lý tác giả**: Giao diện thêm, sửa, xóa tác giả. Các trường thông tin trên giao diện khớp với các trường trong bảng `authors`.
*   **Quản lý nhà xuất bản**: Giao diện thêm, sửa, xóa nhà xuất bản. Các trường thông tin trên giao diện khớp với các trường trong bảng `publishers`.
*   **Quản lý thông báo**: Giao diện quản lý các mẫu thông báo và lịch sử gửi thông báo. Các trường thông tin trên giao diện khớp với các trường trong bảng `notification_templates` và `notifications`.
*   **Cài đặt**: Giao diện cài đặt chung cho hệ thống, có thể bao gồm các quy định về mượn trả (số ngày mượn tối đa, số tiền phạt mỗi ngày, v.v.). Đây là một nghiệp vụ mới cần được thêm vào cơ sở dữ liệu.

#### 2.2.2. Ứng dụng khách hàng

*   **Trang chủ**: Hiển thị các sách nổi bật, sách mới, danh mục phổ biến. Các thông tin này có thể được truy vấn từ các bảng hiện có.
*   **Tìm kiếm sách**: Chức năng tìm kiếm sách theo tiêu đề, tác giả, danh mục. Kết quả tìm kiếm hiển thị thông tin chi tiết về sách.
*   **Chi tiết sách**: Hiển thị thông tin chi tiết về một cuốn sách, bao gồm tiêu đề, tác giả, nhà xuất bản, năm xuất bản, mô tả, ảnh bìa, số lượng bản sao có sẵn. Có chức năng đặt trước sách.
*   **Giỏ hàng (Book Bag)**: Chức năng thêm sách vào giỏ hàng để mượn. Đây là một nghiệp vụ mới cần được thêm vào cơ sở dữ liệu.
*   **Tài khoản của tôi**: Hiển thị thông tin cá nhân, lịch sử mượn trả, các sách đang mượn, các sách đã đặt trước, thông báo. Các thông tin này có thể được truy vấn từ các bảng hiện có.
*   **Đăng nhập/Đăng ký**: Chức năng xác thực người dùng, đã được xử lý thông qua tích hợp Keycloak.

### 2.3. Tổng kết các nghiệp vụ mới cần bổ sung

Dựa trên phân tích trên, các nghiệp vụ mới cần được thêm vào cơ sở dữ liệu bao gồm:

1.  **Quy định thư viện (Library Policies)**: Lưu trữ các quy định về mượn trả, phạt, đặt trước (ví dụ: thời gian mượn tối đa, số tiền phạt mỗi ngày, thời gian giữ đặt trước).
2.  **Giỏ hàng (Book Bag)**: Lưu trữ các sách mà người dùng đã chọn để mượn.

## 3. Thiết kế chi tiết cơ sở dữ liệu với best practices

Dựa trên phân tích và các đề xuất cải tiến, tôi sẽ thiết kế lại cơ sở dữ liệu với các best practices sau:

*   **Sử dụng `BIGSERIAL` cho khóa chính**: Tự động quản lý ID và hỗ trợ số lượng lớn bản ghi.
*   **Sử dụng `UUID` cho các định danh công khai**: Tăng cường bảo mật và tránh lộ thông tin về số lượng bản ghi.
*   **Sử dụng `ENUM` cho các trường có giá trị cố định**: Đảm bảo tính toàn vẹn dữ liệu và tối ưu hóa lưu trữ.
*   **Xác định độ dài cụ thể cho các trường `VARCHAR`**: Tối ưu hóa lưu trữ và hiệu suất.
*   **Thêm các `CHECK` constraint**: Đảm bảo dữ liệu hợp lệ.
*   **Thêm các `INDEX`**: Tăng tốc độ truy vấn cho các trường thường được sử dụng trong `WHERE`, `JOIN`, `ORDER BY`.
*   **Sử dụng `TIMESTAMPTZ` thay cho `TIMESTAMP`**: Lưu trữ thời gian với múi giờ, tránh các vấn đề liên quan đến múi giờ.
*   **Chuẩn hóa tên bảng và trường**: Sử dụng tên bảng số nhiều, tên trường theo kiểu `snake_case`.
*   **Thêm các bảng mới cho các nghiệp vụ còn thiếu**.

Sau đây là thiết kế chi tiết cho từng bảng:

### 3.1. Bảng `users`

Bảng này lưu trữ thông tin về người dùng của hệ thống, bao gồm cả thủ thư và độc giả.

*   `id`: `BIGSERIAL PRIMARY KEY`. Khóa chính tự tăng, đảm bảo mỗi người dùng có một ID duy nhất.
*   `public_id`: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`. Định danh công khai, được sử dụng để tham chiếu đến người dùng từ các dịch vụ khác hoặc trong API, giúp che giấu ID nội bộ.
*   `keycloak_id`: `VARCHAR(36) NOT NULL UNIQUE`. ID từ Keycloak, dùng để liên kết với hệ thống xác thực.
*   `username`: `VARCHAR(50) UNIQUE`. Tên đăng nhập của người dùng. Độ dài 50 là hợp lý.
*   `email`: `VARCHAR(255) UNIQUE`. Email của người dùng. Độ dài 255 là tiêu chuẩn.
*   `first_name`: `VARCHAR(50)`. Tên.
*   `last_name`: `VARCHAR(50)`. Họ.
*   `phone_number`: `VARCHAR(20)`. Số điện thoại.
*   `address`: `TEXT`. Địa chỉ.
*   `date_of_birth`: `DATE`. Ngày sinh.
*   `role`: `VARCHAR(20) NOT NULL DEFAULT 'MEMBER'`. Vai trò của người dùng (ví dụ: `MEMBER`, `LIBRARIAN`, `ADMIN`). Nên sử dụng `ENUM` hoặc `CHECK` constraint.
*   `is_active`: `BOOLEAN NOT NULL DEFAULT TRUE`. Trạng thái hoạt động của tài khoản.
*   `created_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`. Thời gian tạo bản ghi.
*   `updated_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`. Thời gian cập nhật bản ghi.
*   `created_by`: `BIGINT REFERENCES users(id)`. ID của người dùng tạo bản ghi.
*   `updated_by`: `BIGINT REFERENCES users(id)`. ID của người dùng cập nhật bản ghi.
*   `deleted_at`: `TIMESTAMPTZ`. Thời gian xóa mềm. Nếu `NULL` thì bản ghi chưa bị xóa.

### 3.2. Bảng `library_cards`

Bảng này lưu trữ thông tin về thẻ thư viện của người dùng.

*   `id`: `BIGSERIAL PRIMARY KEY`.
*   `public_id`: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`.
*   `card_number`: `VARCHAR(20) NOT NULL UNIQUE`. Số thẻ thư viện.
*   `user_id`: `BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE`. Khóa ngoại đến bảng `users`, đảm bảo mỗi thẻ thuộc về một người dùng. `ON DELETE CASCADE` sẽ tự động xóa thẻ khi người dùng bị xóa.
*   `issue_date`: `DATE NOT NULL DEFAULT CURRENT_DATE`. Ngày cấp thẻ.
*   `expiry_date`: `DATE NOT NULL`. Ngày hết hạn thẻ.
*   `status`: `VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'`. Trạng thái của thẻ. Nên sử dụng `ENUM` hoặc `CHECK` constraint.
*   `created_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `updated_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `created_by`: `BIGINT REFERENCES users(id)`.
*   `updated_by`: `BIGINT REFERENCES users(id)`.
*   `deleted_at`: `TIMESTAMPTZ`.

### 3.3. Bảng `authors`

Bảng này lưu trữ thông tin về tác giả.

*   `id`: `BIGSERIAL PRIMARY KEY`.
*   `public_id`: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`.
*   `name`: `VARCHAR(100) NOT NULL`. Tên tác giả.
*   `biography`: `TEXT`. Tiểu sử tác giả.
*   `created_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `updated_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `created_by`: `BIGINT REFERENCES users(id)`.
*   `updated_by`: `BIGINT REFERENCES users(id)`.
*   `deleted_at`: `TIMESTAMPTZ`.

### 3.4. Bảng `categories`

Bảng này lưu trữ thông tin về danh mục sách.

*   `id`: `BIGSERIAL PRIMARY KEY`.
*   `public_id`: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`.
*   `name`: `VARCHAR(100) NOT NULL UNIQUE`. Tên danh mục.
*   `slug`: `VARCHAR(100) NOT NULL UNIQUE`. Slug cho URL.
*   `description`: `TEXT`. Mô tả danh mục.
*   `parent_id`: `BIGINT REFERENCES categories(id)`. ID của danh mục cha, cho phép tạo cấu trúc danh mục đa cấp.
*   `created_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `updated_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `created_by`: `BIGINT REFERENCES users(id)`.
*   `updated_by`: `BIGINT REFERENCES users(id)`.
*   `deleted_at`: `TIMESTAMPTZ`.

### 3.5. Bảng `publishers`

Bảng này lưu trữ thông tin về nhà xuất bản.

*   `id`: `BIGSERIAL PRIMARY KEY`.
*   `public_id`: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`.
*   `name`: `VARCHAR(100) NOT NULL`. Tên nhà xuất bản.
*   `address`: `TEXT`. Địa chỉ nhà xuất bản.
*   `created_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `updated_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `created_by`: `BIGINT REFERENCES users(id)`.
*   `updated_by`: `BIGINT REFERENCES users(id)`.
*   `deleted_at`: `TIMESTAMPTZ`.

### 3.6. Bảng `books`

Bảng này lưu trữ thông tin về các đầu sách.

*   `id`: `BIGSERIAL PRIMARY KEY`.
*   `public_id`: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`.
*   `title`: `VARCHAR(255) NOT NULL`. Tiêu đề sách.
*   `isbn`: `VARCHAR(20) UNIQUE`. Mã ISBN.
*   `publisher_id`: `BIGINT REFERENCES publishers(id)`. Khóa ngoại đến bảng `publishers`.
*   `publication_year`: `SMALLINT`. Năm xuất bản. `SMALLINT` là đủ.
*   `description`: `TEXT`. Mô tả sách.
*   `language`: `VARCHAR(20)`. Ngôn ngữ của sách.
*   `number_of_pages`: `INTEGER`. Số trang.
*   `cover_image_url`: `VARCHAR(1000)`. URL ảnh bìa.
*   `created_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `updated_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `created_by`: `BIGINT REFERENCES users(id)`.
*   `updated_by`: `BIGINT REFERENCES users(id)`.
*   `deleted_at`: `TIMESTAMPTZ`.

### 3.7. Bảng `book_authors`

Bảng trung gian cho quan hệ nhiều-nhiều giữa `books` và `authors`.

*   `book_id`: `BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE`.
*   `author_id`: `BIGINT NOT NULL REFERENCES authors(id) ON DELETE CASCADE`.
*   `PRIMARY KEY (book_id, author_id)`.

### 3.8. Bảng `book_categories`

Bảng trung gian cho quan hệ nhiều-nhiều giữa `books` và `categories`.

*   `book_id`: `BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE`.
*   `category_id`: `BIGINT NOT NULL REFERENCES categories(id) ON DELETE CASCADE`.
*   `PRIMARY KEY (book_id, category_id)`.

### 3.9. Bảng `book_copies`

Bảng này lưu trữ thông tin về các bản sao của mỗi đầu sách.

*   `id`: `BIGSERIAL PRIMARY KEY`.
*   `public_id`: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`.
*   `book_id`: `BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE`.
*   `copy_number`: `VARCHAR(20) NOT NULL`. Số bản sao.
*   `status`: `VARCHAR(20) NOT NULL`. Trạng thái. Nên sử dụng `ENUM` hoặc `CHECK` constraint.
*   `condition`: `VARCHAR(20)`. Tình trạng. Nên sử dụng `ENUM` hoặc `CHECK` constraint.
*   `location`: `VARCHAR(50)`. Vị trí trên kệ.
*   `created_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `updated_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `created_by`: `BIGINT REFERENCES users(id)`.
*   `updated_by`: `BIGINT REFERENCES users(id)`.
*   `deleted_at`: `TIMESTAMPTZ`.
*   `UNIQUE (book_id, copy_number)`.

### 3.10. Bảng `borrowings`

Bảng này lưu trữ thông tin về các lần mượn sách.

*   `id`: `BIGSERIAL PRIMARY KEY`.
*   `public_id`: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`.
*   `book_copy_id`: `BIGINT NOT NULL REFERENCES book_copies(id)`.
*   `user_id`: `BIGINT NOT NULL REFERENCES users(id)`.
*   `borrow_date`: `DATE NOT NULL DEFAULT CURRENT_DATE`. Ngày mượn.
*   `due_date`: `DATE NOT NULL`. Ngày đến hạn trả.
*   `return_date`: `DATE`. Ngày trả thực tế.
*   `status`: `VARCHAR(20) NOT NULL`. Trạng thái. Nên sử dụng `ENUM` hoặc `CHECK` constraint.
*   `created_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `updated_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `created_by`: `BIGINT REFERENCES users(id)`.
*   `updated_by`: `BIGINT REFERENCES users(id)`.
*   `deleted_at`: `TIMESTAMPTZ`.

### 3.11. Bảng `fines`

Bảng này lưu trữ thông tin về các khoản phạt.

*   `id`: `BIGSERIAL PRIMARY KEY`.
*   `public_id`: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`.
*   `borrowing_id`: `BIGINT NOT NULL REFERENCES borrowings(id)`.
*   `amount`: `DECIMAL(10, 2) NOT NULL`. Số tiền phạt.
*   `reason`: `TEXT`. Lý do phạt.
*   `status`: `VARCHAR(20) NOT NULL`. Trạng thái. Nên sử dụng `ENUM` hoặc `CHECK` constraint.
*   `paid_at`: `TIMESTAMPTZ`. Thời gian thanh toán.
*   `created_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `updated_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `created_by`: `BIGINT REFERENCES users(id)`.
*   `updated_by`: `BIGINT REFERENCES users(id)`.
*   `deleted_at`: `TIMESTAMPTZ`.

### 3.12. Bảng `reservations`

Bảng này lưu trữ thông tin về các lần đặt trước sách.

*   `id`: `BIGSERIAL PRIMARY KEY`.
*   `public_id`: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`.
*   `book_id`: `BIGINT NOT NULL REFERENCES books(id)`.
*   `user_id`: `BIGINT NOT NULL REFERENCES users(id)`.
*   `reservation_date`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`. Ngày đặt trước.
*   `expiry_date`: `TIMESTAMPTZ NOT NULL`. Ngày hết hạn đặt trước.
*   `status`: `VARCHAR(20) NOT NULL`. Trạng thái. Nên sử dụng `ENUM` hoặc `CHECK` constraint.
*   `created_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `updated_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `created_by`: `BIGINT REFERENCES users(id)`.
*   `updated_by`: `BIGINT REFERENCES users(id)`.
*   `deleted_at`: `TIMESTAMPTZ`.

### 3.13. Bảng `book_bags`

Bảng này lưu trữ thông tin về giỏ hàng của người dùng.

*   `id`: `BIGSERIAL PRIMARY KEY`.
*   `user_id`: `BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE`.
*   `created_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `updated_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.

### 3.14. Bảng `book_bag_items`

Bảng này lưu trữ các sách trong giỏ hàng của người dùng.

*   `id`: `BIGSERIAL PRIMARY KEY`.
*   `book_bag_id`: `BIGINT NOT NULL REFERENCES book_bags(id) ON DELETE CASCADE`.
*   `book_id`: `BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE`.
*   `quantity`: `SMALLINT NOT NULL DEFAULT 1`. Số lượng.
*   `added_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `UNIQUE (book_bag_id, book_id)`.

### 3.15. Bảng `library_policies`

Bảng này lưu trữ các quy định của thư viện.

*   `id`: `SERIAL PRIMARY KEY`.
*   `policy_name`: `VARCHAR(100) NOT NULL UNIQUE`. Tên quy định.
*   `policy_value`: `VARCHAR(255) NOT NULL`. Giá trị quy định.
*   `description`: `TEXT`. Mô tả quy định.
*   `created_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `updated_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `created_by`: `BIGINT REFERENCES users(id)`.
*   `updated_by`: `BIGINT REFERENCES users(id)`.

### 3.16. Bảng `notifications`

Bảng này lưu trữ lịch sử các thông báo đã gửi.

*   `id`: `BIGSERIAL PRIMARY KEY`.
*   `public_id`: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`.
*   `user_id`: `BIGINT NOT NULL REFERENCES users(id)`.
*   `title`: `VARCHAR(255) NOT NULL`. Tiêu đề thông báo.
*   `content`: `TEXT NOT NULL`. Nội dung thông báo.
*   `type`: `VARCHAR(20) NOT NULL`. Loại thông báo. Nên sử dụng `ENUM` hoặc `CHECK` constraint.
*   `status`: `VARCHAR(20) NOT NULL`. Trạng thái. Nên sử dụng `ENUM` hoặc `CHECK` constraint.
*   `sent_at`: `TIMESTAMPTZ`. Thời gian gửi.
*   `read_at`: `TIMESTAMPTZ`. Thời gian đọc.
*   `created_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.

### 3.17. Bảng `notification_preferences`

Bảng này lưu trữ các tùy chọn thông báo của người dùng.

*   `id`: `BIGSERIAL PRIMARY KEY`.
*   `user_id`: `BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE`.
*   `email_enabled`: `BOOLEAN NOT NULL DEFAULT TRUE`.
*   `sms_enabled`: `BOOLEAN NOT NULL DEFAULT FALSE`.
*   `push_enabled`: `BOOLEAN NOT NULL DEFAULT TRUE`.
*   `borrow_notification`: `BOOLEAN NOT NULL DEFAULT TRUE`.
*   `return_reminder`: `BOOLEAN NOT NULL DEFAULT TRUE`.
*   `overdue_notification`: `BOOLEAN NOT NULL DEFAULT TRUE`.
*   `reservation_notification`: `BOOLEAN NOT NULL DEFAULT TRUE`.
*   `created_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
*   `updated_at`: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`.

## 4. Sơ đồ quan hệ thực thể (ERD)

(Phần này sẽ bao gồm một sơ đồ ERD trực quan hóa các bảng và mối quan hệ giữa chúng. Do giới hạn của môi trường văn bản, tôi sẽ mô tả các mối quan hệ chính thay vì vẽ sơ đồ)

*   **users** 1-1 **library_cards**
*   **users** 1-n **borrowings**
*   **users** 1-n **reservations**
*   **users** 1-1 **book_bags**
*   **users** 1-1 **notification_preferences**
*   **users** 1-n **notifications**
*   **books** n-n **authors** (thông qua **book_authors**)
*   **books** n-n **categories** (thông qua **book_categories**)
*   **books** 1-n **book_copies**
*   **publishers** 1-n **books**
*   **book_copies** 1-n **borrowings**
*   **borrowings** 1-1 **fines**
*   **book_bags** 1-n **book_bag_items**
*   **books** 1-n **book_bag_items**






## 5. Giải thích chi tiết nghiệp vụ và cách thiết kế từng trường

Trong phần này, tôi sẽ đi sâu vào giải thích ý nghĩa nghiệp vụ và lý do lựa chọn kiểu dữ liệu, ràng buộc cho từng trường trong các bảng đã thiết kế.

### 5.1. Bảng `users`

*   **`id`**: `BIGSERIAL PRIMARY KEY`
    *   **Nghiệp vụ**: Định danh duy nhất cho mỗi người dùng trong hệ thống. Đây là khóa chính nội bộ, được sử dụng để liên kết với các bảng khác.
    *   **Thiết kế**: `BIGSERIAL` tự động tạo một chuỗi số nguyên lớn (tối đa 9,223,372,036,854,775,807) và gán làm giá trị mặc định cho trường này. Điều này đảm bảo tính duy nhất và hiệu quả cho các hệ thống có số lượng người dùng lớn. `PRIMARY KEY` đảm bảo tính duy nhất và không `NULL`.

*   **`public_id`**: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`
    *   **Nghiệp vụ**: Định danh công khai cho người dùng. Trường này được sử dụng khi tương tác với các hệ thống bên ngoài (ví dụ: API, dịch vụ khác) để tránh lộ ID nội bộ, tăng cường bảo mật và tính linh hoạt.
    *   **Thiết kế**: `UUID` (Universally Unique Identifier) là một chuỗi 128-bit đảm bảo tính duy nhất toàn cầu. `gen_random_uuid()` là hàm của PostgreSQL để tạo UUID ngẫu nhiên. `NOT NULL` và `UNIQUE` đảm bảo mỗi người dùng có một `public_id` duy nhất và không rỗng.

*   **`keycloak_id`**: `VARCHAR(36) NOT NULL UNIQUE`
    *   **Nghiệp vụ**: Lưu trữ ID của người dùng từ hệ thống quản lý danh tính Keycloak. Đây là liên kết chính giữa hệ thống thư viện và hệ thống xác thực bên ngoài.
    *   **Thiết kế**: `VARCHAR(36)` vì Keycloak ID thường là một UUID dạng chuỗi có độ dài 36 ký tự. `NOT NULL` và `UNIQUE` đảm bảo mỗi người dùng được liên kết với một Keycloak ID duy nhất.

*   **`username`**: `VARCHAR(50) UNIQUE`
    *   **Nghiệp vụ**: Tên đăng nhập của người dùng. Có thể được sử dụng để đăng nhập hoặc hiển thị trong hệ thống.
    *   **Thiết kế**: `VARCHAR(50)` là độ dài hợp lý cho tên đăng nhập. `UNIQUE` đảm bảo không có hai người dùng nào có cùng tên đăng nhập.

*   **`email`**: `VARCHAR(255) UNIQUE`
    *   **Nghiệp vụ**: Địa chỉ email của người dùng, dùng để liên lạc, khôi phục mật khẩu hoặc gửi thông báo.
    *   **Thiết kế**: `VARCHAR(255)` là độ dài tiêu chuẩn cho địa chỉ email. `UNIQUE` đảm bảo mỗi email chỉ được đăng ký một lần.

*   **`first_name`**: `VARCHAR(50)`
    *   **Nghiệp vụ**: Tên của người dùng.
    *   **Thiết kế**: `VARCHAR(50)` là độ dài đủ cho hầu hết các tên.

*   **`last_name`**: `VARCHAR(50)`
    *   **Nghiệp vụ**: Họ của người dùng.
    *   **Thiết kế**: `VARCHAR(50)` là độ dài đủ cho hầu hết các họ.

*   **`phone_number`**: `VARCHAR(20)`
    *   **Nghiệp vụ**: Số điện thoại liên lạc của người dùng.
    *   **Thiết kế**: `VARCHAR(20)` đủ để chứa các định dạng số điện thoại quốc tế.

*   **`address`**: `TEXT`
    *   **Nghiệp vụ**: Địa chỉ cư trú của người dùng. Có thể dùng cho mục đích vận chuyển hoặc thống kê.
    *   **Thiết kế**: `TEXT` phù hợp cho các chuỗi văn bản dài, không có giới hạn độ dài cụ thể.

*   **`date_of_birth`**: `DATE`
    *   **Nghiệp vụ**: Ngày sinh của người dùng, dùng để xác định tuổi hoặc các chính sách dựa trên độ tuổi.
    *   **Thiết kế**: `DATE` chỉ lưu trữ ngày, tháng, năm, phù hợp cho ngày sinh.

*   **`role`**: `VARCHAR(20) NOT NULL DEFAULT 'MEMBER'`
    *   **Nghiệp vụ**: Vai trò của người dùng trong hệ thống (ví dụ: `MEMBER` - độc giả, `LIBRARIAN` - thủ thư, `ADMIN` - quản trị viên). Quyết định quyền truy cập và chức năng mà người dùng có thể thực hiện.
    *   **Thiết kế**: `VARCHAR(20)` với giá trị mặc định là `MEMBER`. Nên sử dụng `ENUM` hoặc `CHECK` constraint để giới hạn các giá trị hợp lệ (ví dụ: `CHECK (role IN ('MEMBER', 'LIBRARIAN', 'ADMIN'))`).

*   **`is_active`**: `BOOLEAN NOT NULL DEFAULT TRUE`
    *   **Nghiệp vụ**: Cờ trạng thái cho biết tài khoản người dùng có đang hoạt động hay không. Tài khoản không hoạt động sẽ không thể đăng nhập hoặc thực hiện các thao tác.
    *   **Thiết kế**: `BOOLEAN` là kiểu dữ liệu tối ưu cho giá trị đúng/sai. `NOT NULL DEFAULT TRUE` đảm bảo tài khoản mặc định là hoạt động khi được tạo.

*   **`created_at`**: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`
    *   **Nghiệp vụ**: Thời điểm bản ghi người dùng được tạo ra. Quan trọng cho việc theo dõi lịch sử và kiểm toán.
    *   **Thiết kế**: `TIMESTAMPTZ` (timestamp with time zone) lưu trữ thời gian cùng với thông tin múi giờ, giúp tránh các vấn đề khi hệ thống hoạt động ở nhiều múi giờ khác nhau. `NOT NULL DEFAULT NOW()` tự động gán thời gian hiện tại khi bản ghi được tạo.

*   **`updated_at`**: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`
    *   **Nghiệp vụ**: Thời điểm bản ghi người dùng được cập nhật lần cuối. Quan trọng cho việc theo dõi thay đổi và đồng bộ hóa dữ liệu.
    *   **Thiết kế**: Tương tự `created_at`. Thường được cập nhật tự động thông qua trigger hoặc trong ứng dụng.

*   **`created_by`**: `BIGINT REFERENCES users(id)`
    *   **Nghiệp vụ**: ID của người dùng đã tạo bản ghi này. Dùng để kiểm toán và theo dõi trách nhiệm.
    *   **Thiết kế**: `BIGINT` để khớp với kiểu dữ liệu của `users.id`. Là khóa ngoại tham chiếu đến chính bảng `users`.

*   **`updated_by`**: `BIGINT REFERENCES users(id)`
    *   **Nghiệp vụ**: ID của người dùng đã cập nhật bản ghi này lần cuối. Dùng để kiểm toán và theo dõi trách nhiệm.
    *   **Thiết kế**: Tương tự `created_by`.

*   **`deleted_at`**: `TIMESTAMPTZ`
    *   **Nghiệp vụ**: Thời điểm bản ghi được đánh dấu là đã xóa mềm. Nếu giá trị này là `NULL`, bản ghi được coi là đang hoạt động. Đây là một best practice để duy trì lịch sử dữ liệu và khả năng khôi phục.
    *   **Thiết kế**: `TIMESTAMPTZ`. Có thể `NULL`.

### 5.2. Bảng `library_cards`

*   **`id`**: `BIGSERIAL PRIMARY KEY`
*   **`public_id`**: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`
*   **`card_number`**: `VARCHAR(20) NOT NULL UNIQUE`
    *   **Nghiệp vụ**: Số thẻ thư viện duy nhất được in trên thẻ. Dùng để định danh thẻ khi mượn/trả sách.
    *   **Thiết kế**: `VARCHAR(20)` đủ cho các định dạng số thẻ phổ biến. `NOT NULL` và `UNIQUE` đảm bảo tính duy nhất.

*   **`user_id`**: `BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE`
    *   **Nghiệp vụ**: Liên kết thẻ thư viện với một người dùng cụ thể.
    *   **Thiết kế**: `BIGINT` khớp với `users.id`. `NOT NULL` đảm bảo mỗi thẻ phải thuộc về một người dùng. `ON DELETE CASCADE` tự động xóa thẻ khi người dùng sở hữu thẻ bị xóa.

*   **`issue_date`**: `DATE NOT NULL DEFAULT CURRENT_DATE`
    *   **Nghiệp vụ**: Ngày thẻ được cấp cho người dùng.
    *   **Thiết kế**: `DATE` chỉ lưu ngày. `NOT NULL DEFAULT CURRENT_DATE` tự động gán ngày hiện tại khi thẻ được cấp.

*   **`expiry_date`**: `DATE NOT NULL`
    *   **Nghiệp vụ**: Ngày thẻ hết hạn. Sau ngày này, thẻ không còn hiệu lực để mượn sách.
    *   **Thiết kế**: `DATE`. `NOT NULL` đảm bảo thẻ luôn có ngày hết hạn.

*   **`status`**: `VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'`
    *   **Nghiệp vụ**: Trạng thái hiện tại của thẻ (ví dụ: `ACTIVE`, `INACTIVE`, `EXPIRED`, `LOST`, `BLOCKED`).
    *   **Thiết kế**: `VARCHAR(20)`. Nên sử dụng `ENUM` hoặc `CHECK` constraint để giới hạn các giá trị hợp lệ.

*   **Các trường quản lý thời gian và người tạo/cập nhật**: Tương tự bảng `users`.

### 5.3. Bảng `authors`

*   **`id`**: `BIGSERIAL PRIMARY KEY`
*   **`public_id`**: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`
*   **`name`**: `VARCHAR(100) NOT NULL`
    *   **Nghiệp vụ**: Tên đầy đủ của tác giả.
    *   **Thiết kế**: `VARCHAR(100)` là độ dài hợp lý cho tên tác giả. `NOT NULL` đảm bảo tên tác giả luôn được cung cấp.

*   **`biography`**: `TEXT`
    *   **Nghiệp vụ**: Tiểu sử hoặc thông tin chi tiết về tác giả.
    *   **Thiết kế**: `TEXT` cho phép lưu trữ nội dung dài.

*   **Các trường quản lý thời gian và người tạo/cập nhật**: Tương tự bảng `users`.

### 5.4. Bảng `categories`

*   **`id`**: `BIGSERIAL PRIMARY KEY`
*   **`public_id`**: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`
*   **`name`**: `VARCHAR(100) NOT NULL UNIQUE`
    *   **Nghiệp vụ**: Tên của danh mục sách (ví dụ: 




'Khoa học viễn tưởng', 'Lịch sử', 'Văn học').
    *   **Thiết kế**: `VARCHAR(100)` là độ dài hợp lý. `NOT NULL` và `UNIQUE` đảm bảo tên danh mục là duy nhất và không rỗng.

*   **`slug`**: `VARCHAR(100) NOT NULL UNIQUE`
    *   **Nghiệp vụ**: Một phiên bản thân thiện với URL của tên danh mục (ví dụ: 'khoa-hoc-vien-tuong'). Hữu ích cho SEO và định tuyến trong ứng dụng web.
    *   **Thiết kế**: `VARCHAR(100)` và `UNIQUE` đảm bảo tính duy nhất và độ dài phù hợp.

*   **`description`**: `TEXT`
    *   **Nghiệp vụ**: Mô tả chi tiết về danh mục.
    *   **Thiết kế**: `TEXT` cho phép lưu trữ nội dung dài.

*   **`parent_id`**: `BIGINT REFERENCES categories(id)`
    *   **Nghiệp vụ**: Cho phép tạo cấu trúc danh mục phân cấp (ví dụ: 'Khoa học viễn tưởng' có thể là con của 'Văn học').
    *   **Thiết kế**: `BIGINT` tham chiếu đến `categories.id`. Có thể `NULL` nếu danh mục là danh mục gốc.

*   **Các trường quản lý thời gian và người tạo/cập nhật**: Tương tự bảng `users`.

### 5.5. Bảng `publishers`

*   **`id`**: `BIGSERIAL PRIMARY KEY`
*   **`public_id`**: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`
*   **`name`**: `VARCHAR(100) NOT NULL`
    *   **Nghiệp vụ**: Tên của nhà xuất bản.
    *   **Thiết kế**: `VARCHAR(100)` là độ dài hợp lý. `NOT NULL` đảm bảo tên nhà xuất bản luôn được cung cấp.

*   **`address`**: `TEXT`
    *   **Nghiệp vụ**: Địa chỉ của nhà xuất bản.
    *   **Thiết kế**: `TEXT` cho phép lưu trữ nội dung dài.

*   **Các trường quản lý thời gian và người tạo/cập nhật**: Tương tự bảng `users`.

### 5.6. Bảng `books`

*   **`id`**: `BIGSERIAL PRIMARY KEY`
*   **`public_id`**: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`
*   **`title`**: `VARCHAR(255) NOT NULL`
    *   **Nghiệp vụ**: Tiêu đề đầy đủ của cuốn sách.
    *   **Thiết kế**: `VARCHAR(255)` là độ dài tiêu chuẩn cho tiêu đề. `NOT NULL` đảm bảo tiêu đề luôn được cung cấp.

*   **`isbn`**: `VARCHAR(20) UNIQUE`
    *   **Nghiệp vụ**: Mã số sách tiêu chuẩn quốc tế (International Standard Book Number). Dùng để định danh duy nhất một ấn bản sách.
    *   **Thiết kế**: `VARCHAR(20)` đủ cho cả ISBN-10 và ISBN-13. `UNIQUE` đảm bảo mỗi ISBN chỉ xuất hiện một lần.

*   **`publisher_id`**: `BIGINT REFERENCES publishers(id)`
    *   **Nghiệp vụ**: Liên kết sách với nhà xuất bản của nó.
    *   **Thiết kế**: `BIGINT` khớp với `publishers.id`. Có thể `NULL` nếu thông tin nhà xuất bản không có sẵn.

*   **`publication_year`**: `SMALLINT`
    *   **Nghiệp vụ**: Năm xuất bản của cuốn sách.
    *   **Thiết kế**: `SMALLINT` (số nguyên nhỏ) là đủ để lưu trữ năm (ví dụ: từ -32768 đến 32767).

*   **`description`**: `TEXT`
    *   **Nghiệp vụ**: Tóm tắt hoặc mô tả chi tiết về nội dung sách.
    *   **Thiết kế**: `TEXT` cho phép lưu trữ nội dung dài.

*   **`language`**: `VARCHAR(20)`
    *   **Nghiệp vụ**: Ngôn ngữ chính của cuốn sách (ví dụ: 'Tiếng Việt', 'English').
    *   **Thiết kế**: `VARCHAR(20)` đủ cho mã ngôn ngữ hoặc tên ngôn ngữ.

*   **`number_of_pages`**: `INTEGER`
    *   **Nghiệp vụ**: Tổng số trang của cuốn sách.
    *   **Thiết kế**: `INTEGER` là đủ để lưu trữ số trang.

*   **`cover_image_url`**: `VARCHAR(1000)`
    *   **Nghiệp vụ**: URL đến ảnh bìa của cuốn sách. Dùng để hiển thị trong giao diện người dùng.
    *   **Thiết kế**: `VARCHAR(1000)` là độ dài đủ cho hầu hết các URL ảnh.

*   **Các trường quản lý thời gian và người tạo/cập nhật**: Tương tự bảng `users`.

### 5.7. Bảng `book_authors`

*   **`book_id`**: `BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE`
    *   **Nghiệp vụ**: Liên kết với ID của cuốn sách.
    *   **Thiết kế**: `BIGINT` khớp với `books.id`. `ON DELETE CASCADE` đảm bảo khi một cuốn sách bị xóa, các liên kết tác giả của nó cũng bị xóa.

*   **`author_id`**: `BIGINT NOT NULL REFERENCES authors(id) ON DELETE CASCADE`
    *   **Nghiệp vụ**: Liên kết với ID của tác giả.
    *   **Thiết kế**: `BIGINT` khớp với `authors.id`. `ON DELETE CASCADE` đảm bảo khi một tác giả bị xóa, các liên kết sách của họ cũng bị xóa.

*   **`PRIMARY KEY (book_id, author_id)`**
    *   **Nghiệp vụ**: Đảm bảo mỗi cặp sách-tác giả là duy nhất, ngăn chặn việc thêm trùng lặp.
    *   **Thiết kế**: Khóa chính tổng hợp từ hai khóa ngoại, tạo thành một quan hệ nhiều-nhiều hiệu quả.

### 5.8. Bảng `book_categories`

*   **`book_id`**: `BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE`
    *   **Nghiệp vụ**: Liên kết với ID của cuốn sách.
    *   **Thiết kế**: Tương tự `book_authors.book_id`.

*   **`category_id`**: `BIGINT NOT NULL REFERENCES categories(id) ON DELETE CASCADE`
    *   **Nghiệp vụ**: Liên kết với ID của danh mục.
    *   **Thiết kế**: `BIGINT` khớp với `categories.id`. `ON DELETE CASCADE` đảm bảo khi một danh mục bị xóa, các liên kết sách của nó cũng bị xóa.

*   **`PRIMARY KEY (book_id, category_id)`**
    *   **Nghiệp vụ**: Đảm bảo mỗi cặp sách-danh mục là duy nhất.
    *   **Thiết kế**: Khóa chính tổng hợp từ hai khóa ngoại.

### 5.9. Bảng `book_copies`

*   **`id`**: `BIGSERIAL PRIMARY KEY`
*   **`public_id`**: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`
*   **`book_id`**: `BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE`
    *   **Nghiệp vụ**: Liên kết bản sao sách với đầu sách gốc.
    *   **Thiết kế**: `BIGINT` khớp với `books.id`. `ON DELETE CASCADE` đảm bảo khi một đầu sách bị xóa, tất cả các bản sao của nó cũng bị xóa.

*   **`copy_number`**: `VARCHAR(20) NOT NULL`
    *   **Nghiệp vụ**: Số định danh duy nhất cho từng bản sao của một cuốn sách (ví dụ: 'Bản 1', 'Bản 2').
    *   **Thiết kế**: `VARCHAR(20)`. Kết hợp với `book_id` để tạo ràng buộc `UNIQUE (book_id, copy_number)`.

*   **`status`**: `VARCHAR(20) NOT NULL`
    *   **Nghiệp vụ**: Trạng thái hiện tại của bản sao sách (ví dụ: `AVAILABLE`, `BORROWED`, `RESERVED`, `MAINTENANCE`, `LOST`).
    *   **Thiết kế**: `VARCHAR(20)`. Nên sử dụng `ENUM` hoặc `CHECK` constraint để giới hạn các giá trị hợp lệ.

*   **`condition`**: `VARCHAR(20)`
    *   **Nghiệp vụ**: Tình trạng vật lý của bản sao sách (ví dụ: `NEW`, `GOOD`, `FAIR`, `POOR`, `DAMAGED`).
    *   **Thiết kế**: `VARCHAR(20)`. Nên sử dụng `ENUM` hoặc `CHECK` constraint để giới hạn các giá trị hợp lệ.

*   **`location`**: `VARCHAR(50)`
    *   **Nghiệp vụ**: Vị trí cụ thể của bản sao sách trong thư viện (ví dụ: 'Kệ A1, Tầng 2').
    *   **Thiết kế**: `VARCHAR(50)` đủ để mô tả vị trí.

*   **`UNIQUE (book_id, copy_number)`**
    *   **Nghiệp vụ**: Đảm bảo mỗi bản sao sách có một số định danh duy nhất trong phạm vi của một đầu sách.
    *   **Thiết kế**: Ràng buộc duy nhất trên cặp trường này.

*   **Các trường quản lý thời gian và người tạo/cập nhật**: Tương tự bảng `users`.

### 5.10. Bảng `borrowings`

*   **`id`**: `BIGSERIAL PRIMARY KEY`
*   **`public_id`**: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`
*   **`book_copy_id`**: `BIGINT NOT NULL REFERENCES book_copies(id)`
    *   **Nghiệp vụ**: Liên kết với bản sao sách cụ thể được mượn.
    *   **Thiết kế**: `BIGINT` khớp với `book_copies.id`. `NOT NULL` đảm bảo mỗi lần mượn phải liên quan đến một bản sao sách.

*   **`user_id`**: `BIGINT NOT NULL REFERENCES users(id)`
    *   **Nghiệp vụ**: Liên kết với người dùng đã mượn sách.
    *   **Thiết kế**: `BIGINT` khớp với `users.id`. `NOT NULL` đảm bảo mỗi lần mượn phải liên quan đến một người dùng.

*   **`borrow_date`**: `DATE NOT NULL DEFAULT CURRENT_DATE`
    *   **Nghiệp vụ**: Ngày sách được mượn.
    *   **Thiết kế**: `DATE`. `NOT NULL DEFAULT CURRENT_DATE` tự động gán ngày hiện tại.

*   **`due_date`**: `DATE NOT NULL`
    *   **Nghiệp vụ**: Ngày sách phải được trả lại. Dùng để tính toán sách quá hạn.
    *   **Thiết kế**: `DATE`. `NOT NULL` đảm bảo ngày đến hạn luôn được xác định.

*   **`return_date`**: `DATE`
    *   **Nghiệp vụ**: Ngày sách thực tế được trả lại. Có thể `NULL` nếu sách chưa được trả.
    *   **Thiết kế**: `DATE`. Có thể `NULL`.

*   **`status`**: `VARCHAR(20) NOT NULL`
    *   **Nghiệp vụ**: Trạng thái của lần mượn (ví dụ: `ACTIVE`, `RETURNED`, `OVERDUE`, `LOST`).
    *   **Thiết kế**: `VARCHAR(20)`. Nên sử dụng `ENUM` hoặc `CHECK` constraint để giới hạn các giá trị hợp lệ.

*   **Các trường quản lý thời gian và người tạo/cập nhật**: Tương tự bảng `users`.

### 5.11. Bảng `fines`

*   **`id`**: `BIGSERIAL PRIMARY KEY`
*   **`public_id`**: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`
*   **`borrowing_id`**: `BIGINT NOT NULL REFERENCES borrowings(id)`
    *   **Nghiệp vụ**: Liên kết khoản phạt với lần mượn cụ thể gây ra phạt.
    *   **Thiết kế**: `BIGINT` khớp với `borrowings.id`. `NOT NULL` đảm bảo mỗi khoản phạt liên quan đến một lần mượn.

*   **`amount`**: `DECIMAL(10, 2) NOT NULL`
    *   **Nghiệp vụ**: Số tiền phạt.
    *   **Thiết kế**: `DECIMAL(10, 2)` lưu trữ số thập phân với tổng cộng 10 chữ số, trong đó 2 chữ số sau dấu thập phân, phù hợp cho tiền tệ. `NOT NULL` đảm bảo số tiền phạt luôn được xác định.

*   **`reason`**: `TEXT`
    *   **Nghiệp vụ**: Lý do phát sinh khoản phạt (ví dụ: 'Quá hạn 5 ngày', 'Sách bị hư hại').
    *   **Thiết kế**: `TEXT` cho phép mô tả chi tiết.

*   **`status`**: `VARCHAR(20) NOT NULL`
    *   **Nghiệp vụ**: Trạng thái của khoản phạt (ví dụ: `PENDING`, `PAID`, `WAIVED`).
    *   **Thiết kế**: `VARCHAR(20)`. Nên sử dụng `ENUM` hoặc `CHECK` constraint để giới hạn các giá trị hợp lệ.

*   **`paid_at`**: `TIMESTAMPTZ`
    *   **Nghiệp vụ**: Thời điểm khoản phạt được thanh toán.
    *   **Thiết kế**: `TIMESTAMPTZ`. Có thể `NULL` nếu chưa thanh toán.

*   **Các trường quản lý thời gian và người tạo/cập nhật**: Tương tự bảng `users`.

### 5.12. Bảng `reservations`

*   **`id`**: `BIGSERIAL PRIMARY KEY`
*   **`public_id`**: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`
*   **`book_id`**: `BIGINT NOT NULL REFERENCES books(id)`
    *   **Nghiệp vụ**: Liên kết đặt trước với đầu sách được đặt.
    *   **Thiết kế**: `BIGINT` khớp với `books.id`. `NOT NULL` đảm bảo mỗi đặt trước liên quan đến một đầu sách.

*   **`user_id`**: `BIGINT NOT NULL REFERENCES users(id)`
    *   **Nghiệp vụ**: Liên kết đặt trước với người dùng đã đặt.
    *   **Thiết kế**: `BIGINT` khớp với `users.id`. `NOT NULL` đảm bảo mỗi đặt trước liên quan đến một người dùng.

*   **`reservation_date`**: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`
    *   **Nghiệp vụ**: Thời điểm người dùng thực hiện đặt trước.
    *   **Thiết kế**: `TIMESTAMPTZ`. `NOT NULL DEFAULT NOW()` tự động gán thời gian hiện tại.

*   **`expiry_date`**: `TIMESTAMPTZ NOT NULL`
    *   **Nghiệp vụ**: Thời điểm đặt trước hết hạn nếu sách không được mượn.
    *   **Thiết kế**: `TIMESTAMPTZ`. `NOT NULL` đảm bảo thời gian hết hạn luôn được xác định.

*   **`status`**: `VARCHAR(20) NOT NULL`
    *   **Nghiệp vụ**: Trạng thái của đặt trước (ví dụ: `PENDING`, `FULFILLED`, `CANCELLED`, `EXPIRED`).
    *   **Thiết kế**: `VARCHAR(20)`. Nên sử dụng `ENUM` hoặc `CHECK` constraint để giới hạn các giá trị hợp lệ.

*   **Các trường quản lý thời gian và người tạo/cập nhật**: Tương tự bảng `users`.

### 5.13. Bảng `book_bags`

*   **`id`**: `BIGSERIAL PRIMARY KEY`
*   **`user_id`**: `BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE`
    *   **Nghiệp vụ**: Liên kết giỏ sách với người dùng sở hữu nó. Mỗi người dùng có một giỏ sách duy nhất.
    *   **Thiết kế**: `BIGINT` khớp với `users.id`. `NOT NULL` và `UNIQUE` đảm bảo mỗi người dùng chỉ có một giỏ sách. `ON DELETE CASCADE` tự động xóa giỏ sách khi người dùng bị xóa.

*   **`created_at`**: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`
*   **`updated_at`**: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`

### 5.14. Bảng `book_bag_items`

*   **`id`**: `BIGSERIAL PRIMARY KEY`
*   **`book_bag_id`**: `BIGINT NOT NULL REFERENCES book_bags(id) ON DELETE CASCADE`
    *   **Nghiệp vụ**: Liên kết mục trong giỏ sách với giỏ sách cụ thể.
    *   **Thiết kế**: `BIGINT` khớp với `book_bags.id`. `NOT NULL`. `ON DELETE CASCADE` tự động xóa mục khi giỏ sách bị xóa.

*   **`book_id`**: `BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE`
    *   **Nghiệp vụ**: Liên kết mục trong giỏ sách với đầu sách cụ thể.
    *   **Thiết kế**: `BIGINT` khớp với `books.id`. `NOT NULL`. `ON DELETE CASCADE` tự động xóa mục khi sách bị xóa.

*   **`quantity`**: `SMALLINT NOT NULL DEFAULT 1`
    *   **Nghiệp vụ**: Số lượng bản sao của một đầu sách trong giỏ sách. Mặc dù thường là 1, nhưng có thể cho phép nhiều hơn nếu thư viện cho phép mượn nhiều bản sao của cùng một đầu sách cùng lúc.
    *   **Thiết kế**: `SMALLINT` là đủ. `NOT NULL DEFAULT 1`.

*   **`added_at`**: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`
    *   **Nghiệp vụ**: Thời điểm sách được thêm vào giỏ sách.
    *   **Thiết kế**: `TIMESTAMPTZ`. `NOT NULL DEFAULT NOW()`.

*   **`UNIQUE (book_bag_id, book_id)`**
    *   **Nghiệp vụ**: Đảm bảo mỗi đầu sách chỉ xuất hiện một lần trong một giỏ sách cụ thể.
    *   **Thiết kế**: Ràng buộc duy nhất trên cặp trường này.

### 5.15. Bảng `library_policies`

*   **`id`**: `SERIAL PRIMARY KEY`
*   **`policy_name`**: `VARCHAR(100) NOT NULL UNIQUE`
    *   **Nghiệp vụ**: Tên duy nhất của quy định (ví dụ: 'MAX_BORROW_DAYS', 'OVERDUE_FINE_PER_DAY').
    *   **Thiết kế**: `VARCHAR(100)`. `NOT NULL` và `UNIQUE`.

*   **`policy_value`**: `VARCHAR(255) NOT NULL`
    *   **Nghiệp vụ**: Giá trị của quy định (ví dụ: '14' cho số ngày mượn, '0.50' cho tiền phạt).
    *   **Thiết kế**: `VARCHAR(255)` để lưu trữ giá trị dưới dạng chuỗi, cho phép linh hoạt với các loại dữ liệu khác nhau (số, boolean, chuỗi). Việc chuyển đổi kiểu dữ liệu sẽ được thực hiện ở tầng ứng dụng.

*   **`description`**: `TEXT`
    *   **Nghiệp vụ**: Mô tả chi tiết về quy định.
    *   **Thiết kế**: `TEXT`.

*   **Các trường quản lý thời gian và người tạo/cập nhật**: Tương tự bảng `users`.

### 5.16. Bảng `notifications`

*   **`id`**: `BIGSERIAL PRIMARY KEY`
*   **`public_id`**: `UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`
*   **`user_id`**: `BIGINT NOT NULL REFERENCES users(id)`
    *   **Nghiệp vụ**: Người dùng nhận thông báo.
    *   **Thiết kế**: `BIGINT` khớp với `users.id`. `NOT NULL`.

*   **`title`**: `VARCHAR(255) NOT NULL`
    *   **Nghiệp vụ**: Tiêu đề của thông báo.
    *   **Thiết kế**: `VARCHAR(255)`. `NOT NULL`.

*   **`content`**: `TEXT NOT NULL`
    *   **Nghiệp vụ**: Nội dung chi tiết của thông báo.
    *   **Thiết kế**: `TEXT`. `NOT NULL`.

*   **`type`**: `VARCHAR(20) NOT NULL`
    *   **Nghiệp vụ**: Kênh gửi thông báo (ví dụ: `EMAIL`, `SMS`, `PUSH`).
    *   **Thiết kế**: `VARCHAR(20)`. Nên sử dụng `ENUM` hoặc `CHECK` constraint.

*   **`status`**: `VARCHAR(20) NOT NULL`
    *   **Nghiệp vụ**: Trạng thái gửi thông báo (ví dụ: `SENT`, `DELIVERED`, `READ`, `FAILED`).
    *   **Thiết kế**: `VARCHAR(20)`. Nên sử dụng `ENUM` hoặc `CHECK` constraint.

*   **`sent_at`**: `TIMESTAMPTZ`
    *   **Nghiệp vụ**: Thời điểm thông báo được gửi đi.
    *   **Thiết kế**: `TIMESTAMPTZ`. Có thể `NULL` nếu thông báo chưa được gửi.

*   **`read_at`**: `TIMESTAMPTZ`
    *   **Nghiệp vụ**: Thời điểm người dùng đọc thông báo.
    *   **Thiết kế**: `TIMESTAMPTZ`. Có thể `NULL` nếu chưa đọc.

*   **`created_at`**: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`

### 5.17. Bảng `notification_preferences`

*   **`id`**: `BIGSERIAL PRIMARY KEY`
*   **`user_id`**: `BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE`
    *   **Nghiệp vụ**: Người dùng sở hữu các tùy chọn thông báo này. Mỗi người dùng chỉ có một bộ tùy chọn.
    *   **Thiết kế**: `BIGINT` khớp với `users.id`. `NOT NULL` và `UNIQUE` đảm bảo tính duy nhất. `ON DELETE CASCADE` tự động xóa tùy chọn khi người dùng bị xóa.

*   **`email_enabled`**: `BOOLEAN NOT NULL DEFAULT TRUE`
    *   **Nghiệp vụ**: Cờ bật/tắt nhận thông báo qua email.
    *   **Thiết kế**: `BOOLEAN`. `NOT NULL DEFAULT TRUE`.

*   **`sms_enabled`**: `BOOLEAN NOT NULL DEFAULT FALSE`
    *   **Nghiệp vụ**: Cờ bật/tắt nhận thông báo qua SMS.
    *   **Thiết kế**: `BOOLEAN`. `NOT NULL DEFAULT FALSE`.

*   **`push_enabled`**: `BOOLEAN NOT NULL DEFAULT TRUE`
    *   **Nghiệp vụ**: Cờ bật/tắt nhận thông báo đẩy (push notification).
    *   **Thiết kế**: `BOOLEAN`. `NOT NULL DEFAULT TRUE`.

*   **`borrow_notification`**: `BOOLEAN NOT NULL DEFAULT TRUE`
    *   **Nghiệp vụ**: Cờ bật/tắt thông báo khi mượn sách.
    *   **Thiết kế**: `BOOLEAN`. `NOT NULL DEFAULT TRUE`.

*   **`return_reminder`**: `BOOLEAN NOT NULL DEFAULT TRUE`
    *   **Nghiệp vụ**: Cờ bật/tắt nhắc nhở trả sách.
    *   **Thiết kế**: `BOOLEAN`. `NOT NULL DEFAULT TRUE`.

*   **`overdue_notification`**: `BOOLEAN NOT NULL DEFAULT TRUE`
    *   **Nghiệp vụ**: Cờ bật/tắt thông báo khi sách quá hạn.
    *   **Thiết kế**: `BOOLEAN`. `NOT NULL DEFAULT TRUE`.

*   **`reservation_notification`**: `BOOLEAN NOT NULL DEFAULT TRUE`
    *   **Nghiệp vụ**: Cờ bật/tắt thông báo khi đặt trước sách.
    *   **Thiết kế**: `BOOLEAN`. `NOT NULL DEFAULT TRUE`.

*   **`created_at`**: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`
*   **`updated_at`**: `TIMESTAMPTZ NOT NULL DEFAULT NOW()`






## 6. Phân chia bảng theo Microservices và điều chỉnh ràng buộc khóa ngoại

Để phù hợp với kiến trúc microservices, cơ sở dữ liệu sẽ được phân chia thành các schema hoặc cơ sở dữ liệu riêng biệt cho từng dịch vụ. Điều này giúp tăng tính độc lập, khả năng mở rộng và giảm sự phụ thuộc giữa các dịch vụ. Theo yêu cầu, các ràng buộc khóa ngoại (Foreign Key - FK) giữa các bảng thuộc các microservice khác nhau sẽ được loại bỏ. Tuy nhiên, các ràng buộc FK nội bộ trong cùng một microservice vẫn sẽ được giữ lại để đảm bảo tính toàn vẹn dữ liệu cục bộ.

### 6.1. Nguyên tắc thiết kế cho Microservices Database

*   **Độc lập dữ liệu**: Mỗi microservice sở hữu cơ sở dữ liệu riêng của mình. Các dịch vụ không trực tiếp truy cập vào cơ sở dữ liệu của dịch vụ khác.
*   **Loại bỏ ràng buộc FK liên dịch vụ**: Các ràng buộc khóa ngoại giữa các bảng thuộc các dịch vụ khác nhau sẽ được loại bỏ. Thay vào đó, việc duy trì tính nhất quán dữ liệu giữa các dịch vụ sẽ được xử lý ở tầng ứng dụng thông qua các cơ chế như giao tiếp API, message queues (ví dụ: Kafka, RabbitMQ) hoặc Sagas.
*   **Giữ lại ràng buộc FK nội bộ**: Các ràng buộc khóa ngoại giữa các bảng trong cùng một dịch vụ vẫn được giữ lại để đảm bảo tính toàn vẹn dữ liệu trong phạm vi của dịch vụ đó.
*   **Sử dụng `UUID` cho các khóa ngoại liên dịch vụ**: Khi một dịch vụ cần tham chiếu đến một thực thể từ dịch vụ khác, nó sẽ lưu trữ `public_id` (UUID) của thực thể đó thay vì khóa chính nội bộ (`id`). Điều này giúp giảm sự phụ thuộc vào cấu trúc ID của dịch vụ khác và tăng tính linh hoạt.

### 6.2. Phân chia bảng vào các Microservices

Dựa trên các dịch vụ đã xác định (`catalog-service`, `loan-service`, `member-service`, `notification-service`), các bảng sẽ được phân chia như sau:

#### 6.2.1. `member-service`

Dịch vụ này sẽ quản lý thông tin người dùng và thẻ thư viện. Nó sẽ sở hữu các bảng:

*   `users`
*   `library_cards`

**Điều chỉnh ràng buộc khóa ngoại:**

*   `library_cards.user_id` sẽ vẫn là khóa ngoại tham chiếu đến `users.id` vì cả hai bảng đều thuộc `member-service`.
*   Các bảng khác trong hệ thống (ví dụ: `borrowings`, `reservations`, `notifications`, `notification_preferences`, `book_bags`) sẽ không còn ràng buộc khóa ngoại trực tiếp đến `users.id`. Thay vào đó, chúng sẽ lưu trữ `users.public_id` (UUID) và việc kiểm tra tính hợp lệ của `user_id` sẽ được thực hiện ở tầng ứng dụng của các dịch vụ tương ứng.

#### 6.2.2. `catalog-service`

Dịch vụ này sẽ quản lý thông tin về sách, tác giả, danh mục và nhà xuất bản. Nó sẽ sở hữu các bảng:

*   `authors`
*   `categories`
*   `publishers`
*   `books`
*   `book_authors`
*   `book_categories`
*   `book_copies`

**Điều chỉnh ràng buộc khóa ngoại:**

*   `books.publisher_id` sẽ vẫn là khóa ngoại tham chiếu đến `publishers.id`.
*   `book_authors.book_id` và `book_authors.author_id` sẽ vẫn là khóa ngoại tham chiếu đến `books.id` và `authors.id`.
*   `book_categories.book_id` và `book_categories.category_id` sẽ vẫn là khóa ngoại tham chiếu đến `books.id` và `categories.id`.
*   `book_copies.book_id` sẽ vẫn là khóa ngoại tham chiếu đến `books.id`.
*   Các bảng khác trong hệ thống (ví dụ: `borrowings`, `reservations`, `book_bag_items`) sẽ không còn ràng buộc khóa ngoại trực tiếp đến `books.id` hoặc `book_copies.id`. Thay vào đó, chúng sẽ lưu trữ `books.public_id` hoặc `book_copies.public_id` (UUID).

#### 6.2.3. `loan-service`

Dịch vụ này sẽ quản lý các nghiệp vụ liên quan đến mượn, trả, đặt trước và phạt. Nó sẽ sở hữu các bảng:

*   `borrowings`
*   `fines`
*   `reservations`
*   `book_bags`
*   `book_bag_items`
*   `library_policies`

**Điều chỉnh ràng buộc khóa ngoại:**

*   `fines.borrowing_id` sẽ vẫn là khóa ngoại tham chiếu đến `borrowings.id`.
*   `book_bag_items.book_bag_id` sẽ vẫn là khóa ngoại tham chiếu đến `book_bags.id`.
*   `borrowings.book_copy_id` sẽ không còn là khóa ngoại trực tiếp đến `book_copies.id` (thuộc `catalog-service`). Thay vào đó, nó sẽ lưu trữ `book_copies.public_id` (UUID).
*   `borrowings.user_id` sẽ không còn là khóa ngoại trực tiếp đến `users.id` (thuộc `member-service`). Thay vào đó, nó sẽ lưu trữ `users.public_id` (UUID).
*   `reservations.book_id` sẽ không còn là khóa ngoại trực tiếp đến `books.id` (thuộc `catalog-service`). Thay vào đó, nó sẽ lưu trữ `books.public_id` (UUID).
*   `reservations.user_id` sẽ không còn là khóa ngoại trực tiếp đến `users.id` (thuộc `member-service`). Thay vào đó, nó sẽ lưu trữ `users.public_id` (UUID).
*   `book_bags.user_id` sẽ không còn là khóa ngoại trực tiếp đến `users.id` (thuộc `member-service`). Thay vào đó, nó sẽ lưu trữ `users.public_id` (UUID).
*   `book_bag_items.book_id` sẽ không còn là khóa ngoại trực tiếp đến `books.id` (thuộc `catalog-service`). Thay vào đó, nó sẽ lưu trữ `books.public_id` (UUID).

#### 6.2.4. `notification-service`

Dịch vụ này sẽ quản lý các thông báo và tùy chọn thông báo. Nó sẽ sở hữu các bảng:

*   `notifications`
*   `notification_preferences`

**Điều chỉnh ràng buộc khóa ngoại:**

*   `notifications.user_id` sẽ không còn là khóa ngoại trực tiếp đến `users.id` (thuộc `member-service`). Thay vào đó, nó sẽ lưu trữ `users.public_id` (UUID).
*   `notification_preferences.user_id` sẽ không còn là khóa ngoại trực tiếp đến `users.id` (thuộc `member-service`). Thay vào đó, nó sẽ lưu trữ `users.public_id` (UUID).

### 6.3. Cập nhật các trường `created_by` và `updated_by`

Trong kiến trúc microservices, việc tham chiếu `created_by` và `updated_by` đến `users.id` (thuộc `member-service`) sẽ gây ra ràng buộc liên dịch vụ. Để giải quyết vấn đề này, các trường `created_by` và `updated_by` trong các bảng không thuộc `member-service` sẽ được thay đổi để lưu trữ `public_id` (UUID) của người dùng thực hiện thao tác, hoặc đơn giản là `VARCHAR(36)` để lưu trữ `keycloak_id` của người dùng đó. Điều này cho phép các dịch vụ khác ghi lại thông tin người tạo/cập nhật mà không cần phụ thuộc vào cơ sở dữ liệu của `member-service`.

Trong trường hợp này, tôi sẽ thay đổi `created_by` và `updated_by` thành `VARCHAR(36)` để lưu trữ `keycloak_id` của người dùng, vì `keycloak_id` đã được định nghĩa là `VARCHAR(36)` và là `UNIQUE` trong bảng `users` của `member-service`, đảm bảo tính duy nhất và khả năng truy xuất thông tin người dùng từ `member-service` khi cần.






### 6.4. Điều chỉnh chi tiết các bảng và trường dữ liệu trong kiến trúc Microservices

#### 6.4.1. `member-service`

**Bảng `users`**

*   `created_by`: `VARCHAR(36)`. Lưu trữ `keycloak_id` của người dùng tạo bản ghi. Không còn là khóa ngoại trực tiếp đến `users.id` để tránh ràng buộc tự tham chiếu phức tạp khi `users` là bảng chính của `member-service`.
*   `updated_by`: `VARCHAR(36)`. Tương tự `created_by`.

**Bảng `library_cards`**

*   `user_id`: `BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE`. Vẫn giữ ràng buộc khóa ngoại nội bộ vì `users` và `library_cards` đều thuộc `member-service`.
*   `created_by`: `VARCHAR(36)`. Lưu trữ `keycloak_id` của người dùng tạo bản ghi.
*   `updated_by`: `VARCHAR(36)`. Tương tự `created_by`.

#### 6.4.2. `catalog-service`

**Bảng `authors`**

*   `created_by`: `VARCHAR(36)`. Lưu trữ `keycloak_id` của người dùng tạo bản ghi.
*   `updated_by`: `VARCHAR(36)`. Tương tự `created_by`.

**Bảng `categories`**

*   `parent_id`: `BIGINT REFERENCES categories(id)`. Vẫn giữ ràng buộc khóa ngoại nội bộ.
*   `created_by`: `VARCHAR(36)`. Lưu trữ `keycloak_id` của người dùng tạo bản ghi.
*   `updated_by`: `VARCHAR(36)`. Tương tự `created_by`.

**Bảng `publishers`**

*   `created_by`: `VARCHAR(36)`. Lưu trữ `keycloak_id` của người dùng tạo bản ghi.
*   `updated_by`: `VARCHAR(36)`. Tương tự `created_by`.

**Bảng `books`**

*   `publisher_id`: `BIGINT REFERENCES publishers(id)`. Vẫn giữ ràng buộc khóa ngoại nội bộ.
*   `created_by`: `VARCHAR(36)`. Lưu trữ `keycloak_id` của người dùng tạo bản ghi.
*   `updated_by`: `VARCHAR(36)`. Tương tự `created_by`.

**Bảng `book_authors`**

*   `book_id`: `BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE`. Vẫn giữ ràng buộc khóa ngoại nội bộ.
*   `author_id`: `BIGINT NOT NULL REFERENCES authors(id) ON DELETE CASCADE`. Vẫn giữ ràng buộc khóa ngoại nội bộ.

**Bảng `book_categories`**

*   `book_id`: `BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE`. Vẫn giữ ràng buộc khóa ngoại nội bộ.
*   `category_id`: `BIGINT NOT NULL REFERENCES categories(id) ON DELETE CASCADE`. Vẫn giữ ràng buộc khóa ngoại nội bộ.

**Bảng `book_copies`**

*   `book_id`: `BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE`. Vẫn giữ ràng buộc khóa ngoại nội bộ.
*   `created_by`: `VARCHAR(36)`. Lưu trữ `keycloak_id` của người dùng tạo bản ghi.
*   `updated_by`: `VARCHAR(36)`. Tương tự `created_by`.

#### 6.4.3. `loan-service`

**Bảng `borrowings`**

*   `book_copy_public_id`: `UUID NOT NULL`. Thay thế `book_copy_id` (INT) và loại bỏ ràng buộc khóa ngoại đến `book_copies` (thuộc `catalog-service`). Dịch vụ `loan-service` sẽ sử dụng `public_id` này để tham chiếu đến bản sao sách thông qua API của `catalog-service`.
*   `user_public_id`: `UUID NOT NULL`. Thay thế `user_id` (INT) và loại bỏ ràng buộc khóa ngoại đến `users` (thuộc `member-service`). Dịch vụ `loan-service` sẽ sử dụng `public_id` này để tham chiếu đến người dùng thông qua API của `member-service`.
*   `created_by`: `VARCHAR(36)`. Lưu trữ `keycloak_id` của người dùng tạo bản ghi.
*   `updated_by`: `VARCHAR(36)`. Tương tự `created_by`.

**Bảng `fines`**

*   `borrowing_id`: `BIGINT NOT NULL REFERENCES borrowings(id)`. Vẫn giữ ràng buộc khóa ngoại nội bộ.
*   `created_by`: `VARCHAR(36)`. Lưu trữ `keycloak_id` của người dùng tạo bản ghi.
*   `updated_by`: `VARCHAR(36)`. Tương tự `created_by`.

**Bảng `reservations`**

*   `book_public_id`: `UUID NOT NULL`. Thay thế `book_id` (INT) và loại bỏ ràng buộc khóa ngoại đến `books` (thuộc `catalog-service`).
*   `user_public_id`: `UUID NOT NULL`. Thay thế `user_id` (INT) và loại bỏ ràng buộc khóa ngoại đến `users` (thuộc `member-service`).
*   `created_by`: `VARCHAR(36)`. Lưu trữ `keycloak_id` của người dùng tạo bản ghi.
*   `updated_by`: `VARCHAR(36)`. Tương tự `created_by`.

**Bảng `book_bags`**

*   `user_public_id`: `UUID NOT NULL UNIQUE`. Thay thế `user_id` (INT) và loại bỏ ràng buộc khóa ngoại đến `users` (thuộc `member-service`). `UNIQUE` đảm bảo mỗi người dùng chỉ có một giỏ sách.

**Bảng `book_bag_items`**

*   `book_bag_id`: `BIGINT NOT NULL REFERENCES book_bags(id) ON DELETE CASCADE`. Vẫn giữ ràng buộc khóa ngoại nội bộ.
*   `book_public_id`: `UUID NOT NULL`. Thay thế `book_id` (INT) và loại bỏ ràng buộc khóa ngoại đến `books` (thuộc `catalog-service`).

**Bảng `library_policies`**

*   `created_by`: `VARCHAR(36)`. Lưu trữ `keycloak_id` của người dùng tạo bản ghi.
*   `updated_by`: `VARCHAR(36)`. Tương tự `created_by`.

#### 6.4.4. `notification-service`

**Bảng `notifications`**

*   `user_public_id`: `UUID NOT NULL`. Thay thế `user_id` (INT) và loại bỏ ràng buộc khóa ngoại đến `users` (thuộc `member-service`).

**Bảng `notification_preferences`**

*   `user_public_id`: `UUID NOT NULL UNIQUE`. Thay thế `user_id` (INT) và loại bỏ ràng buộc khóa ngoại đến `users` (thuộc `member-service`). `UNIQUE` đảm bảo mỗi người dùng chỉ có một bộ tùy chọn thông báo.

### 6.5. Tổng kết thay đổi

Việc chuyển đổi sang kiến trúc microservices với cơ sở dữ liệu độc lập cho mỗi dịch vụ mang lại nhiều lợi ích về khả năng mở rộng, độc lập triển khai và khả năng chịu lỗi. Tuy nhiên, nó cũng đòi hỏi việc quản lý tính nhất quán dữ liệu giữa các dịch vụ phải được xử lý ở tầng ứng dụng, thường thông qua các cơ chế giao tiếp phi đồng bộ hoặc các giao dịch phân tán. Việc sử dụng `public_id` (UUID) làm khóa ngoại logic giữa các dịch vụ là một best practice để giảm thiểu sự phụ thuộc và tăng tính linh hoạt cho hệ thống.



