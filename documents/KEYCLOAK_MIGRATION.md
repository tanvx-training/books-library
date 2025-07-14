# Hướng dẫn chuyển đổi User Service sang sử dụng Keycloak

Tài liệu này mô tả quá trình chuyển đổi User Service từ hệ thống xác thực tự quản lý sang sử dụng Keycloak.

## 1. Tổng quan

### 1.1 Mục tiêu chuyển đổi

- Ủy thác việc xác thực và phân quyền cho Keycloak
- Giữ lại dữ liệu người dùng trong database hiện tại cho các mục đích nghiệp vụ
- Đảm bảo tích hợp liền mạch với các service khác trong hệ thống

### 1.2 Thay đổi chính

- Thêm trường `keycloak_id` vào bảng `users` để liên kết với Keycloak
- Chuyển đổi cơ chế xác thực từ JWT tự quản lý sang OAuth2/OIDC với Keycloak
- Triển khai cơ chế đồng bộ người dùng JIT (Just-In-Time Provisioning)

## 2. Thay đổi cơ sở dữ liệu

### 2.1 Thêm trường keycloak_id

```sql
-- Thêm trường keycloak_id vào bảng users
ALTER TABLE users ADD COLUMN keycloak_id VARCHAR(36) NULL UNIQUE;

-- Thay đổi kiểu dữ liệu của các trường audit để lưu keycloak_id
ALTER TABLE users ALTER COLUMN created_by TYPE VARCHAR(36);
ALTER TABLE users ALTER COLUMN updated_by TYPE VARCHAR(36);

ALTER TABLE library_cards ALTER COLUMN created_by TYPE VARCHAR(36);
ALTER TABLE library_cards ALTER COLUMN updated_by TYPE VARCHAR(36);

-- Tạo index cho keycloak_id
CREATE INDEX idx_users_keycloak_id ON users(keycloak_id);
```

### 2.2 Kế hoạch cho trường password

Trong giai đoạn chuyển tiếp, chúng ta vẫn giữ lại trường `password` để hỗ trợ cả hai cơ chế xác thực. Sau khi hoàn tất chuyển đổi, có thể xóa trường này:

```sql
-- Chạy sau khi hoàn tất chuyển đổi
ALTER TABLE users DROP COLUMN password;
```

## 3. Thay đổi mã nguồn

### 3.1 Thêm dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

### 3.2 Cấu hình application.yaml

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${KEYCLOAK_URL:http://localhost:8080}/auth/realms/${KEYCLOAK_REALM:library}
          jwk-set-uri: ${KEYCLOAK_URL:http://localhost:8080}/auth/realms/${KEYCLOAK_REALM:library}/protocol/openid-connect/certs

app:
  keycloak:
    realm: ${KEYCLOAK_REALM:library}
    auth-server-url: ${KEYCLOAK_URL:http://localhost:8080}/auth
    client-id: ${KEYCLOAK_CLIENT_ID:user-service}
    client-secret: ${KEYCLOAK_CLIENT_SECRET:your-client-secret}
```

### 3.3 Cấu hình bảo mật

Tạo lớp `KeycloakSecurityConfig` để cấu hình Spring Security với OAuth2 Resource Server:

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("keycloak")
public class KeycloakSecurityConfig {
    // ... (xem mã nguồn đầy đủ trong file)
}
```

### 3.4 Đồng bộ người dùng

Tạo lớp `UserSyncService` để đồng bộ dữ liệu người dùng giữa Keycloak và database:

```java
@Service
public class UserSyncService {
    // ... (xem mã nguồn đầy đủ trong file)
}
```

### 3.5 Endpoint "Me"

Tạo endpoint `/api/users/me` để trả về thông tin người dùng hiện tại và kích hoạt đồng bộ:

```java
@RestController
@RequestMapping("/api/users")
public class UserMeController {
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getCurrentUser() {
        // ... (xem mã nguồn đầy đủ trong file)
    }
}
```

## 4. Kế hoạch chuyển đổi

### 4.1 Giai đoạn 1: Chuẩn bị

- Triển khai Keycloak
- Tạo realm và client trong Keycloak
- Định nghĩa các vai trò (ADMIN, LIBRARIAN, USER)

### 4.2 Giai đoạn 2: Chuyển đổi dữ liệu

- Chạy script migration để thêm trường `keycloak_id`
- Tạo người dùng trong Keycloak (có thể sử dụng Keycloak Admin API)
- Cập nhật `keycloak_id` cho người dùng hiện có trong database

### 4.3 Giai đoạn 3: Chuyển đổi mã nguồn

- Triển khai các thay đổi mã nguồn
- Chạy song song cả hai cơ chế xác thực trong một thời gian
- Chuyển đổi hoàn toàn sang Keycloak

### 4.4 Giai đoạn 4: Hoàn tất

- Xóa mã nguồn liên quan đến xác thực cũ
- Xóa trường `password` trong database

## 5. Hướng dẫn kiểm thử

### 5.1 Kiểm tra xác thực

Sử dụng Postman hoặc curl để kiểm tra luồng xác thực:

1. Lấy token từ Keycloak:

```bash
curl -X POST \
  http://localhost:8080/auth/realms/library/protocol/openid-connect/token \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=password&client_id=user-service&username=test@example.com&password=password'
```

2. Gọi API với token:

```bash
curl -X GET \
  http://localhost:8082/api/users/me \
  -H 'Authorization: Bearer <token>'
```

### 5.2 Kiểm tra đồng bộ người dùng

1. Tạo người dùng mới trong Keycloak
2. Đăng nhập và gọi API `/api/users/me`
3. Kiểm tra database để xác nhận người dùng đã được tạo

## 6. Lưu ý quan trọng

- Đảm bảo duy trì tính nhất quán giữa Keycloak và database
- Xử lý các trường hợp ngoại lệ (ví dụ: người dùng bị xóa trong Keycloak)
- Cân nhắc sử dụng Keycloak Event Listener để đồng bộ các thay đổi từ Keycloak 