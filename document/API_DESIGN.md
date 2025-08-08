# Thiết kế API cho hệ thống quản lý thư viện Microservices

## 1. Giới thiệu

Tài liệu này trình bày chi tiết thiết kế API cho hệ thống quản lý thư viện dựa trên kiến trúc microservices. Hệ thống bao gồm các dịch vụ chính: `member-service`, `catalog-service`, `loan-service`, và `notification-service`. Mỗi API sẽ được mô tả rõ ràng về endpoint, phương thức HTTP, cấu trúc request, cấu trúc response và ví dụ minh họa.

## 2. Nguyên tắc thiết kế API chung

Để đảm bảo tính nhất quán, dễ sử dụng và khả năng mở rộng, các API trong hệ thống sẽ tuân thủ các nguyên tắc sau:

*   **RESTful Principles**: Sử dụng các nguyên tắc của kiến trúc REST, bao gồm tài nguyên (resources), phương thức HTTP (GET, POST, PUT, DELETE), và mã trạng thái HTTP (HTTP status codes).
*   **Versioning**: Sử dụng tiền tố `/api/v1/` trong tất cả các endpoint để quản lý phiên bản API, cho phép nâng cấp mà không ảnh hưởng đến các client hiện có.
*   **Resource Naming**: Tên tài nguyên (entity name) trong URL sẽ là danh từ số nhiều, viết thường (ví dụ: `/users`, `/books`).
*   **JSON Format**: Tất cả các request và response body sẽ sử dụng định dạng JSON.
*   **Authentication & Authorization**: Các API sẽ được bảo vệ bằng cơ chế xác thực và ủy quyền phù hợp (ví dụ: OAuth2/JWT). Thông tin xác thực sẽ được gửi qua header `Authorization`.
*   **Error Handling**: Trả về các mã trạng thái HTTP chuẩn và cấu trúc lỗi JSON rõ ràng để client có thể xử lý (ví dụ: `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found`, `500 Internal Server Error`).
*   **Pagination, Filtering, Sorting**: Hỗ trợ các tham số truy vấn để phân trang (`page`, `size`), lọc (`filter_by`), và sắp xếp (`sort_by`, `order`) cho các API trả về danh sách tài nguyên.
*   **Idempotency**: Các API `PUT` và `DELETE` nên là idempotent, nghĩa là thực hiện nhiều lần cùng một request sẽ cho cùng một kết quả.
*   **Audit Fields**: Các trường `created_at`, `updated_at`, `created_by`, `updated_by` sẽ được tự động quản lý bởi dịch vụ và không cần gửi trong request body.
*   **Public IDs**: Khi tham chiếu đến các thực thể từ dịch vụ khác, sẽ sử dụng `public_id` (UUID) thay vì ID nội bộ.

## 3. Thiết kế API cho từng Microservice

### 3.1. Member Service API

**Mô tả**: Dịch vụ này quản lý thông tin người dùng và thẻ thư viện. Các API sẽ cho phép tạo, đọc, cập nhật, xóa (CRUD) thông tin người dùng và quản lý thẻ thư viện.

**Base URL**: `/api/v1/`

#### 3.1.1. Quản lý người dùng (`/users`)

##### 3.1.1.1. Lấy danh sách người dùng

*   **Endpoint**: `GET /api/v1/users`
*   **Mô tả**: Lấy danh sách tất cả người dùng trong hệ thống. Hỗ trợ phân trang, lọc và sắp xếp.
*   **Request (Query Parameters)**:
    *   `page` (integer, optional): Số trang (mặc định: 1).
    *   `size` (integer, optional): Số lượng bản ghi trên mỗi trang (mặc định: 10, tối đa: 100).
    *   `username` (string, optional): Lọc theo tên người dùng.
    *   `email` (string, optional): Lọc theo email.
    *   `role` (string, optional): Lọc theo vai trò (MEMBER, LIBRARIAN, ADMIN).
    *   `is_active` (boolean, optional): Lọc theo trạng thái hoạt động.
    *   `sort_by` (string, optional): Trường để sắp xếp (ví dụ: `created_at`, `username`).
    *   `order` (string, optional): Thứ tự sắp xếp (`asc` hoặc `desc`, mặc định: `desc`).
*   **Response (Status: 200 OK)**:
    ```json
    {
        "data": [
            {
                "public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
                "keycloak_id": "xyz123abc-def4-5678-90ab-cdef12345678",
                "username": "john.doe",
                "email": "john.doe@example.com",
                "first_name": "John",
                "last_name": "Doe",
                "phone_number": "+84901234567",
                "address": "123 Main St, City, Country",
                "date_of_birth": "1990-01-15",
                "role": "MEMBER",
                "is_active": true,
                "created_at": "2023-01-01T10:00:00Z",
                "updated_at": "2023-01-01T10:00:00Z"
            }
        ],
        "pagination": {
            "total_items": 100,
            "total_pages": 10,
            "current_page": 1,
            "page_size": 10
        }
    }
    ```
*   **Ví dụ**: Lấy danh sách người dùng là thành viên, trang 2, mỗi trang 5 bản ghi, sắp xếp theo `username` tăng dần.
    `GET /api/v1/users?role=MEMBER&page=2&size=5&sort_by=username&order=asc`

    ```bash
    curl -X GET "/api/v1/users?role=MEMBER&page=2&size=5&sort_by=username&order=asc" \
      -H "Accept: application/json"
    ```

##### 3.1.1.2. Lấy thông tin người dùng theo Public ID

*   **Endpoint**: `GET /api/v1/users/{public_id}`
*   **Mô tả**: Lấy thông tin chi tiết của một người dùng dựa trên `public_id`.
*   **Request**: Không có body.
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "keycloak_id": "xyz123abc-def4-5678-90ab-cdef12345678",
        "username": "john.doe",
        "email": "john.doe@example.com",
        "first_name": "John",
        "last_name": "Doe",
        "phone_number": "+84901234567",
        "address": "123 Main St, City, Country",
        "date_of_birth": "1990-01-15",
        "role": "MEMBER",
        "is_active": true,
        "created_at": "2023-01-01T10:00:00Z",
        "updated_at": "2023-01-01T10:00:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**:
    ```json
    {
        "error": "User not found",
        "message": "Người dùng với public_id 'invalid-id' không tồn tại."
    }
    ```
*   **Ví dụ**: Lấy thông tin người dùng có public_id `a1b2c3d4-e5f6-7890-1234-567890abcdef`.
    `GET /api/v1/users/a1b2c3d4-e5f6-7890-1234-567890abcdef`

    ```bash
    curl -X GET "/api/v1/users/a1b2c3d4-e5f6-7890-1234-567890abcdef" \
      -H "Accept: application/json"
    ```

##### 3.1.1.3. Tạo người dùng mới

*   **Endpoint**: `POST /api/v1/users`
*   **Mô tả**: Tạo một người dùng mới trong hệ thống. `keycloak_id` là bắt buộc và phải duy nhất.
*   **Request (Body)**:
    ```json
    {
        "keycloak_id": "new-keycloak-id-1234-abcd-efgh-ijklmnopqrst",
        "username": "jane.doe",
        "email": "jane.doe@example.com",
        "first_name": "Jane",
        "last_name": "Doe",
        "phone_number": "+84909876543",
        "address": "456 Oak Ave, Town, Country",
        "date_of_birth": "1992-05-20",
        "role": "MEMBER",
        "is_active": true
    }
    ```
*   **Response (Status: 201 Created)**:
    ```json
    {
        "public_id": "f1e2d3c4-b5a6-9876-5432-10fedcba9876",
        "keycloak_id": "new-keycloak-id-1234-abcd-efgh-ijklmnopqrst",
        "username": "jane.doe",
        "email": "jane.doe@example.com",
        "first_name": "Jane",
        "last_name": "Doe",
        "phone_number": "+84909876543",
        "address": "456 Oak Ave, Town, Country",
        "date_of_birth": "1992-05-20",
        "role": "MEMBER",
        "is_active": true,
        "created_at": "2023-07-24T15:30:00Z",
        "updated_at": "2023-07-24T15:30:00Z"
    }
    ```
*   **Response (Status: 400 Bad Request)**:
    ```json
    {
        "error": "Validation Error",
        "message": "Keycloak ID đã tồn tại hoặc dữ liệu không hợp lệ."
    }
    ```
*   **Ví dụ**: Tạo người dùng mới.
    `POST /api/v1/users` với body JSON như trên.

    ```bash
    curl -X POST "/api/v1/users" \
      -H "Content-Type: application/json" \
      -d '{
        "keycloak_id": "new-keycloak-id-1234-abcd-efgh-ijklmnopqrst",
        "username": "jane.doe",
        "email": "jane.doe@example.com",
        "first_name": "Jane",
        "last_name": "Doe",
        "phone_number": "+84909876543",
        "address": "456 Oak Ave, Town, Country",
        "date_of_birth": "1992-05-20",
        "role": "MEMBER",
        "is_active": true
      }'
    ```

##### 3.1.1.4. Cập nhật thông tin người dùng

*   **Endpoint**: `PUT /api/v1/users/{public_id}`
*   **Mô tả**: Cập nhật toàn bộ thông tin của một người dùng. Tất cả các trường trong request body sẽ thay thế các giá trị hiện có.
*   **Request (Body)**:
    ```json
    {
        "username": "john.doe.updated",
        "email": "john.doe.updated@example.com",
        "phone_number": "+84912345678",
        "is_active": false
    }
    ```
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "keycloak_id": "xyz123abc-def4-5678-90ab-cdef12345678",
        "username": "john.doe.updated",
        "email": "john.doe.updated@example.com",
        "first_name": "John",
        "last_name": "Doe",
        "phone_number": "+84912345678",
        "address": "123 Main St, City, Country",
        "date_of_birth": "1990-01-15",
        "role": "MEMBER",
        "is_active": false,
        "created_at": "2023-01-01T10:00:00Z",
        "updated_at": "2023-07-24T16:00:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Cập nhật thông tin người dùng có public_id `a1b2c3d4-e5f6-7890-1234-567890abcdef`.
    `PUT /api/v1/users/a1b2c3d4-e5f6-7890-1234-567890abcdef` với body JSON như trên.

    ```bash
    curl -X PUT "/api/v1/users/a1b2c3d4-e5f6-7890-1234-567890abcdef" \
      -H "Content-Type: application/json" \
      -d "{\"username\": \"john.doe.updated\", \"email\": \"john.doe.updated@example.com\", \"phone_number\": \"+84912345678\", \"is_active\": false}"
    ```

##### 3.1.1.5. Xóa người dùng (Soft Delete)

*   **Endpoint**: `DELETE /api/v1/users/{public_id}`
*   **Mô tả**: Đánh dấu người dùng là đã xóa mềm (`deleted_at` được cập nhật). Người dùng sẽ không còn hoạt động nhưng dữ liệu vẫn được giữ lại.
*   **Request**: Không có body.
*   **Response (Status: 204 No Content)**: Thành công, không trả về nội dung.
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Xóa mềm người dùng có public_id `a1b2c3d4-e5f6-7890-1234-567890abcdef`.
    `DELETE /api/v1/users/a1b2c3d4-e5f6-7890-1234-567890abcdef`

    ```bash
    curl -X DELETE "/api/v1/users/a1b2c3d4-e5f6-7890-1234-567890abcdef"
    ```

#### 3.1.2. Quản lý thẻ thư viện (`/library-cards`)

##### 3.1.2.1. Lấy danh sách thẻ thư viện

*   **Endpoint**: `GET /api/v1/library-cards`
*   **Mô tả**: Lấy danh sách tất cả thẻ thư viện. Hỗ trợ phân trang, lọc và sắp xếp.
*   **Request (Query Parameters)**:
    *   `page` (integer, optional): Số trang.
    *   `size` (integer, optional): Số lượng bản ghi trên mỗi trang.
    *   `card_number` (string, optional): Lọc theo số thẻ.
    *   `user_public_id` (string, optional): Lọc theo public_id của người dùng sở hữu thẻ.
    *   `status` (string, optional): Lọc theo trạng thái thẻ (ACTIVE, INACTIVE, EXPIRED, LOST, BLOCKED).
*   **Response (Status: 200 OK)**:
    ```json
    {
        "data": [
            {
                "public_id": "c1d2e3f4-a5b6-7890-1234-567890abcdef",
                "card_number": "LC001",
                "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
                "issue_date": "2022-01-01",
                "expiry_date": "2025-01-01",
                "status": "ACTIVE",
                "created_at": "2022-01-01T09:00:00Z",
                "updated_at": "2022-01-01T09:00:00Z"
            }
        ],
        "pagination": {
            "total_items": 50,
            "total_pages": 5,
            "current_page": 1,
            "page_size": 10
        }
    }
    ```
*   **Ví dụ**: Lấy danh sách thẻ thư viện đang hoạt động.
    `GET /api/v1/library-cards?status=ACTIVE`

    ```bash
    curl -X GET "/api/v1/library-cards?status=ACTIVE" \
      -H "Accept: application/json"
    ```

##### 3.1.2.2. Lấy thông tin thẻ thư viện theo Public ID

*   **Endpoint**: `GET /api/v1/library-cards/{public_id}`
*   **Mô tả**: Lấy thông tin chi tiết của một thẻ thư viện dựa trên `public_id`.
*   **Request**: Không có body.
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "c1d2e3f4-a5b6-7890-1234-567890abcdef",
        "card_number": "LC001",
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "issue_date": "2022-01-01",
        "expiry_date": "2025-01-01",
        "status": "ACTIVE",
        "created_at": "2022-01-01T09:00:00Z",
        "updated_at": "2022-01-01T09:00:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**: Tương tự như GET users.
*   **Ví dụ**: Lấy thông tin thẻ thư viện có public_id `c1d2e3f4-a5b6-7890-1234-567890abcdef`.
    `GET /api/v1/library-cards/c1d2e3f4-a5b6-7890-1234-567890abcdef`

    ```bash
    curl -X GET "/api/v1/library-cards/c1d2e3f4-a5b6-7890-1234-567890abcdef" \
      -H "Accept: application/json"
    ```

##### 3.1.2.3. Tạo thẻ thư viện mới

*   **Endpoint**: `POST /api/v1/library-cards`
*   **Mô tả**: Tạo một thẻ thư viện mới cho một người dùng. `user_public_id` là bắt buộc.
*   **Request (Body)**:
    ```json
    {
        "card_number": "LC002",
        "user_public_id": "f1e2d3c4-b5a6-9876-5432-10fedcba9876",
        "issue_date": "2023-07-24",
        "expiry_date": "2026-07-24",
        "status": "ACTIVE"
    }
    ```
*   **Response (Status: 201 Created)**:
    ```json
    {
        "public_id": "d1e2f3a4-b5c6-7890-5432-10fedcba9876",
        "card_number": "LC002",
        "user_public_id": "f1e2d3c4-b5a6-9876-5432-10fedcba9876",
        "issue_date": "2023-07-24",
        "expiry_date": "2026-07-24",
        "status": "ACTIVE",
        "created_at": "2023-07-24T17:00:00Z",
        "updated_at": "2023-07-24T17:00:00Z"
    }
    ```
*   **Response (Status: 400 Bad Request)**:
    ```json
    {
        "error": "Validation Error",
        "message": "Số thẻ đã tồn tại hoặc người dùng không hợp lệ."
    }
    ```
*   **Ví dụ**: Tạo thẻ thư viện mới cho người dùng có public_id `f1e2d3c4-b5a6-9876-5432-10fedcba9876`.
    `POST /api/v1/library-cards` với body JSON như trên.

    ```bash
    curl -X POST "/api/v1/library-cards" \
      -H "Content-Type: application/json" \
      -d "{\"card_number\": \"LC002\", \"user_public_id\": \"f1e2d3c4-b5a6-9876-5432-10fedcba9876\", \"issue_date\": \"2023-07-24\", \"expiry_date\": \"2026-07-24\", \"status\": \"ACTIVE\"}"
    ```

##### 3.1.2.4. Cập nhật thông tin thẻ thư viện

*   **Endpoint**: `PUT /api/v1/library-cards/{public_id}`
*   **Mô tả**: Cập nhật toàn bộ thông tin của một thẻ thư viện.
*   **Request (Body)**:
    ```json
    {
        "expiry_date": "2027-01-01",
        "status": "INACTIVE"
    }
    ```
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "c1d2e3f4-a5b6-7890-1234-567890abcdef",
        "card_number": "LC001",
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "issue_date": "2022-01-01",
        "expiry_date": "2027-01-01",
        "status": "INACTIVE",
        "created_at": "2022-01-01T09:00:00Z",
        "updated_at": "2023-07-24T17:30:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Cập nhật trạng thái thẻ thư viện có public_id `c1d2e3f4-a5b6-7890-1234-567890abcdef`.
    `PUT /api/v1/library-cards/c1d2e3f4-a5b6-7890-1234-567890abcdef` với body JSON như trên.

    ```bash
    curl -X PUT "/api/v1/library-cards/c1d2e3f4-a5b6-7890-1234-567890abcdef" \
      -H "Content-Type: application/json" \
      -d "{\"expiry_date\": \"2027-01-01\", \"status\": \"INACTIVE\"}"
    ```

##### 3.1.2.5. Xóa thẻ thư viện (Soft Delete)

*   **Endpoint**: `DELETE /api/v1/library-cards/{public_id}`
*   **Mô tả**: Đánh dấu thẻ thư viện là đã xóa mềm.
*   **Request**: Không có body.
*   **Response (Status: 204 No Content)**: Thành công.
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Xóa mềm thẻ thư viện có public_id `c1d2e3f4-a5b6-7890-1234-567890abcdef`.
    `DELETE /api/v1/library-cards/c1d2e3f4-a5b6-7890-1234-567890abcdef`

    ```bash
    curl -X DELETE "/api/v1/library-cards/c1d2e3f4-a5b6-7890-1234-567890abcdef"
    ```






### 3.2. Catalog Service API

**Mô tả**: Dịch vụ này quản lý thông tin về sách, tác giả, danh mục và nhà xuất bản, cũng như các bản sao sách. Các API sẽ cho phép CRUD thông tin các thực thể này.

**Base URL**: `/api/v1/`

#### 3.2.1. Quản lý tác giả (`/authors`)

##### 3.2.1.1. Lấy danh sách tác giả

*   **Endpoint**: `GET /api/v1/authors`
*   **Mô tả**: Lấy danh sách tất cả tác giả. Hỗ trợ phân trang, lọc và sắp xếp.
*   **Request (Query Parameters)**:
    *   `page` (integer, optional): Số trang.
    *   `size` (integer, optional): Số lượng bản ghi trên mỗi trang.
    *   `name` (string, optional): Lọc theo tên tác giả.
    *   `sort_by` (string, optional): Trường để sắp xếp (ví dụ: `name`, `created_at`).
    *   `order` (string, optional): Thứ tự sắp xếp (`asc` hoặc `desc`).
*   **Response (Status: 200 OK)**:
    ```json
    {
        "data": [
            {
                "public_id": "e1f2g3h4-i5j6-7890-1234-567890abcdef",
                "name": "Nguyễn Nhật Ánh",
                "biography": "Nhà văn nổi tiếng Việt Nam với các tác phẩm dành cho tuổi mới lớn.",
                "created_at": "2023-01-01T10:00:00Z",
                "updated_at": "2023-01-01T10:00:00Z"
            }
        ],
        "pagination": {
            "total_items": 20,
            "total_pages": 2,
            "current_page": 1,
            "page_size": 10
        }
    }
    ```
*   **Ví dụ**: Lấy danh sách tác giả có tên chứa 




    `Nguyễn`.
    `GET /api/v1/authors?name=Nguyễn`

    ```bash
    curl -X GET "/api/v1/authors?name=Nguyễn" \
      -H "Accept: application/json"
    ```

##### 3.2.1.2. Lấy thông tin tác giả theo Public ID

*   **Endpoint**: `GET /api/v1/authors/{public_id}`
*   **Mô tả**: Lấy thông tin chi tiết của một tác giả dựa trên `public_id`.
*   **Request**: Không có body.
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "e1f2g3h4-i5j6-7890-1234-567890abcdef",
        "name": "Nguyễn Nhật Ánh",
        "biography": "Nhà văn nổi tiếng Việt Nam với các tác phẩm dành cho tuổi mới lớn.",
        "created_at": "2023-01-01T10:00:00Z",
        "updated_at": "2023-01-01T10:00:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**:
    ```json
    {
        "error": "Author not found",
        "message": "Tác giả với public_id 'invalid-id' không tồn tại."
    }
    ```
*   **Ví dụ**: Lấy thông tin tác giả có public_id `e1f2g3h4-i5j6-7890-1234-567890abcdef`.
    `GET /api/v1/authors/e1f2g3h4-i5j6-7890-1234-567890abcdef`

    ```bash
    curl -X GET "/api/v1/authors/e1f2g3h4-i5j6-7890-1234-567890abcdef" \
      -H "Accept: application/json"
    ```

##### 3.2.1.3. Tạo tác giả mới

*   **Endpoint**: `POST /api/v1/authors`
*   **Mô tả**: Tạo một tác giả mới.
*   **Request (Body)**:
    ```json
    {
        "name": "Tô Hoài",
        "biography": "Nhà văn Việt Nam nổi tiếng với tác phẩm Dế Mèn phiêu lưu ký."
    }
    ```
*   **Response (Status: 201 Created)**:
    ```json
    {
        "public_id": "f1g2h3i4-j5k6-7890-5432-10fedcba9876",
        "name": "Tô Hoài",
        "biography": "Nhà văn Việt Nam nổi tiếng với tác phẩm Dế Mèn phiêu lưu ký.",
        "created_at": "2023-07-24T18:00:00Z",
        "updated_at": "2023-07-24T18:00:00Z"
    }
    ```
*   **Response (Status: 400 Bad Request)**:
    ```json
    {
        "error": "Validation Error",
        "message": "Tên tác giả không được để trống."
    }
    ```
*   **Ví dụ**: Tạo tác giả mới.
    `POST /api/v1/authors` với body JSON như trên.

    ```bash
    curl -X POST "/api/v1/authors" \
      -H "Content-Type: application/json" \
      -d "{\"name\": \"Tô Hoài\", \"biography\": \"Nhà văn Việt Nam nổi tiếng với tác phẩm Dế Mèn phiêu lưu ký.\"}"
    ```

##### 3.2.1.4. Cập nhật thông tin tác giả

*   **Endpoint**: `PUT /api/v1/authors/{public_id}`
*   **Mô tả**: Cập nhật toàn bộ thông tin của một tác giả.
*   **Request (Body)**:
    ```json
    {
        "name": "Tô Hoài (cập nhật)",
        "biography": "Nhà văn Việt Nam nổi tiếng với tác phẩm Dế Mèn phiêu lưu ký, đã được cập nhật."
    }
    ```
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "f1g2h3i4-j5k6-7890-5432-10fedcba9876",
        "name": "Tô Hoài (cập nhật)",
        "biography": "Nhà văn Việt Nam nổi tiếng với tác phẩm Dế Mèn phiêu lưu ký, đã được cập nhật.",
        "created_at": "2023-07-24T18:00:00Z",
        "updated_at": "2023-07-24T18:30:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Cập nhật thông tin tác giả có public_id `f1g2h3i4-j5k6-7890-5432-10fedcba9876`.
    `PUT /api/v1/authors/f1g2h3i4-j5k6-7890-5432-10fedcba9876` với body JSON như trên.

    ```bash
    curl -X PUT "/api/v1/authors/f1g2h3i4-j5k6-7890-5432-10fedcba9876" \
      -H "Content-Type: application/json" \
      -d "{\"name\": \"Tô Hoài (cập nhật)\", \"biography\": \"Nhà văn Việt Nam nổi tiếng với tác phẩm Dế Mèn phiêu lưu ký, đã được cập nhật.\"}"
    ```

##### 3.2.1.5. Xóa tác giả (Soft Delete)

*   **Endpoint**: `DELETE /api/v1/authors/{public_id}`
*   **Mô tả**: Đánh dấu tác giả là đã xóa mềm.
*   **Request**: Không có body.
*   **Response (Status: 204 No Content)**: Thành công.
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Xóa mềm tác giả có public_id `f1g2h3i4-j5k6-7890-5432-10fedcba9876`.
    `DELETE /api/v1/authors/f1g2h3i4-j5k6-7890-5432-10fedcba9876`

    ```bash
    curl -X DELETE "/api/v1/authors/f1g2h3i4-j5k6-7890-5432-10fedcba9876"
    ```

#### 3.2.2. Quản lý danh mục (`/categories`)

##### 3.2.2.1. Lấy danh sách danh mục

*   **Endpoint**: `GET /api/v1/categories`
*   **Mô tả**: Lấy danh sách tất cả danh mục. Hỗ trợ phân trang, lọc và sắp xếp.
*   **Request (Query Parameters)**:
    *   `page` (integer, optional): Số trang.
    *   `size` (integer, optional): Số lượng bản ghi trên mỗi trang.
    *   `name` (string, optional): Lọc theo tên danh mục.
    *   `slug` (string, optional): Lọc theo slug.
    *   `parent_id` (string, optional): Lọc theo public_id của danh mục cha.
    *   `sort_by` (string, optional): Trường để sắp xếp (ví dụ: `name`, `created_at`).
    *   `order` (string, optional): Thứ tự sắp xếp (`asc` hoặc `desc`).
*   **Response (Status: 200 OK)**:
    ```json
    {
        "data": [
            {
                "public_id": "g1h2i3j4-k5l6-7890-1234-567890abcdef",
                "name": "Văn học",
                "slug": "van-hoc",
                "description": "Các tác phẩm văn học.",
                "parent_id": null,
                "created_at": "2023-01-01T10:00:00Z",
                "updated_at": "2023-01-01T10:00:00Z"
            }
        ],
        "pagination": {
            "total_items": 15,
            "total_pages": 2,
            "current_page": 1,
            "page_size": 10
        }
    }
    ```
*   **Ví dụ**: Lấy danh sách danh mục con của danh mục có public_id `g1h2i3j4-k5l6-7890-1234-567890abcdef`.
    `GET /api/v1/categories?parent_id=g1h2i3j4-k5l6-7890-1234-567890abcdef`

    ```bash
    curl -X GET "/api/v1/categories?parent_id=g1h2i3j4-k5l6-7890-1234-567890abcdef" \
      -H "Accept: application/json"
    ```

##### 3.2.2.2. Lấy thông tin danh mục theo Public ID

*   **Endpoint**: `GET /api/v1/categories/{public_id}`
*   **Mô tả**: Lấy thông tin chi tiết của một danh mục dựa trên `public_id`.
*   **Request**: Không có body.
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "g1h2i3j4-k5l6-7890-1234-567890abcdef",
        "name": "Văn học",
        "slug": "van-hoc",
        "description": "Các tác phẩm văn học.",
        "parent_id": null,
        "created_at": "2023-01-01T10:00:00Z",
        "updated_at": "2023-01-01T10:00:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**: Tương tự như GET authors.
*   **Ví dụ**: Lấy thông tin danh mục có public_id `g1h2i3j4-k5l6-7890-1234-567890abcdef`.
    `GET /api/v1/categories/g1h2i3j4-k5l6-7890-1234-567890abcdef`

    ```bash
    curl -X GET "/api/v1/categories/g1h2i3j4-k5l6-7890-1234-567890abcdef" \
      -H "Accept: application/json"
    ```

##### 3.2.2.3. Tạo danh mục mới

*   **Endpoint**: `POST /api/v1/categories`
*   **Mô tả**: Tạo một danh mục mới.
*   **Request (Body)**:
    ```json
    {
        "name": "Tiểu thuyết",
        "slug": "tieu-thuyet",
        "description": "Các tác phẩm tiểu thuyết.",
        "parent_id": "g1h2i3j4-k5l6-7890-1234-567890abcdef" 
    }
    ```
*   **Response (Status: 201 Created)**:
    ```json
    {
        "public_id": "h1i2j3k4-l5m6-7890-5432-10fedcba9876",
        "name": "Tiểu thuyết",
        "slug": "tieu-thuyet",
        "description": "Các tác phẩm tiểu thuyết.",
        "parent_id": "g1h2i3j4-k5l6-7890-1234-567890abcdef",
        "created_at": "2023-07-24T19:00:00Z",
        "updated_at": "2023-07-24T19:00:00Z"
    }
    ```
*   **Response (Status: 400 Bad Request)**:
    ```json
    {
        "error": "Validation Error",
        "message": "Tên danh mục hoặc slug đã tồn tại."
    }
    ```
*   **Ví dụ**: Tạo danh mục mới.
    `POST /api/v1/categories` với body JSON như trên.

    ```bash
    curl -X POST "/api/v1/categories" \
      -H "Content-Type: application/json" \
      -d "{\"name\": \"Tiểu thuyết\", \"slug\": \"tieu-thuyet\", \"description\": \"Các tác phẩm tiểu thuyết.\", \"parent_id\": \"g1h2i3j4-k5l6-7890-1234-567890abcdef\"}"
    ```

##### 3.2.2.4. Cập nhật thông tin danh mục

*   **Endpoint**: `PUT /api/v1/categories/{public_id}`
*   **Mô tả**: Cập nhật toàn bộ thông tin của một danh mục.
*   **Request (Body)**:
    ```json
    {
        "name": "Tiểu thuyết (cập nhật)",
        "description": "Các tác phẩm tiểu thuyết đã được cập nhật."
    }
    ```
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "h1i2j3k4-l5m6-7890-5432-10fedcba9876",
        "name": "Tiểu thuyết (cập nhật)",
        "slug": "tieu-thuyet",
        "description": "Các tác phẩm tiểu thuyết đã được cập nhật.",
        "parent_id": "g1h2i3j4-k5l6-7890-1234-567890abcdef",
        "created_at": "2023-07-24T19:00:00Z",
        "updated_at": "2023-07-24T19:30:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Cập nhật thông tin danh mục có public_id `h1i2j3k4-l5m6-7890-5432-10fedcba9876`.
    `PUT /api/v1/categories/h1i2j3k4-l5m6-7890-5432-10fedcba9876` với body JSON như trên.

    ```bash
    curl -X PUT "/api/v1/categories/h1i2j3k4-l5m6-7890-5432-10fedcba9876" \
      -H "Content-Type: application/json" \
      -d "{\"name\": \"Tiểu thuyết (cập nhật)\", \"description\": \"Các tác phẩm tiểu thuyết đã được cập nhật.\"}"
    ```

##### 3.2.2.5. Xóa danh mục (Soft Delete)

*   **Endpoint**: `DELETE /api/v1/categories/{public_id}`
*   **Mô tả**: Đánh dấu danh mục là đã xóa mềm.
*   **Request**: Không có body.
*   **Response (Status: 204 No Content)**: Thành công.
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Xóa mềm danh mục có public_id `h1i2j3k4-l5m6-7890-5432-10fedcba9876`.
    `DELETE /api/v1/categories/h1i2j3k4-l5m6-7890-5432-10fedcba9876`

    ```bash
    curl -X DELETE "/api/v1/categories/h1i2j3k4-l5m6-7890-5432-10fedcba9876"
    ```

#### 3.2.3. Quản lý nhà xuất bản (`/publishers`)

##### 3.2.3.1. Lấy danh sách nhà xuất bản

*   **Endpoint**: `GET /api/v1/publishers`
*   **Mô tả**: Lấy danh sách tất cả nhà xuất bản. Hỗ trợ phân trang, lọc và sắp xếp.
*   **Request (Query Parameters)**:
    *   `page` (integer, optional): Số trang.
    *   `size` (integer, optional): Số lượng bản ghi trên mỗi trang.
    *   `name` (string, optional): Lọc theo tên nhà xuất bản.
    *   `sort_by` (string, optional): Trường để sắp xếp (ví dụ: `name`, `created_at`).
    *   `order` (string, optional): Thứ tự sắp xếp (`asc` hoặc `desc`).
*   **Response (Status: 200 OK)**:
    ```json
    {
        "data": [
            {
                "public_id": "i1j2k3l4-m5n6-7890-1234-567890abcdef",
                "name": "Nhà xuất bản Trẻ",
                "address": "TP. Hồ Chí Minh, Việt Nam",
                "created_at": "2023-01-01T10:00:00Z",
                "updated_at": "2023-01-01T10:00:00Z"
            }
        ],
        "pagination": {
            "total_items": 10,
            "total_pages": 1,
            "current_page": 1,
            "page_size": 10
        }
    }
    ```
*   **Ví dụ**: Lấy danh sách nhà xuất bản có tên chứa `Trẻ`.
    `GET /api/v1/publishers?name=Trẻ`

    ```bash
    curl -X GET "/api/v1/publishers?name=Trẻ" \
      -H "Accept: application/json"
    ```

##### 3.2.3.2. Lấy thông tin nhà xuất bản theo Public ID

*   **Endpoint**: `GET /api/v1/publishers/{public_id}`
*   **Mô tả**: Lấy thông tin chi tiết của một nhà xuất bản dựa trên `public_id`.
*   **Request**: Không có body.
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "i1j2k3l4-m5n6-7890-1234-567890abcdef",
        "name": "Nhà xuất bản Trẻ",
        "address": "TP. Hồ Chí Minh, Việt Nam",
        "created_at": "2023-01-01T10:00:00Z",
        "updated_at": "2023-01-01T10:00:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**: Tương tự như GET authors.
*   **Ví dụ**: Lấy thông tin nhà xuất bản có public_id `i1j2k3l4-m5n6-7890-1234-567890abcdef`.
    `GET /api/v1/publishers/i1j2k3l4-m5n6-7890-1234-567890abcdef`

    ```bash
    curl -X GET "/api/v1/publishers/i1j2k3l4-m5n6-7890-1234-567890abcdef" \
      -H "Accept: application/json"
    ```

##### 3.2.3.3. Tạo nhà xuất bản mới

*   **Endpoint**: `POST /api/v1/publishers`
*   **Mô tả**: Tạo một nhà xuất bản mới.
*   **Request (Body)**:
    ```json
    {
        "name": "Nhà xuất bản Kim Đồng",
        "address": "Hà Nội, Việt Nam"
    }
    ```
*   **Response (Status: 201 Created)**:
    ```json
    {
        "public_id": "j1k2l3m4-n5o6-7890-5432-10fedcba9876",
        "name": "Nhà xuất bản Kim Đồng",
        "address": "Hà Nội, Việt Nam",
        "created_at": "2023-07-24T20:00:00Z",
        "updated_at": "2023-07-24T20:00:00Z"
    }
    ```
*   **Response (Status: 400 Bad Request)**:
    ```json
    {
        "error": "Validation Error",
        "message": "Tên nhà xuất bản không được để trống."
    }
    ```
*   **Ví dụ**: Tạo nhà xuất bản mới.
    `POST /api/v1/publishers` với body JSON như trên.

    ```bash
    curl -X POST "/api/v1/publishers" \
      -H "Content-Type: application/json" \
      -d "{\"name\": \"Nhà xuất bản Kim Đồng\", \"address\": \"Hà Nội, Việt Nam\"}"
    ```

##### 3.2.3.4. Cập nhật thông tin nhà xuất bản

*   **Endpoint**: `PUT /api/v1/publishers/{public_id}`
*   **Mô tả**: Cập nhật toàn bộ thông tin của một nhà xuất bản.
*   **Request (Body)**:
    ```json
    {
        "name": "Nhà xuất bản Kim Đồng (cập nhật)",
        "address": "Hà Nội, Việt Nam (cập nhật)"
    }
    ```
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "j1k2l3m4-n5o6-7890-5432-10fedcba9876",
        "name": "Nhà xuất bản Kim Đồng (cập nhật)",
        "address": "Hà Nội, Việt Nam (cập nhật)",
        "created_at": "2023-07-24T20:00:00Z",
        "updated_at": "2023-07-24T20:30:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Cập nhật thông tin nhà xuất bản có public_id `j1k2l3m4-n5o6-7890-5432-10fedcba9876`.
    `PUT /api/v1/publishers/j1k2l3m4-n5o6-7890-5432-10fedcba9876` với body JSON như trên.

    ```bash
    curl -X PUT "/api/v1/publishers/j1k2l3m4-n5o6-7890-5432-10fedcba9876" \
      -H "Content-Type: application/json" \
      -d "{\"name\": \"Nhà xuất bản Kim Đồng (cập nhật)\", \"address\": \"Hà Nội, Việt Nam (cập nhật)\"}"
    ```

##### 3.2.3.5. Xóa nhà xuất bản (Soft Delete)

*   **Endpoint**: `DELETE /api/v1/publishers/{public_id}`
*   **Mô tả**: Đánh dấu nhà xuất bản là đã xóa mềm.
*   **Request**: Không có body.
*   **Response (Status: 204 No Content)**: Thành công.
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Xóa mềm nhà xuất bản có public_id `j1k2l3m4-n5o6-7890-5432-10fedcba9876`.
    `DELETE /api/v1/publishers/j1k2l3m4-n5o6-7890-5432-10fedcba9876`

    ```bash
    curl -X DELETE "/api/v1/publishers/j1k2l3m4-n5o6-7890-5432-10fedcba9876"
    ```

#### 3.2.4. Quản lý sách (`/books`)

##### 3.2.4.1. Lấy danh sách sách

*   **Endpoint**: `GET /api/v1/books`
*   **Mô tả**: Lấy danh sách tất cả sách. Hỗ trợ phân trang, lọc và sắp xếp.
*   **Request (Query Parameters)**:
    *   `page` (integer, optional): Số trang.
    *   `size` (integer, optional): Số lượng bản ghi trên mỗi trang.
    *   `title` (string, optional): Lọc theo tiêu đề sách.
    *   `isbn` (string, optional): Lọc theo ISBN.
    *   `publisher_public_id` (string, optional): Lọc theo public_id của nhà xuất bản.
    *   `author_public_id` (string, optional): Lọc theo public_id của tác giả.
    *   `category_public_id` (string, optional): Lọc theo public_id của danh mục.
    *   `publication_year` (integer, optional): Lọc theo năm xuất bản.
    *   `language` (string, optional): Lọc theo ngôn ngữ.
    *   `sort_by` (string, optional): Trường để sắp xếp (ví dụ: `title`, `publication_year`).
    *   `order` (string, optional): Thứ tự sắp xếp (`asc` hoặc `desc`).
*   **Response (Status: 200 OK)**:
    ```json
    {
        "data": [
            {
                "public_id": "k1l2m3n4-o5p6-7890-1234-567890abcdef",
                "title": "Mắt biếc",
                "isbn": "978-604-1-08920-7",
                "publisher_public_id": "i1j2k3l4-m5n6-7890-1234-567890abcdef",
                "publication_year": 2019,
                "description": "Tiểu thuyết của Nguyễn Nhật Ánh.",
                "language": "Tiếng Việt",
                "number_of_pages": 250,
                "cover_image_url": "https://example.com/matbiec.jpg",
                "authors": [
                    {
                        "public_id": "e1f2g3h4-i5j6-7890-1234-567890abcdef",
                        "name": "Nguyễn Nhật Ánh"
                    }
                ],
                "categories": [
                    {
                        "public_id": "g1h2i3j4-k5l6-7890-1234-567890abcdef",
                        "name": "Văn học"
                    },
                    {
                        "public_id": "h1i2j3k4-l5m6-7890-5432-10fedcba9876",
                        "name": "Tiểu thuyết"
                    }
                ],
                "created_at": "2023-01-01T10:00:00Z",
                "updated_at": "2023-01-01T10:00:00Z"
            }
        ],
        "pagination": {
            "total_items": 100,
            "total_pages": 10,
            "current_page": 1,
            "page_size": 10
        }
    }
    ```
*   **Ví dụ**: Lấy danh sách sách của tác giả Nguyễn Nhật Ánh.
    `GET /api/v1/books?author_public_id=e1f2g3h4-i5j6-7890-1234-567890abcdef`

    ```bash
    curl -X GET "/api/v1/books?author_public_id=e1f2g3h4-i5j6-7890-1234-567890abcdef" \
      -H "Accept: application/json"
    ```

##### 3.2.4.2. Lấy thông tin sách theo Public ID

*   **Endpoint**: `GET /api/v1/books/{public_id}`
*   **Mô tả**: Lấy thông tin chi tiết của một cuốn sách dựa trên `public_id`.
*   **Request**: Không có body.
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "k1l2m3n4-o5p6-7890-1234-567890abcdef",
        "title": "Mắt biếc",
        "isbn": "978-604-1-08920-7",
        "publisher_public_id": "i1j2k3l4-m5n6-7890-1234-567890abcdef",
        "publication_year": 2019,
        "description": "Tiểu thuyết của Nguyễn Nhật Ánh.",
        "language": "Tiếng Việt",
        "number_of_pages": 250,
        "cover_image_url": "https://example.com/matbiec.jpg",
        "authors": [
            {
                "public_id": "e1f2g3h4-i5j6-7890-1234-567890abcdef",
                "name": "Nguyễn Nhật Ánh"
            }
        ],
        "categories": [
            {
                "public_id": "g1h2i3j4-k5l6-7890-1234-567890abcdef",
                "name": "Văn học"
            },
            {
                "public_id": "h1i2j3k4-l5m6-7890-5432-10fedcba9876",
                "name": "Tiểu thuyết"
            }
        ],
        "created_at": "2023-01-01T10:00:00Z",
        "updated_at": "2023-01-01T10:00:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**: Tương tự như GET authors.
*   **Ví dụ**: Lấy thông tin sách có public_id `k1l2m3n4-o5p6-7890-1234-567890abcdef`.
    `GET /api/v1/books/k1l2m3n4-o5p6-7890-1234-567890abcdef`

    ```bash
    curl -X GET "/api/v1/books/k1l2m3n4-o5p6-7890-1234-567890abcdef" \
      -H "Accept: application/json"
    ```

##### 3.2.4.3. Tạo sách mới

*   **Endpoint**: `POST /api/v1/books`
*   **Mô tả**: Tạo một cuốn sách mới. Bao gồm thông tin về tác giả và danh mục.
*   **Request (Body)**:
    ```json
    {
        "title": "Dế Mèn phiêu lưu ký",
        "isbn": "978-604-1-08921-4",
        "publisher_public_id": "j1k2l3m4-n5o6-7890-5432-10fedcba9876",
        "publication_year": 1941,
        "description": "Truyện đồng thoại kinh điển của Tô Hoài.",
        "language": "Tiếng Việt",
        "number_of_pages": 150,
        "cover_image_url": "https://example.com/demen.jpg",
        "author_public_ids": [
            "f1g2h3i4-j5k6-7890-5432-10fedcba9876"
        ],
        "category_public_ids": [
            "g1h2i3j4-k5l6-7890-1234-567890abcdef"
        ]
    }
    ```
*   **Response (Status: 201 Created)**:
    ```json
    {
        "public_id": "l1m2n3o4-p5q6-7890-5432-10fedcba9876",
        "title": "Dế Mèn phiêu lưu ký",
        "isbn": "978-604-1-08921-4",
        "publisher_public_id": "j1k2l3m4-n5o6-7890-5432-10fedcba9876",
        "publication_year": 1941,
        "description": "Truyện đồng thoại kinh điển của Tô Hoài.",
        "language": "Tiếng Việt",
        "number_of_pages": 150,
        "cover_image_url": "https://example.com/demen.jpg",
        "authors": [
            {
                "public_id": "f1g2h3i4-j5k6-7890-5432-10fedcba9876",
                "name": "Tô Hoài"
            }
        ],
        "categories": [
            {
                "public_id": "g1h2i3j4-k5l6-7890-1234-567890abcdef",
                "name": "Văn học"
            }
        ],
        "created_at": "2023-07-24T21:00:00Z",
        "updated_at": "2023-07-24T21:00:00Z"
    }
    ```
*   **Response (Status: 400 Bad Request)**:
    ```json
    {
        "error": "Validation Error",
        "message": "ISBN đã tồn tại hoặc dữ liệu không hợp lệ."
    }
    ```
*   **Ví dụ**: Tạo sách mới.
    `POST /api/v1/books` với body JSON như trên.

    ```bash
    curl -X POST "/api/v1/books" \
      -H "Content-Type: application/json" \
      -d "{\"title\": \"Dế Mèn phiêu lưu ký\", \"isbn\": \"978-604-1-08921-4\", \"publisher_public_id\": \"j1k2l3m4-n5o6-7890-5432-10fedcba9876\", \"publication_year\": 1941, \"description\": \"Truyện đồng thoại kinh điển của Tô Hoài.\", \"language\": \"Tiếng Việt\", \"number_of_pages\": 150, \"cover_image_url\": \"https://example.com/demen.jpg\", \"author_public_ids\": [\"f1g2h3i4-j5k6-7890-5432-10fedcba9876\"], \"category_public_ids\": [\"g1h2i3j4-k5l6-7890-1234-567890abcdef\"]}"
    ```

##### 3.2.4.4. Cập nhật thông tin sách

*   **Endpoint**: `PUT /api/v1/books/{public_id}`
*   **Mô tả**: Cập nhật toàn bộ thông tin của một cuốn sách.
*   **Request (Body)**:
    ```json
    {
        "title": "Dế Mèn phiêu lưu ký (bản mới)",
        "publication_year": 2020,
        "author_public_ids": [
            "f1g2h3i4-j5k6-7890-5432-10fedcba9876"
        ],
        "category_public_ids": [
            "g1h2i3j4-k5l6-7890-1234-567890abcdef",
            "h1i2j3k4-l5m6-7890-5432-10fedcba9876"
        ]
    }
    ```
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "l1m2n3o4-p5q6-7890-5432-10fedcba9876",
        "title": "Dế Mèn phiêu lưu ký (bản mới)",
        "isbn": "978-604-1-08921-4",
        "publisher_public_id": "j1k2l3m4-n5o6-7890-5432-10fedcba9876",
        "publication_year": 2020,
        "description": "Truyện đồng thoại kinh điển của Tô Hoài.",
        "language": "Tiếng Việt",
        "number_of_pages": 150,
        "cover_image_url": "https://example.com/demen.jpg",
        "authors": [
            {
                "public_id": "f1g2h3i4-j5k6-7890-5432-10fedcba9876",
                "name": "Tô Hoài"
            }
        ],
        "categories": [
            {
                "public_id": "g1h2i3j4-k5l6-7890-1234-567890abcdef",
                "name": "Văn học"
            },
            {
                "public_id": "h1i2j3k4-l5m6-7890-5432-10fedcba9876",
                "name": "Tiểu thuyết"
            }
        ],
        "created_at": "2023-07-24T21:00:00Z",
        "updated_at": "2023-07-24T21:30:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Cập nhật thông tin sách có public_id `l1m2n3o4-p5q6-7890-5432-10fedcba9876`.
    `PUT /api/v1/books/l1m2n3o4-p5q6-7890-5432-10fedcba9876` với body JSON như trên.

    ```bash
    curl -X PUT "/api/v1/books/l1m2n3o4-p5q6-7890-5432-10fedcba9876" \
      -H "Content-Type: application/json" \
      -d "{\"title\": \"Dế Mèn phiêu lưu ký (bản mới)\", \"publication_year\": 2020, \"author_public_ids\": [\"f1g2h3i4-j5k6-7890-5432-10fedcba9876\"], \"category_public_ids\": [\"g1h2i3j4-k5l6-7890-1234-567890abcdef\", \"h1i2j3k4-l5m6-7890-5432-10fedcba9876\"]}"
    ```

##### 3.2.4.5. Xóa sách (Soft Delete)

*   **Endpoint**: `DELETE /api/v1/books/{public_id}`
*   **Mô tả**: Đánh dấu sách là đã xóa mềm.
*   **Request**: Không có body.
*   **Response (Status: 204 No Content)**: Thành công.
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Xóa mềm sách có public_id `l1m2n3o4-p5q6-7890-5432-10fedcba9876`.
    `DELETE /api/v1/books/l1m2n3o4-p5q6-7890-5432-10fedcba9876`

    ```bash
    curl -X DELETE "/api/v1/books/l1m2n3o4-p5q6-7890-5432-10fedcba9876"
    ```

#### 3.2.5. Quản lý bản sao sách (`/book-copies`)

##### 3.2.5.1. Lấy danh sách bản sao sách

*   **Endpoint**: `GET /api/v1/book-copies`
*   **Mô tả**: Lấy danh sách tất cả bản sao sách. Hỗ trợ phân trang, lọc và sắp xếp.
*   **Request (Query Parameters)**:
    *   `page` (integer, optional): Số trang.
    *   `size` (integer, optional): Số lượng bản ghi trên mỗi trang.
    *   `book_public_id` (string, optional): Lọc theo public_id của đầu sách.
    *   `copy_number` (string, optional): Lọc theo số bản sao.
    *   `status` (string, optional): Lọc theo trạng thái (AVAILABLE, BORROWED, RESERVED, MAINTENANCE, LOST).
    *   `condition` (string, optional): Lọc theo tình trạng (NEW, GOOD, FAIR, POOR, DAMAGED).
    *   `location` (string, optional): Lọc theo vị trí.
    *   `sort_by` (string, optional): Trường để sắp xếp (ví dụ: `copy_number`, `created_at`).
    *   `order` (string, optional): Thứ tự sắp xếp (`asc` hoặc `desc`).
*   **Response (Status: 200 OK)**:
    ```json
    {
        "data": [
            {
                "public_id": "m1n2o3p4-q5r6-7890-1234-567890abcdef",
                "book_public_id": "k1l2m3n4-o5p6-7890-1234-567890abcdef",
                "copy_number": "001",
                "status": "AVAILABLE",
                "condition": "NEW",
                "location": "Kệ A1, Tầng 1",
                "created_at": "2023-01-01T10:00:00Z",
                "updated_at": "2023-01-01T10:00:00Z"
            }
        ],
        "pagination": {
            "total_items": 50,
            "total_pages": 5,
            "current_page": 1,
            "page_size": 10
        }
    }
    ```
*   **Ví dụ**: Lấy danh sách bản sao sách của sách có public_id `k1l2m3n4-o5p6-7890-1234-567890abcdef` và trạng thái `AVAILABLE`.
    `GET /api/v1/book-copies?book_public_id=k1l2m3n4-o5p6-7890-1234-567890abcdef&status=AVAILABLE`

    ```bash
    curl -X GET "/api/v1/book-copies?book_public_id=k1l2m3n4-o5p6-7890-1234-567890abcdef&status=AVAILABLE" \
      -H "Accept: application/json"
    ```

##### 3.2.5.2. Lấy thông tin bản sao sách theo Public ID

*   **Endpoint**: `GET /api/v1/book-copies/{public_id}`
*   **Mô tả**: Lấy thông tin chi tiết của một bản sao sách dựa trên `public_id`.
*   **Request**: Không có body.
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "m1n2o3p4-q5r6-7890-1234-567890abcdef",
        "book_public_id": "k1l2m3n4-o5p6-7890-1234-567890abcdef",
        "copy_number": "001",
        "status": "AVAILABLE",
        "condition": "NEW",
        "location": "Kệ A1, Tầng 1",
        "created_at": "2023-01-01T10:00:00Z",
        "updated_at": "2023-01-01T10:00:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**: Tương tự như GET authors.
*   **Ví dụ**: Lấy thông tin bản sao sách có public_id `m1n2o3p4-q5r6-7890-1234-567890abcdef`.
    `GET /api/v1/book-copies/m1n2o3p4-q5r6-7890-1234-567890abcdef`

    ```bash
    curl -X GET "/api/v1/book-copies/m1n2o3p4-q5r6-7890-1234-567890abcdef" \
      -H "Accept: application/json"
    ```

##### 3.2.5.3. Tạo bản sao sách mới

*   **Endpoint**: `POST /api/v1/book-copies`
*   **Mô tả**: Tạo một bản sao sách mới cho một đầu sách.
*   **Request (Body)**:
    ```json
    {
        "book_public_id": "k1l2m3n4-o5p6-7890-1234-567890abcdef",
        "copy_number": "002",
        "status": "AVAILABLE",
        "condition": "GOOD",
        "location": "Kệ A1, Tầng 1"
    }
    ```
*   **Response (Status: 201 Created)**:
    ```json
    {
        "public_id": "n1o2p3q4-r5s6-7890-5432-10fedcba9876",
        "book_public_id": "k1l2m3n4-o5p6-7890-1234-567890abcdef",
        "copy_number": "002",
        "status": "AVAILABLE",
        "condition": "GOOD",
        "location": "Kệ A1, Tầng 1",
        "created_at": "2023-07-24T22:00:00Z",
        "updated_at": "2023-07-24T22:00:00Z"
    }
    ```
*   **Response (Status: 400 Bad Request)**:
    ```json
    {
        "error": "Validation Error",
        "message": "Số bản sao đã tồn tại cho đầu sách này hoặc dữ liệu không hợp lệ."
    }
    ```
*   **Ví dụ**: Tạo bản sao sách mới.
    `POST /api/v1/book-copies` với body JSON như trên.

    ```bash
    curl -X POST "/api/v1/book-copies" \
      -H "Content-Type: application/json" \
      -d "{\"book_public_id\": \"k1l2m3n4-o5p6-7890-1234-567890abcdef\", \"copy_number\": \"002\", \"status\": \"AVAILABLE\", \"condition\": \"GOOD\", \"location\": \"Kệ A1, Tầng 1\"}"
    ```

##### 3.2.5.4. Cập nhật thông tin bản sao sách

*   **Endpoint**: `PUT /api/v1/book-copies/{public_id}`
*   **Mô tả**: Cập nhật toàn bộ thông tin của một bản sao sách.
*   **Request (Body)**:
    ```json
    {
        "status": "MAINTENANCE",
        "condition": "DAMAGED"
    }
    ```
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "m1n2o3p4-q5r6-7890-1234-567890abcdef",
        "book_public_id": "k1l2m3n4-o5p6-7890-1234-567890abcdef",
        "copy_number": "001",
        "status": "MAINTENANCE",
        "condition": "DAMAGED",
        "location": "Kệ A1, Tầng 1",
        "created_at": "2023-01-01T10:00:00Z",
        "updated_at": "2023-07-24T22:30:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Cập nhật trạng thái bản sao sách có public_id `m1n2o3p4-q5r6-7890-1234-567890abcdef`.
    `PUT /api/v1/book-copies/m1n2o3p4-q5r6-7890-1234-567890abcdef` với body JSON như trên.

    ```bash
    curl -X PUT "/api/v1/book-copies/m1n2o3p4-q5r6-7890-1234-567890abcdef" \
      -H "Content-Type: application/json" \
      -d "{\"status\": \"MAINTENANCE\", \"condition\": \"DAMAGED\"}"
    ```

##### 3.2.5.5. Xóa bản sao sách (Soft Delete)

*   **Endpoint**: `DELETE /api/v1/book-copies/{public_id}`
*   **Mô tả**: Đánh dấu bản sao sách là đã xóa mềm.
*   **Request**: Không có body.
*   **Response (Status: 204 No Content)**: Thành công.
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Xóa mềm bản sao sách có public_id `m1n2o3p4-q5r6-7890-1234-567890abcdef`.
    `DELETE /api/v1/book-copies/m1n2o3p4-q5r6-7890-1234-567890abcdef`

    ```bash
    curl -X DELETE "/api/v1/book-copies/m1n2o3p4-q5r6-7890-1234-567890abcdef"
    ```






### 3.3. Loan Service API

**Mô tả**: Dịch vụ này quản lý các nghiệp vụ liên quan đến mượn, trả, đặt trước sách, quản lý phạt và các quy định của thư viện. Nó sẽ tương tác với `member-service` và `catalog-service` thông qua `public_id`.

**Base URL**: `/api/v1/`

#### 3.3.1. Quản lý mượn trả (`/borrowings`)

##### 3.3.1.1. Lấy danh sách phiếu mượn

*   **Endpoint**: `GET /api/v1/borrowings`
*   **Mô tả**: Lấy danh sách tất cả các phiếu mượn. Hỗ trợ phân trang, lọc và sắp xếp.
*   **Request (Query Parameters)**:
    *   `page` (integer, optional): Số trang.
    *   `size` (integer, optional): Số lượng bản ghi trên mỗi trang.
    *   `user_public_id` (string, optional): Lọc theo public_id của người dùng mượn sách.
    *   `book_copy_public_id` (string, optional): Lọc theo public_id của bản sao sách được mượn.
    *   `status` (string, optional): Lọc theo trạng thái (ACTIVE, RETURNED, OVERDUE, LOST).
    *   `borrow_date_from` (date, optional): Lọc từ ngày mượn.
    *   `borrow_date_to` (date, optional): Lọc đến ngày mượn.
    *   `due_date_from` (date, optional): Lọc từ ngày đến hạn.
    *   `due_date_to` (date, optional): Lọc đến ngày đến hạn.
    *   `sort_by` (string, optional): Trường để sắp xếp (ví dụ: `borrow_date`, `due_date`).
    *   `order` (string, optional): Thứ tự sắp xếp (`asc` hoặc `desc`).
*   **Response (Status: 200 OK)**:
    ```json
    {
        "data": [
            {
                "public_id": "p1q2r3s4-t5u6-7890-1234-567890abcdef",
                "book_copy_public_id": "m1n2o3p4-q5r6-7890-1234-567890abcdef",
                "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
                "borrow_date": "2023-07-01",
                "due_date": "2023-07-15",
                "return_date": null,
                "status": "ACTIVE",
                "created_at": "2023-07-01T10:00:00Z",
                "updated_at": "2023-07-01T10:00:00Z"
            }
        ],
        "pagination": {
            "total_items": 30,
            "total_pages": 3,
            "current_page": 1,
            "page_size": 10
        }
    }
    ```
*   **Ví dụ**: Lấy danh sách các phiếu mượn đang `ACTIVE` của người dùng có public_id `a1b2c3d4-e5f6-7890-1234-567890abcdef`.
    `GET /api/v1/borrowings?user_public_id=a1b2c3d4-e5f6-7890-1234-567890abcdef&status=ACTIVE`

    ```bash
    curl -X GET "/api/v1/borrowings?user_public_id=a1b2c3d4-e5f6-7890-1234-567890abcdef&status=ACTIVE" \
      -H "Accept: application/json"
    ```

##### 3.3.1.2. Lấy thông tin phiếu mượn theo Public ID

*   **Endpoint**: `GET /api/v1/borrowings/{public_id}`
*   **Mô tả**: Lấy thông tin chi tiết của một phiếu mượn dựa trên `public_id`.
*   **Request**: Không có body.
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "p1q2r3s4-t5u6-7890-1234-567890abcdef",
        "book_copy_public_id": "m1n2o3p4-q5r6-7890-1234-567890abcdef",
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "borrow_date": "2023-07-01",
        "due_date": "2023-07-15",
        "return_date": null,
        "status": "ACTIVE",
        "created_at": "2023-07-01T10:00:00Z",
        "updated_at": "2023-07-01T10:00:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**:
    ```json
    {
        "error": "Borrowing not found",
        "message": "Phiếu mượn với public_id 'invalid-id' không tồn tại."
    }
    ```
*   **Ví dụ**: Lấy thông tin phiếu mượn có public_id `p1q2r3s4-t5u6-7890-1234-567890abcdef`.
    `GET /api/v1/borrowings/p1q2r3s4-t5u6-7890-1234-567890abcdef`

    ```bash
    curl -X GET "/api/v1/borrowings/p1q2r3s4-t5u6-7890-1234-567890abcdef" \
      -H "Accept: application/json"
    ```

##### 3.3.1.3. Tạo phiếu mượn mới

*   **Endpoint**: `POST /api/v1/borrowings`
*   **Mô tả**: Tạo một phiếu mượn mới. Cần kiểm tra trạng thái của bản sao sách và người dùng trước khi tạo.
*   **Request (Body)**:
    ```json
    {
        "book_copy_public_id": "m1n2o3p4-q5r6-7890-1234-567890abcdef",
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "borrow_date": "2023-07-24",
        "due_date": "2023-08-07" 
    }
    ```
*   **Response (Status: 201 Created)**:
    ```json
    {
        "public_id": "q1r2s3t4-u5v6-7890-5432-10fedcba9876",
        "book_copy_public_id": "m1n2o3p4-q5r6-7890-1234-567890abcdef",
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "borrow_date": "2023-07-24",
        "due_date": "2023-08-07",
        "return_date": null,
        "status": "ACTIVE",
        "created_at": "2023-07-24T10:00:00Z",
        "updated_at": "2023-07-24T10:00:00Z"
    }
    ```
*   **Response (Status: 400 Bad Request)**:
    ```json
    {
        "error": "Validation Error",
        "message": "Bản sao sách không khả dụng hoặc người dùng không hợp lệ."
    }
    ```
*   **Ví dụ**: Tạo phiếu mượn mới.
    `POST /api/v1/borrowings` với body JSON như trên.

    ```bash
    curl -X POST "/api/v1/borrowings" \
      -H "Content-Type: application/json" \
      -d "{\"book_copy_public_id\": \"m1n2o3p4-q5r6-7890-1234-567890abcdef\", \"user_public_id\": \"a1b2c3d4-e5f6-7890-1234-567890abcdef\", \"borrow_date\": \"2023-07-24\", \"due_date\": \"2023-08-07\"}"
    ```

##### 3.3.1.4. Cập nhật trạng thái phiếu mượn (Trả sách)

*   **Endpoint**: `PUT /api/v1/borrowings/{public_id}/return`
*   **Mô tả**: Cập nhật trạng thái của phiếu mượn thành `RETURNED` và ghi lại `return_date`. Có thể tính toán và tạo khoản phạt nếu quá hạn.
*   **Request (Body)**:
    ```json
    {
        "return_date": "2023-07-25"
    }
    ```
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "p1q2r3s4-t5u6-7890-1234-567890abcdef",
        "book_copy_public_id": "m1n2o3p4-q5r6-7890-1234-567890abcdef",
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "borrow_date": "2023-07-01",
        "due_date": "2023-07-15",
        "return_date": "2023-07-25",
        "status": "RETURNED",
        "fine_amount": 10.50, 
        "created_at": "2023-07-01T10:00:00Z",
        "updated_at": "2023-07-24T11:00:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Trả sách cho phiếu mượn có public_id `p1q2r3s4-t5u6-7890-1234-567890abcdef`.
    `PUT /api/v1/borrowings/p1q2r3s4-t5u6-7890-1234-567890abcdef/return` với body JSON như trên.

    ```bash
    curl -X PUT "/api/v1/borrowings/p1q2r3s4-t5u6-7890-1234-567890abcdef/return" \
      -H "Content-Type: application/json" \
      -d "{\"return_date\": \"2023-07-25\"}"
    ```

##### 3.3.1.5. Cập nhật trạng thái phiếu mượn (Gia hạn)

*   **Endpoint**: `PUT /api/v1/borrowings/{public_id}/renew`
*   **Mô tả**: Gia hạn phiếu mượn. `due_date` sẽ được cập nhật dựa trên quy định của thư viện.
*   **Request (Body)**:
    ```json
    {
        "new_due_date": "2023-08-21"
    }
    ```
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "p1q2r3s4-t5u6-7890-1234-567890abcdef",
        "book_copy_public_id": "m1n2o3p4-q5r6-7890-1234-567890abcdef",
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "borrow_date": "2023-07-01",
        "due_date": "2023-08-21",
        "return_date": null,
        "status": "ACTIVE",
        "created_at": "2023-07-01T10:00:00Z",
        "updated_at": "2023-07-24T12:00:00Z"
    }
    ```
*   **Response (Status: 400 Bad Request)**:
    ```json
    {
        "error": "Renewal not allowed",
        "message": "Phiếu mượn đã quá hạn hoặc không đủ điều kiện gia hạn."
    }
    ```
*   **Ví dụ**: Gia hạn phiếu mượn có public_id `p1q2r3s4-t5u6-7890-1234-567890abcdef`.
    `PUT /api/v1/borrowings/p1q2r3s4-t5u6-7890-1234-567890abcdef/renew` với body JSON như trên.

    ```bash
    curl -X PUT "/api/v1/borrowings/p1q2r3s4-t5u6-7890-1234-567890abcdef/renew" \
      -H "Content-Type: application/json" \
      -d "{\"new_due_date\": \"2023-08-21\"}"
    ```

##### 3.3.1.6. Xóa phiếu mượn (Soft Delete)

*   **Endpoint**: `DELETE /api/v1/borrowings/{public_id}`
*   **Mô tả**: Đánh dấu phiếu mượn là đã xóa mềm.
*   **Request**: Không có body.
*   **Response (Status: 204 No Content)**: Thành công.
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Xóa mềm phiếu mượn có public_id `p1q2r3s4-t5u6-7890-1234-567890abcdef`.
    `DELETE /api/v1/borrowings/p1q2r3s4-t5u6-7890-1234-567890abcdef`

    ```bash
    curl -X DELETE "/api/v1/borrowings/p1q2r3s4-t5u6-7890-1234-567890abcdef"
    ```

#### 3.3.2. Quản lý phạt (`/fines`)

##### 3.3.2.1. Lấy danh sách phạt

*   **Endpoint**: `GET /api/v1/fines`
*   **Mô tả**: Lấy danh sách tất cả các khoản phạt. Hỗ trợ phân trang, lọc và sắp xếp.
*   **Request (Query Parameters)**:
    *   `page` (integer, optional): Số trang.
    *   `size` (integer, optional): Số lượng bản ghi trên mỗi trang.
    *   `borrowing_public_id` (string, optional): Lọc theo public_id của phiếu mượn liên quan.
    *   `user_public_id` (string, optional): Lọc theo public_id của người dùng bị phạt.
    *   `status` (string, optional): Lọc theo trạng thái (PENDING, PAID, WAIVED).
    *   `sort_by` (string, optional): Trường để sắp xếp (ví dụ: `created_at`, `amount`).
    *   `order` (string, optional): Thứ tự sắp xếp (`asc` hoặc `desc`).
*   **Response (Status: 200 OK)**:
    ```json
    {
        "data": [
            {
                "public_id": "r1s2t3u4-v5w6-7890-1234-567890abcdef",
                "borrowing_public_id": "p1q2r3s4-t5u6-7890-1234-567890abcdef",
                "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
                "amount": 10.50,
                "reason": "Quá hạn 7 ngày",
                "status": "PENDING",
                "paid_at": null,
                "created_at": "2023-07-22T10:00:00Z",
                "updated_at": "2023-07-22T10:00:00Z"
            }
        ],
        "pagination": {
            "total_items": 10,
            "total_pages": 1,
            "current_page": 1,
            "page_size": 10
        }
    }
    ```
*   **Ví dụ**: Lấy danh sách các khoản phạt đang `PENDING` của người dùng có public_id `a1b2c3d4-e5f6-7890-1234-567890abcdef`.
    `GET /api/v1/fines?user_public_id=a1b2c3d4-e5f6-7890-1234-567890abcdef&status=PENDING`

    ```bash
    curl -X GET "/api/v1/fines?user_public_id=a1b2c3d4-e5f6-7890-1234-567890abcdef&status=PENDING" \
      -H "Accept: application/json"
    ```

##### 3.3.2.2. Lấy thông tin phạt theo Public ID

*   **Endpoint**: `GET /api/v1/fines/{public_id}`
*   **Mô tả**: Lấy thông tin chi tiết của một khoản phạt dựa trên `public_id`.
*   **Request**: Không có body.
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "r1s2t3u4-v5w6-7890-1234-567890abcdef",
        "borrowing_public_id": "p1q2r3s4-t5u6-7890-1234-567890abcdef",
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "amount": 10.50,
        "reason": "Quá hạn 7 ngày",
        "status": "PENDING",
        "paid_at": null,
        "created_at": "2023-07-22T10:00:00Z",
        "updated_at": "2023-07-22T10:00:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**: Tương tự như GET borrowings.
*   **Ví dụ**: Lấy thông tin phạt có public_id `r1s2t3u4-v5w6-7890-1234-567890abcdef`.
    `GET /api/v1/fines/r1s2t3u4-v5w6-7890-1234-567890abcdef`

    ```bash
    curl -X GET "/api/v1/fines/r1s2t3u4-v5w6-7890-1234-567890abcdef" \
      -H "Accept: application/json"
    ```

##### 3.3.2.3. Tạo khoản phạt mới (Thường được tạo tự động)

*   **Endpoint**: `POST /api/v1/fines`
*   **Mô tả**: Tạo một khoản phạt mới. Thường được gọi nội bộ khi một phiếu mượn quá hạn hoặc sách bị hư hại.
*   **Request (Body)**:
    ```json
    {
        "borrowing_public_id": "p1q2r3s4-t5u6-7890-1234-567890abcdef",
        "amount": 5.00,
        "reason": "Sách bị hư hại nhẹ",
        "status": "PENDING"
    }
    ```
*   **Response (Status: 201 Created)**:
    ```json
    {
        "public_id": "s1t2u3v4-w5x6-7890-5432-10fedcba9876",
        "borrowing_public_id": "p1q2r3s4-t5u6-7890-1234-567890abcdef",
        "amount": 5.00,
        "reason": "Sách bị hư hại nhẹ",
        "status": "PENDING",
        "paid_at": null,
        "created_at": "2023-07-24T13:00:00Z",
        "updated_at": "2023-07-24T13:00:00Z"
    }
    ```
*   **Response (Status: 400 Bad Request)**:
    ```json
    {
        "error": "Validation Error",
        "message": "Phiếu mượn không hợp lệ hoặc số tiền phạt không hợp lệ."
    }
    ```
*   **Ví dụ**: Tạo khoản phạt mới.
    `POST /api/v1/fines` với body JSON như trên.

    ```bash
    curl -X POST "/api/v1/fines" \
      -H "Content-Type: application/json" \
      -d "{\"borrowing_public_id\": \"p1q2r3s4-t5u6-7890-1234-567890abcdef\", \"amount\": 5.00, \"reason\": \"Sách bị hư hại nhẹ\", \"status\": \"PENDING\"}"
    ```

##### 3.3.2.4. Cập nhật trạng thái phạt (Thanh toán/Miễn)

*   **Endpoint**: `PUT /api/v1/fines/{public_id}/status`
*   **Mô tả**: Cập nhật trạng thái của khoản phạt (ví dụ: từ `PENDING` sang `PAID` hoặc `WAIVED`).
*   **Request (Body)**:
    ```json
    {
        "status": "PAID",
        "paid_at": "2023-07-24T14:00:00Z"
    }
    ```
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "r1s2t3u4-v5w6-7890-1234-567890abcdef",
        "borrowing_public_id": "p1q2r3s4-t5u6-7890-1234-567890abcdef",
        "amount": 10.50,
        "reason": "Quá hạn 7 ngày",
        "status": "PAID",
        "paid_at": "2023-07-24T14:00:00Z",
        "created_at": "2023-07-22T10:00:00Z",
        "updated_at": "2023-07-24T14:00:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Cập nhật trạng thái phạt có public_id `r1s2t3u4-v5w6-7890-1234-567890abcdef` thành `PAID`.
    `PUT /api/v1/fines/r1s2t3u4-v5w6-7890-1234-567890abcdef/status` với body JSON như trên.

    ```bash
    curl -X PUT "/api/v1/fines/r1s2t3u4-v5w6-7890-1234-567890abcdef/status" \
      -H "Content-Type: application/json" \
      -d "{\"status\": \"PAID\", \"paid_at\": \"2023-07-24T14:00:00Z\"}"
    ```

##### 3.3.2.5. Xóa khoản phạt (Soft Delete)

*   **Endpoint**: `DELETE /api/v1/fines/{public_id}`
*   **Mô tả**: Đánh dấu khoản phạt là đã xóa mềm.
*   **Request**: Không có body.
*   **Response (Status: 204 No Content)**: Thành công.
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Xóa mềm khoản phạt có public_id `r1s2t3u4-v5w6-7890-1234-567890abcdef`.
    `DELETE /api/v1/fines/r1s2t3u4-v5w6-7890-1234-567890abcdef`

    ```bash
    curl -X DELETE "/api/v1/fines/r1s2t3u4-v5w6-7890-1234-567890abcdef"
    ```

#### 3.3.3. Quản lý đặt trước (`/reservations`)

##### 3.3.3.1. Lấy danh sách đặt trước

*   **Endpoint**: `GET /api/v1/reservations`
*   **Mô tả**: Lấy danh sách tất cả các yêu cầu đặt trước. Hỗ trợ phân trang, lọc và sắp xếp.
*   **Request (Query Parameters)**:
    *   `page` (integer, optional): Số trang.
    *   `size` (integer, optional): Số lượng bản ghi trên mỗi trang.
    *   `user_public_id` (string, optional): Lọc theo public_id của người dùng đặt sách.
    *   `book_public_id` (string, optional): Lọc theo public_id của đầu sách được đặt.
    *   `status` (string, optional): Lọc theo trạng thái (PENDING, FULFILLED, CANCELLED, EXPIRED).
    *   `reservation_date_from` (date, optional): Lọc từ ngày đặt trước.
    *   `reservation_date_to` (date, optional): Lọc đến ngày đặt trước.
    *   `sort_by` (string, optional): Trường để sắp xếp (ví dụ: `reservation_date`, `expiry_date`).
    *   `order` (string, optional): Thứ tự sắp xếp (`asc` hoặc `desc`).
*   **Response (Status: 200 OK)**:
    ```json
    {
        "data": [
            {
                "public_id": "t1u2v3w4-x5y6-7890-1234-567890abcdef",
                "book_public_id": "k1l2m3n4-o5p6-7890-1234-567890abcdef",
                "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
                "reservation_date": "2023-07-20T10:00:00Z",
                "expiry_date": "2023-07-27T10:00:00Z",
                "status": "PENDING",
                "created_at": "2023-07-20T10:00:00Z",
                "updated_at": "2023-07-20T10:00:00Z"
            }
        ],
        "pagination": {
            "total_items": 15,
            "total_pages": 2,
            "current_page": 1,
            "page_size": 10
        }
    }
    ```
*   **Ví dụ**: Lấy danh sách các đặt trước đang `PENDING` của sách có public_id `k1l2m3n4-o5p6-7890-1234-567890abcdef`.
    `GET /api/v1/reservations?book_public_id=k1l2m3n4-o5p6-7890-1234-567890abcdef&status=PENDING`

    ```bash
    curl -X GET "/api/v1/reservations?book_public_id=k1l2m3n4-o5p6-7890-1234-567890abcdef&status=PENDING" \
      -H "Accept: application/json"
    ```

##### 3.3.3.2. Lấy thông tin đặt trước theo Public ID

*   **Endpoint**: `GET /api/v1/reservations/{public_id}`
*   **Mô tả**: Lấy thông tin chi tiết của một yêu cầu đặt trước dựa trên `public_id`.
*   **Request**: Không có body.
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "t1u2v3w4-x5y6-7890-1234-567890abcdef",
        "book_public_id": "k1l2m3n4-o5p6-7890-1234-567890abcdef",
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "reservation_date": "2023-07-20T10:00:00Z",
        "expiry_date": "2023-07-27T10:00:00Z",
        "status": "PENDING",
        "created_at": "2023-07-20T10:00:00Z",
        "updated_at": "2023-07-20T10:00:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**: Tương tự như GET borrowings.
*   **Ví dụ**: Lấy thông tin đặt trước có public_id `t1u2v3w4-x5y6-7890-1234-567890abcdef`.
    `GET /api/v1/reservations/t1u2v3w4-x5y6-7890-1234-567890abcdef`

    ```bash
    curl -X GET "/api/v1/reservations/t1u2v3w4-x5y6-7890-1234-567890abcdef" \
      -H "Accept: application/json"
    ```

##### 3.3.3.3. Tạo yêu cầu đặt trước mới

*   **Endpoint**: `POST /api/v1/reservations`
*   **Mô tả**: Tạo một yêu cầu đặt trước sách mới. Cần kiểm tra tính khả dụng của sách và các quy định đặt trước.
*   **Request (Body)**:
    ```json
    {
        "book_public_id": "k1l2m3n4-o5p6-7890-1234-567890abcdef",
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "expiry_date": "2023-08-01T10:00:00Z"
    }
    ```
*   **Response (Status: 201 Created)**:
    ```json
    {
        "public_id": "u1v2w3x4-y5z6-7890-5432-10fedcba9876",
        "book_public_id": "k1l2m3n4-o5p6-7890-1234-567890abcdef",
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "reservation_date": "2023-07-24T15:00:00Z",
        "expiry_date": "2023-08-01T10:00:00Z",
        "status": "PENDING",
        "created_at": "2023-07-24T15:00:00Z",
        "updated_at": "2023-07-24T15:00:00Z"
    }
    ```
*   **Response (Status: 400 Bad Request)**:
    ```json
    {
        "error": "Validation Error",
        "message": "Sách không khả dụng để đặt trước hoặc người dùng đã có đặt trước cho sách này."
    }
    ```
*   **Ví dụ**: Tạo yêu cầu đặt trước mới.
    `POST /api/v1/reservations` với body JSON như trên.

    ```bash
    curl -X POST "/api/v1/reservations" \
      -H "Content-Type: application/json" \
      -d "{\"book_public_id\": \"k1l2m3n4-o5p6-7890-1234-567890abcdef\", \"user_public_id\": \"a1b2c3d4-e5f6-7890-1234-567890abcdef\", \"expiry_date\": \"2023-08-01T10:00:00Z\"}"
    ```

##### 3.3.3.4. Cập nhật trạng thái đặt trước (Hủy/Hoàn thành)

*   **Endpoint**: `PUT /api/v1/reservations/{public_id}/status`
*   **Mô tả**: Cập nhật trạng thái của yêu cầu đặt trước (ví dụ: từ `PENDING` sang `CANCELLED` hoặc `FULFILLED`).
*   **Request (Body)**:
    ```json
    {
        "status": "CANCELLED"
    }
    ```
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "t1u2v3w4-x5y6-7890-1234-567890abcdef",
        "book_public_id": "k1l2m3n4-o5p6-7890-1234-567890abcdef",
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "reservation_date": "2023-07-20T10:00:00Z",
        "expiry_date": "2023-07-27T10:00:00Z",
        "status": "CANCELLED",
        "created_at": "2023-07-20T10:00:00Z",
        "updated_at": "2023-07-24T16:00:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Hủy đặt trước có public_id `t1u2v3w4-x5y6-7890-1234-567890abcdef`.
    `PUT /api/v1/reservations/t1u2v3w4-x5y6-7890-1234-567890abcdef/status` với body JSON như trên.

    ```bash
    curl -X PUT "/api/v1/reservations/t1u2v3w4-x5y6-7890-1234-567890abcdef/status" \
      -H "Content-Type: application/json" \
      -d "{\"status\": \"CANCELLED\"}"
    ```

##### 3.3.3.5. Xóa yêu cầu đặt trước (Soft Delete)

*   **Endpoint**: `DELETE /api/v1/reservations/{public_id}`
*   **Mô tả**: Đánh dấu yêu cầu đặt trước là đã xóa mềm.
*   **Request**: Không có body.
*   **Response (Status: 204 No Content)**: Thành công.
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Xóa mềm yêu cầu đặt trước có public_id `t1u2v3w4-x5y6-7890-1234-567890abcdef`.
    `DELETE /api/v1/reservations/t1u2v3w4-x5y6-7890-1234-567890abcdef`

    ```bash
    curl -X DELETE "/api/v1/reservations/t1u2v3w4-x5y6-7890-1234-567890abcdef"
    ```

#### 3.3.4. Quản lý giỏ sách (`/book-bags`)

##### 3.3.4.1. Lấy thông tin giỏ sách của người dùng

*   **Endpoint**: `GET /api/v1/book-bags/{user_public_id}`
*   **Mô tả**: Lấy thông tin giỏ sách của một người dùng cụ thể, bao gồm các sách trong giỏ.
*   **Request**: Không có body.
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "v1w2x3y4-z5a6-7890-1234-567890abcdef",
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "created_at": "2023-07-24T09:00:00Z",
        "updated_at": "2023-07-24T09:00:00Z",
        "items": [
            {
                "book_public_id": "k1l2m3n4-o5p6-7890-1234-567890abcdef",
                "quantity": 1,
                "added_at": "2023-07-24T09:15:00Z"
            }
        ]
    }
    ```
*   **Response (Status: 404 Not Found)**:
    ```json
    {
        "error": "Book bag not found",
        "message": "Giỏ sách của người dùng với public_id 'invalid-id' không tồn tại."
    }
    ```
*   **Ví dụ**: Lấy thông tin giỏ sách của người dùng có public_id `a1b2c3d4-e5f6-7890-1234-567890abcdef`.
    `GET /api/v1/book-bags/a1b2c3d4-e5f6-7890-1234-567890abcdef`

    ```bash
    curl -X GET "/api/v1/book-bags/a1b2c3d4-e5f6-7890-1234-567890abcdef" \
      -H "Accept: application/json"
    ```

##### 3.3.4.2. Thêm sách vào giỏ sách

*   **Endpoint**: `POST /api/v1/book-bags/{user_public_id}/items`
*   **Mô tả**: Thêm một cuốn sách vào giỏ sách của người dùng. Nếu sách đã có trong giỏ, tăng số lượng.
*   **Request (Body)**:
    ```json
    {
        "book_public_id": "l1m2n3o4-p5q6-7890-5432-10fedcba9876",
        "quantity": 1
    }
    ```
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "v1w2x3y4-z5a6-7890-1234-567890abcdef",
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "created_at": "2023-07-24T09:00:00Z",
        "updated_at": "2023-07-24T17:00:00Z",
        "items": [
            {
                "book_public_id": "k1l2m3n4-o5p6-7890-1234-567890abcdef",
                "quantity": 1,
                "added_at": "2023-07-24T09:15:00Z"
            },
            {
                "book_public_id": "l1m2n3o4-p5q6-7890-5432-10fedcba9876",
                "quantity": 1,
                "added_at": "2023-07-24T17:00:00Z"
            }
        ]
    }
    ```
*   **Response (Status: 400 Bad Request)**:
    ```json
    {
        "error": "Validation Error",
        "message": "Sách không tồn tại hoặc số lượng không hợp lệ."
    }
    ```
*   **Ví dụ**: Thêm sách vào giỏ sách của người dùng có public_id `a1b2c3d4-e5f6-7890-1234-567890abcdef`.
    `POST /api/v1/book-bags/a1b2c3d4-e5f6-7890-1234-567890abcdef/items` với body JSON như trên.

    ```bash
    curl -X POST "/api/v1/book-bags/a1b2c3d4-e5f6-7890-1234-567890abcdef/items" \
      -H "Content-Type: application/json" \
      -d "{\"book_public_id\": \"l1m2n3o4-p5q6-7890-5432-10fedcba9876\", \"quantity\": 1}"
    ```

##### 3.3.4.3. Cập nhật số lượng sách trong giỏ sách

*   **Endpoint**: `PUT /api/v1/book-bags/{user_public_id}/items/{book_public_id}`
*   **Mô tả**: Cập nhật số lượng của một cuốn sách trong giỏ sách của người dùng.
*   **Request (Body)**:
    ```json
    {
        "quantity": 2
    }
    ```
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "v1w2x3y4-z5a6-7890-1234-567890abcdef",
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "created_at": "2023-07-24T09:00:00Z",
        "updated_at": "2023-07-24T18:00:00Z",
        "items": [
            {
                "book_public_id": "k1l2m3n4-o5p6-7890-1234-567890abcdef",
                "quantity": 2,
                "added_at": "2023-07-24T09:15:00Z"
            },
            {
                "book_public_id": "l1m2n3o4-p5q6-7890-5432-10fedcba9876",
                "quantity": 1,
                "added_at": "2023-07-24T17:00:00Z"
            }
        ]
    }
    ```
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Cập nhật số lượng sách có public_id `k1l2m3n4-o5p6-7890-1234-567890abcdef` trong giỏ sách của người dùng `a1b2c3d4-e5f6-7890-1234-567890abcdef` lên 2.
    `PUT /api/v1/book-bags/a1b2c3d4-e5f6-7890-1234-567890abcdef/items/k1l2m3n4-o5p6-7890-1234-567890abcdef` với body JSON như trên.

    ```bash
    curl -X PUT "/api/v1/book-bags/a1b2c3d4-e5f6-7890-1234-567890abcdef/items/k1l2m3n4-o5p6-7890-1234-567890abcdef" \
      -H "Content-Type: application/json" \
      -d "{\"quantity\": 2}"
    ```

##### 3.3.4.4. Xóa sách khỏi giỏ sách

*   **Endpoint**: `DELETE /api/v1/book-bags/{user_public_id}/items/{book_public_id}`
*   **Mô tả**: Xóa một cuốn sách khỏi giỏ sách của người dùng.
*   **Request**: Không có body.
*   **Response (Status: 204 No Content)**: Thành công.
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Xóa sách có public_id `l1m2n3o4-p5q6-7890-5432-10fedcba9876` khỏi giỏ sách của người dùng `a1b2c3d4-e5f6-7890-1234-567890abcdef`.
    `DELETE /api/v1/book-bags/a1b2c3d4-e5f6-7890-1234-567890abcdef/items/l1m2n3o4-p5q6-7890-5432-10fedcba9876`

    ```bash
    curl -X DELETE "/api/v1/book-bags/a1b2c3d4-e5f6-7890-1234-567890abcdef/items/l1m2n3o4-p5q6-7890-5432-10fedcba9876"
    ```

#### 3.3.5. Quản lý quy định thư viện (`/library-policies`)

##### 3.3.5.1. Lấy danh sách quy định

*   **Endpoint**: `GET /api/v1/library-policies`
*   **Mô tả**: Lấy danh sách tất cả các quy định của thư viện. Hỗ trợ phân trang, lọc và sắp xếp.
*   **Request (Query Parameters)**:
    *   `page` (integer, optional): Số trang.
    *   `size` (integer, optional): Số lượng bản ghi trên mỗi trang.
    *   `policy_name` (string, optional): Lọc theo tên quy định.
    *   `sort_by` (string, optional): Trường để sắp xếp (ví dụ: `policy_name`, `created_at`).
    *   `order` (string, optional): Thứ tự sắp xếp (`asc` hoặc `desc`).
*   **Response (Status: 200 OK)**:
    ```json
    {
        "data": [
            {
                "policy_name": "MAX_BORROW_DAYS",
                "policy_value": "14",
                "description": "Số ngày tối đa cho phép mượn một cuốn sách.",
                "created_at": "2023-01-01T10:00:00Z",
                "updated_at": "2023-01-01T10:00:00Z"
            }
        ],
        "pagination": {
            "total_items": 5,
            "total_pages": 1,
            "current_page": 1,
            "page_size": 10
        }
    }
    ```
*   **Ví dụ**: Lấy quy định về số ngày mượn tối đa.
    `GET /api/v1/library-policies?policy_name=MAX_BORROW_DAYS`

    ```bash
    curl -X GET "/api/v1/library-policies?policy_name=MAX_BORROW_DAYS" \
      -H "Accept: application/json"
    ```

##### 3.3.5.2. Lấy thông tin quy định theo tên

*   **Endpoint**: `GET /api/v1/library-policies/{policy_name}`
*   **Mô tả**: Lấy thông tin chi tiết của một quy định dựa trên tên quy định.
*   **Request**: Không có body.
*   **Response (Status: 200 OK)**:
    ```json
    {
        "policy_name": "MAX_BORROW_DAYS",
        "policy_value": "14",
        "description": "Số ngày tối đa cho phép mượn một cuốn sách.",
        "created_at": "2023-01-01T10:00:00Z",
        "updated_at": "2023-01-01T10:00:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**:
    ```json
    {
        "error": "Policy not found",
        "message": "Quy định với tên 'invalid-policy' không tồn tại."
    }
    ```
*   **Ví dụ**: Lấy thông tin quy định `OVERDUE_FINE_PER_DAY`.
    `GET /api/v1/library-policies/OVERDUE_FINE_PER_DAY`

    ```bash
    curl -X GET "/api/v1/library-policies/OVERDUE_FINE_PER_DAY" \
      -H "Accept: application/json"
    ```

##### 3.3.5.3. Tạo quy định mới

*   **Endpoint**: `POST /api/v1/library-policies`
*   **Mô tả**: Tạo một quy định mới của thư viện.
*   **Request (Body)**:
    ```json
    {
        "policy_name": "MAX_RENEWALS",
        "policy_value": "2",
        "description": "Số lần tối đa cho phép gia hạn một cuốn sách."
    }
    ```
*   **Response (Status: 201 Created)**:
    ```json
    {
        "policy_name": "MAX_RENEWALS",
        "policy_value": "2",
        "description": "Số lần tối đa cho phép gia hạn một cuốn sách.",
        "created_at": "2023-07-24T19:00:00Z",
        "updated_at": "2023-07-24T19:00:00Z"
    }
    ```
*   **Response (Status: 400 Bad Request)**:
    ```json
    {
        "error": "Validation Error",
        "message": "Tên quy định đã tồn tại hoặc dữ liệu không hợp lệ."
    }
    ```
*   **Ví dụ**: Tạo quy định mới.
    `POST /api/v1/library-policies` với body JSON như trên.

    ```bash
    curl -X POST "/api/v1/library-policies" \
      -H "Content-Type: application/json" \
      -d "{\"policy_name\": \"MAX_RENEWALS\", \"policy_value\": \"2\", \"description\": \"Số lần tối đa cho phép gia hạn một cuốn sách.\"}"
    ```

##### 3.3.5.4. Cập nhật quy định

*   **Endpoint**: `PUT /api/v1/library-policies/{policy_name}`
*   **Mô tả**: Cập nhật giá trị hoặc mô tả của một quy định.
*   **Request (Body)**:
    ```json
    {
        "policy_value": "3",
        "description": "Số lần tối đa cho phép gia hạn một cuốn sách (cập nhật)."
    }
    ```
*   **Response (Status: 200 OK)**:
    ```json
    {
        "policy_name": "MAX_RENEWALS",
        "policy_value": "3",
        "description": "Số lần tối đa cho phép gia hạn một cuốn sách (cập nhật).",
        "created_at": "2023-07-24T19:00:00Z",
        "updated_at": "2023-07-24T19:30:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Cập nhật quy định `MAX_RENEWALS`.
    `PUT /api/v1/library-policies/MAX_RENEWALS` với body JSON như trên.

    ```bash
    curl -X PUT "/api/v1/library-policies/MAX_RENEWALS" \
      -H "Content-Type: application/json" \
      -d "{\"policy_value\": \"3\", \"description\": \"Số lần tối đa cho phép gia hạn một cuốn sách (cập nhật).\"}"
    ```

##### 3.3.5.5. Xóa quy định

*   **Endpoint**: `DELETE /api/v1/library-policies/{policy_name}`
*   **Mô tả**: Xóa một quy định khỏi hệ thống.
*   **Request**: Không có body.
*   **Response (Status: 204 No Content)**: Thành công.
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Xóa quy định `MAX_RENEWALS`.
    `DELETE /api/v1/library-policies/MAX_RENEWALS`

    ```bash
    curl -X DELETE "/api/v1/library-policies/MAX_RENEWALS"
    ```






### 3.4. Notification Service API

**Mô tả**: Dịch vụ này quản lý việc gửi thông báo và tùy chọn nhận thông báo của người dùng. Nó sẽ tương tác với `member-service` thông qua `public_id`.

**Base URL**: `/api/v1/`

#### 3.4.1. Quản lý thông báo (`/notifications`)

##### 3.4.1.1. Lấy danh sách thông báo

*   **Endpoint**: `GET /api/v1/notifications`
*   **Mô tả**: Lấy danh sách tất cả các thông báo đã gửi. Hỗ trợ phân trang, lọc và sắp xếp.
*   **Request (Query Parameters)**:
    *   `page` (integer, optional): Số trang.
    *   `size` (integer, optional): Số lượng bản ghi trên mỗi trang.
    *   `user_public_id` (string, optional): Lọc theo public_id của người dùng nhận thông báo.
    *   `type` (string, optional): Lọc theo loại thông báo (EMAIL, SMS, PUSH).
    *   `status` (string, optional): Lọc theo trạng thái (SENT, DELIVERED, READ, FAILED).
    *   `sent_at_from` (datetime, optional): Lọc từ thời gian gửi.
    *   `sent_at_to` (datetime, optional): Lọc đến thời gian gửi.
    *   `sort_by` (string, optional): Trường để sắp xếp (ví dụ: `sent_at`, `created_at`).
    *   `order` (string, optional): Thứ tự sắp xếp (`asc` hoặc `desc`).
*   **Response (Status: 200 OK)**:
    ```json
    {
        "data": [
            {
                "public_id": "w1x2y3z4-a5b6-7890-1234-567890abcdef",
                "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
                "title": "Nhắc nhở trả sách",
                "content": "Cuốn sách 'Mắt biếc' của bạn sắp đến hạn trả vào ngày 2023-07-25.",
                "type": "EMAIL",
                "status": "SENT",
                "sent_at": "2023-07-24T09:00:00Z",
                "read_at": null,
                "created_at": "2023-07-24T09:00:00Z"
            }
        ],
        "pagination": {
            "total_items": 50,
            "total_pages": 5,
            "current_page": 1,
            "page_size": 10
        }
    }
    ```
*   **Ví dụ**: Lấy danh sách thông báo chưa đọc của người dùng có public_id `a1b2c3d4-e5f6-7890-1234-567890abcdef`.
    `GET /api/v1/notifications?user_public_id=a1b2c3d4-e5f6-7890-1234-567890abcdef&status=SENT`

    ```bash
    curl -X GET "/api/v1/notifications?user_public_id=a1b2c3d4-e5f6-7890-1234-567890abcdef&status=SENT" \
      -H "Accept: application/json"
    ```

##### 3.4.1.2. Lấy thông tin thông báo theo Public ID

*   **Endpoint**: `GET /api/v1/notifications/{public_id}`
*   **Mô tả**: Lấy thông tin chi tiết của một thông báo dựa trên `public_id`.
*   **Request**: Không có body.
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "w1x2y3z4-a5b6-7890-1234-567890abcdef",
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "title": "Nhắc nhở trả sách",
        "content": "Cuốn sách 'Mắt biếc' của bạn sắp đến hạn trả vào ngày 2023-07-25.",
        "type": "EMAIL",
        "status": "SENT",
        "sent_at": "2023-07-24T09:00:00Z",
        "read_at": null,
        "created_at": "2023-07-24T09:00:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**:
    ```json
    {
        "error": "Notification not found",
        "message": "Thông báo với public_id 'invalid-id' không tồn tại."
    }
    ```
*   **Ví dụ**: Lấy thông tin thông báo có public_id `w1x2y3z4-a5b6-7890-1234-567890abcdef`.
    `GET /api/v1/notifications/w1x2y3z4-a5b6-7890-1234-567890abcdef`

    ```bash
    curl -X GET "/api/v1/notifications/w1x2y3z4-a5b6-7890-1234-567890abcdef" \
      -H "Accept: application/json"
    ```

##### 3.4.1.3. Tạo thông báo mới (Thường được tạo nội bộ)

*   **Endpoint**: `POST /api/v1/notifications`
*   **Mô tả**: Tạo một thông báo mới. Thường được gọi nội bộ bởi các dịch vụ khác hoặc hệ thống lập lịch.
*   **Request (Body)**:
    ```json
    {
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "title": "Sách quá hạn",
        "content": "Cuốn sách 'Dế Mèn phiêu lưu ký' của bạn đã quá hạn trả. Vui lòng trả sách sớm để tránh phát sinh phí phạt.",
        "type": "PUSH",
        "status": "SENT",
        "sent_at": "2023-07-24T10:00:00Z"
    }
    ```
*   **Response (Status: 201 Created)**:
    ```json
    {
        "public_id": "x1y2z3a4-b5c6-7890-5432-10fedcba9876",
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "title": "Sách quá hạn",
        "content": "Cuốn sách 'Dế Mèn phiêu lưu ký' của bạn đã quá hạn trả. Vui lòng trả sách sớm để tránh phát sinh phí phạt.",
        "type": "PUSH",
        "status": "SENT",
        "sent_at": "2023-07-24T10:00:00Z",
        "read_at": null,
        "created_at": "2023-07-24T10:00:00Z"
    }
    ```
*   **Response (Status: 400 Bad Request)**:
    ```json
    {
        "error": "Validation Error",
        "message": "Dữ liệu thông báo không hợp lệ."
    }
    ```
*   **Ví dụ**: Tạo thông báo mới.
    `POST /api/v1/notifications` với body JSON như trên.

    ```bash
    curl -X POST "/api/v1/notifications" \
      -H "Content-Type: application/json" \
      -d "{\"user_public_id\": \"a1b2c3d4-e5f6-7890-1234-567890abcdef\", \"title\": \"Sách quá hạn\", \"content\": \"Cuốn sách 'Dế Mèn phiêu lưu ký' của bạn đã quá hạn trả. Vui lòng trả sách sớm để tránh phát sinh phí phạt.\", \"type\": \"PUSH\", \"status\": \"SENT\", \"sent_at\": \"2023-07-24T10:00:00Z\"}"
    ```

##### 3.4.1.4. Cập nhật trạng thái thông báo (Đánh dấu đã đọc)

*   **Endpoint**: `PUT /api/v1/notifications/{public_id}/read`
*   **Mô tả**: Đánh dấu một thông báo là đã đọc.
*   **Request (Body)**:
    ```json
    {
        "read_at": "2023-07-24T11:00:00Z"
    }
    ```
*   **Response (Status: 200 OK)**:
    ```json
    {
        "public_id": "w1x2y3z4-a5b6-7890-1234-567890abcdef",
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "title": "Nhắc nhở trả sách",
        "content": "Cuốn sách 'Mắt biếc' của bạn sắp đến hạn trả vào ngày 2023-07-25.",
        "type": "EMAIL",
        "status": "READ",
        "sent_at": "2023-07-24T09:00:00Z",
        "read_at": "2023-07-24T11:00:00Z",
        "created_at": "2023-07-24T09:00:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Đánh dấu thông báo có public_id `w1x2y3z4-a5b6-7890-1234-567890abcdef` là đã đọc.
    `PUT /api/v1/notifications/w1x2y3z4-a5b6-7890-1234-567890abcdef/read` với body JSON như trên.

    ```bash
    curl -X PUT "/api/v1/notifications/w1x2y3z4-a5b6-7890-1234-567890abcdef/read" \
      -H "Content-Type: application/json" \
      -d "{\"read_at\": \"2023-07-24T11:00:00Z\"}"
    ```

##### 3.4.1.5. Xóa thông báo (Soft Delete)

*   **Endpoint**: `DELETE /api/v1/notifications/{public_id}`
*   **Mô tả**: Đánh dấu thông báo là đã xóa mềm.
*   **Request**: Không có body.
*   **Response (Status: 204 No Content)**: Thành công.
*   **Response (Status: 404 Not Found)**: Tương tự như GET.
*   **Ví dụ**: Xóa mềm thông báo có public_id `w1x2y3z4-a5b6-7890-1234-567890abcdef`.
    `DELETE /api/v1/notifications/w1x2y3z4-a5b6-7890-1234-567890abcdef`

    ```bash
    curl -X DELETE "/api/v1/notifications/w1x2y3z4-a5b6-7890-1234-567890abcdef"
    ```

#### 3.4.2. Quản lý tùy chọn thông báo (`/notification-preferences`)

##### 3.4.2.1. Lấy tùy chọn thông báo của người dùng

*   **Endpoint**: `GET /api/v1/notification-preferences/{user_public_id}`
*   **Mô tả**: Lấy tùy chọn nhận thông báo của một người dùng cụ thể.
*   **Request**: Không có body.
*   **Response (Status: 200 OK)**:
    ```json
    {
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "email_enabled": true,
        "sms_enabled": false,
        "push_enabled": true,
        "borrow_notification": true,
        "return_reminder": true,
        "overdue_notification": true,
        "reservation_notification": true,
        "created_at": "2023-01-01T10:00:00Z",
        "updated_at": "2023-01-01T10:00:00Z"
    }
    ```
*   **Response (Status: 404 Not Found)**:
    ```json
    {
        "error": "Notification preferences not found",
        "message": "Tùy chọn thông báo của người dùng với public_id 'invalid-id' không tồn tại."
    }
    ```
*   **Ví dụ**: Lấy tùy chọn thông báo của người dùng có public_id `a1b2c3d4-e5f6-7890-1234-567890abcdef`.
    `GET /api/v1/notification-preferences/a1b2c3d4-e5f6-7890-1234-567890abcdef`

    ```bash
    curl -X GET "/api/v1/notification-preferences/a1b2c3d4-e5f6-7890-1234-567890abcdef" \
      -H "Accept: application/json"
    ```

##### 3.4.2.2. Cập nhật tùy chọn thông báo của người dùng

*   **Endpoint**: `PUT /api/v1/notification-preferences/{user_public_id}`
*   **Mô tả**: Cập nhật tùy chọn nhận thông báo của một người dùng. Nếu chưa có, sẽ tạo mới.
*   **Request (Body)**:
    ```json
    {
        "email_enabled": false,
        "sms_enabled": true,
        "return_reminder": false
    }
    ```
*   **Response (Status: 200 OK)**:
    ```json
    {
        "user_public_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "email_enabled": false,
        "sms_enabled": true,
        "push_enabled": true,
        "borrow_notification": true,
        "return_reminder": false,
        "overdue_notification": true,
        "reservation_notification": true,
        "created_at": "2023-01-01T10:00:00Z",
        "updated_at": "2023-07-24T12:00:00Z"
    }
    ```
*   **Response (Status: 400 Bad Request)**:
    ```json
    {
        "error": "Validation Error",
        "message": "Dữ liệu tùy chọn thông báo không hợp lệ."
    }
    ```
*   **Ví dụ**: Cập nhật tùy chọn thông báo của người dùng có public_id `a1b2c3d4-e5f6-7890-1234-567890abcdef`.
    `PUT /api/v1/notification-preferences/a1b2c3d4-e5f6-7890-1234-567890abcdef` với body JSON như trên.

    ```bash
    curl -X PUT "/api/v1/notification-preferences/a1b2c3d4-e5f6-7890-1234-567890abcdef" \
      -H "Content-Type: application/json" \
      -d "{\"email_enabled\": false, \"sms_enabled\": true, \"return_reminder\": false}"
    ```






## 4. Kết luận

Thiết kế API này cung cấp một cái nhìn toàn diện về cách các microservice trong hệ thống quản lý thư viện sẽ tương tác với nhau và với các ứng dụng client. Bằng cách tuân thủ các nguyên tắc RESTful, sử dụng `public_id` cho các tham chiếu liên dịch vụ và cung cấp các endpoint rõ ràng, hệ thống sẽ đạt được tính linh hoạt, khả năng mở rộng và dễ bảo trì. Các ví dụ cụ thể cho từng API giúp các nhà phát triển dễ dàng hiểu và triển khai.



