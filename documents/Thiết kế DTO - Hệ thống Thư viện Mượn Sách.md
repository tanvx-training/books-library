# Thiết kế DTO - Hệ thống Thư viện Mượn Sách

Tài liệu này tổng hợp thiết kế chi tiết các Data Transfer Objects (DTOs) được sử dụng để trả về dữ liệu cho các endpoint API của các microservice trong hệ thống: User Service, Book Service, và Notification Service.

---

# Thiết kế DTO - User Service

Tài liệu này mô tả chi tiết các Data Transfer Objects (DTOs) được sử dụng để trả về dữ liệu cho các endpoint API của User Service.

---

## DTOs Chung

### `ApiResponse<T>`

DTO chung cho tất cả các phản hồi API, bao gồm trạng thái, thông điệp và dữ liệu trả về.

```java
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    // Getters and Setters
}
```

## DTOs cho Authentication API (`/api/auth/*`)

### `RegisterRequest`

DTO cho request đăng ký người dùng mới.

```java
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber; // Optional
    // Getters and Setters
}
```

### `LoginRequest`

DTO cho request đăng nhập.

```java
public class LoginRequest {
    private String username;
    private String password;
    // Getters and Setters
}
```

### `AuthResponse`

DTO trả về sau khi đăng nhập thành công, chứa JWT token.

```java
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private List<String> roles;
    // Getters and Setters
}
```

### `RefreshTokenRequest`

DTO cho request làm mới token.

```java
public class RefreshTokenRequest {
    private String refreshToken;
    // Getters and Setters
}
```

## DTOs cho User Management API (`/api/users/*`)

### `RoleDTO`

DTO đại diện cho thông tin vai trò.

```java
public class RoleDTO {
    private Integer id;
    private String name; // e.g., READER, LIBRARIAN, ADMIN
    // Getters and Setters
}
```

### `UserSummaryDTO`

DTO tóm tắt thông tin người dùng, thường dùng trong danh sách.

```java
public class UserSummaryDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean isActive;
    // Getters and Setters
}
```

### `UserDetailDTO`

DTO chi tiết thông tin người dùng, bao gồm cả vai trò và thẻ thư viện (nếu có).

```java
public class UserDetailDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RoleDTO> roles;
    private LibraryCardDTO libraryCard; // Null if user is not a READER or has no card
    // Getters and Setters
}
```

### `UserUpdateDTO`

DTO cho request cập nhật thông tin người dùng.

```java
public class UserUpdateDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email; // Allow email update?
    private Boolean isActive; // For Admin/Librarian to activate/deactivate
    private List<Integer> roleIds; // For Admin to change roles
    // Getters and Setters
}
```

## DTOs cho Library Card API (`/api/cards/*`)

### `LibraryCardDTO`

DTO đại diện cho thông tin thẻ thư viện.

```java
public class LibraryCardDTO {
    private Long id;
    private String cardNumber;
    private Long userId;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String status; // ACTIVE, EXPIRED, LOST
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Getters and Setters
}
```

### `CreateLibraryCardRequest`

DTO cho request tạo thẻ thư viện mới.

```java
public class CreateLibraryCardRequest {
    private Long userId;
    private String cardNumber; // Or auto-generate?
    private LocalDate issueDate; // Or default to now?
    private LocalDate expiryDate;
    // Getters and Setters
}
```

### `UpdateLibraryCardRequest`

DTO cho request cập nhật thông tin thẻ (ví dụ: báo mất).

```java
public class UpdateLibraryCardRequest {
    private String status; // e.g., LOST
    // Getters and Setters
}
```

### `RenewLibraryCardRequest`

DTO cho request gia hạn thẻ.

```java
public class RenewLibraryCardRequest {
    private LocalDate newExpiryDate;
    // Getters and Setters
}
```




---

# Thiết kế DTO - Book Service

Tài liệu này mô tả chi tiết các Data Transfer Objects (DTOs) được sử dụng để trả về dữ liệu cho các endpoint API của Book Service.

---

## DTOs Chung (Tham chiếu từ Common Library)

- `ApiResponse<T>`: Sử dụng cấu trúc chung.

## DTOs cho Thực thể Chính

### `AuthorDTO`

DTO đại diện cho thông tin tác giả.

```java
public class AuthorDTO {
    private Long id;
    private String name;
    // Getters and Setters
}
```

### `CategoryDTO`

DTO đại diện cho thông tin thể loại.

```java
public class CategoryDTO {
    private Long id;
    private String name;
    // Getters and Setters
}
```

### `PublisherDTO`

DTO đại diện cho thông tin nhà xuất bản.

```java
public class PublisherDTO {
    private Long id;
    private String name;
    // Getters and Setters
}
```

### `BookCopySummaryDTO`

DTO tóm tắt thông tin bản sao sách.

```java
public class BookCopySummaryDTO {
    private Long id;
    private String copyNumber;
    private String status; // AVAILABLE, BORROWED, RESERVED, LOST, DAMAGED
    private String location;
    // Getters and Setters
}
```

### `BookSummaryDTO`

DTO tóm tắt thông tin sách, dùng trong danh sách hoặc tìm kiếm.

```java
public class BookSummaryDTO {
    private Long id;
    private String title;
    private String isbn;
    private Integer publicationYear;
    private List<String> authorNames; // Chỉ lấy tên tác giả
    private String coverImageUrl;
    // Getters and Setters
}
```

### `BookDetailDTO`

DTO chi tiết thông tin sách, bao gồm tác giả, thể loại, nhà xuất bản và các bản sao.

```java
public class BookDetailDTO {
    private Long id;
    private String title;
    private String isbn;
    private Integer publicationYear;
    private String description;
    private String coverImageUrl;
    private PublisherDTO publisher;
    private List<AuthorDTO> authors;
    private List<CategoryDTO> categories;
    private List<BookCopySummaryDTO> copies; // Danh sách các bản sao có sẵn hoặc thông tin tổng quan
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Getters and Setters
}
```

## DTOs cho Book Management API (`/api/books/*`)

### `CreateBookRequest`

DTO cho request tạo sách mới.

```java
public class CreateBookRequest {
    private String title;
    private String isbn;
    private Integer publicationYear;
    private String description;
    private String coverImageUrl;
    private Long publisherId;
    private List<Long> authorIds;
    private List<Long> categoryIds;
    private List<CreateBookCopyRequest> initialCopies; // Optional: Tạo bản sao ban đầu
    // Getters and Setters
}
```

### `CreateBookCopyRequest`

DTO con trong `CreateBookRequest` để tạo bản sao ban đầu.

```java
public class CreateBookCopyRequest {
    private String copyNumber;
    private String status = "AVAILABLE";
    private String location;
    // Getters and Setters
}
```

### `UpdateBookRequest`

DTO cho request cập nhật thông tin sách.

```java
public class UpdateBookRequest {
    private String title;
    private String isbn;
    private Integer publicationYear;
    private String description;
    private String coverImageUrl;
    private Long publisherId;
    private List<Long> authorIds;
    private List<Long> categoryIds;
    // Getters and Setters
}
```

### `BookSearchCriteria`

DTO cho các tiêu chí tìm kiếm sách (`GET /api/books/search`).

```java
public class BookSearchCriteria {
    private String title;
    private String authorName;
    private String categoryName;
    private String isbn;
    private Integer publicationYear;
    // Pagination parameters (page, size, sort)
    // Getters and Setters
}
```

## DTOs cho Category API (`/api/categories/*`)

### `CreateCategoryRequest`

```java
public class CreateCategoryRequest {
    private String name;
    // Getters and Setters
}
```

## DTOs cho Author API (`/api/authors/*`)

### `CreateAuthorRequest`

```java
public class CreateAuthorRequest {
    private String name;
    // Getters and Setters
}
```

## DTOs cho Borrowing API (`/api/borrowings/*`)

### `BorrowRequest`

DTO cho request mượn sách.

```java
public class BorrowRequest {
    private Long userId;
    private Long bookCopyId;
    private LocalDate borrowDate; // Or default to now?
    private LocalDate dueDate;
    // Getters and Setters
}
```

### `ReturnRequest`

DTO cho request trả sách.

```java
public class ReturnRequest {
    private LocalDate returnDate; // Or default to now?
    // Potentially include condition assessment
    // Getters and Setters
}
```

### `BorrowingDTO`

DTO đại diện cho thông tin một lượt mượn sách.

```java
public class BorrowingDTO {
    private Long id;
    private Long userId;
    private BookCopySummaryDTO bookCopy; // Thông tin bản sao đã mượn
    private BookSummaryDTO book; // Thông tin tóm tắt sách
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status; // ACTIVE, RETURNED, OVERDUE
    private BigDecimal fineAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Getters and Setters
}
```

## DTOs cho Reservation API (`/api/reservations/*`)

### `ReservationRequest`

DTO cho request đặt trước sách.

```java
public class ReservationRequest {
    private Long userId;
    private Long bookId;
    // Getters and Setters
}
```

### `ReservationDTO`

DTO đại diện cho thông tin một lượt đặt trước.

```java
public class ReservationDTO {
    private Long id;
    private Long userId;
    private BookSummaryDTO book; // Thông tin tóm tắt sách đã đặt
    private LocalDateTime reservationDate;
    private LocalDateTime expiryDate; // Hạn chót để mượn khi sách có sẵn
    private String status; // PENDING, AVAILABLE, CANCELED, EXPIRED, FULFILLED
    private LocalDateTime notificationSentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Getters and Setters
}
```



---

# Thiết kế DTO - Notification Service

Tài liệu này mô tả chi tiết các Data Transfer Objects (DTOs) được sử dụng để trả về dữ liệu cho các endpoint API của Notification Service.

---

## DTOs Chung (Tham chiếu từ Common Library)

- `ApiResponse<T>`: Sử dụng cấu trúc chung.

## DTOs cho Thực thể Chính

### `NotificationTemplateDTO`

DTO đại diện cho thông tin một mẫu thông báo.

```java
public class NotificationTemplateDTO {
    private Long id;
    private String name; // e.g., DUE_DATE_REMINDER
    private String type; // EMAIL, SMS, PUSH
    private String subjectTemplate;
    private String contentTemplate;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Getters and Setters
}
```

### `NotificationPreferenceDTO`

DTO đại diện cho tùy chọn nhận thông báo của người dùng.

```java
public class NotificationPreferenceDTO {
    private Long id;
    private Long userId;
    private boolean emailEnabled;
    private boolean smsEnabled;
    private boolean pushEnabled;
    // Add specific notification type preferences if needed, e.g.:
    // private boolean borrowNotificationEnabled;
    // private boolean returnReminderEnabled;
    // private boolean overdueNotificationEnabled;
    // private boolean reservationNotificationEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Getters and Setters
}
```

### `NotificationSummaryDTO`

DTO tóm tắt thông tin thông báo, dùng trong danh sách.

```java
public class NotificationSummaryDTO {
    private Long id;
    private Long userId;
    private String type; // EMAIL, SMS, PUSH
    private String subject; // Or a truncated title/content snippet
    private String status; // SENT, DELIVERED, READ, FAILED
    private boolean isRead; // Derived from read_at
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
    // Getters and Setters
}
```

### `NotificationDetailDTO`

DTO chi tiết thông tin một thông báo.

```java
public class NotificationDetailDTO {
    private Long id;
    private Long userId;
    private String type; // EMAIL, SMS, PUSH
    private String subject;
    private String content;
    private String status; // SENT, DELIVERED, READ, FAILED
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long templateId; // Optional: ID of the template used
    // Getters and Setters
}
```

## DTOs cho Notification Management API (`/api/notifications/*`)

*(Chủ yếu sử dụng `NotificationSummaryDTO` và `NotificationDetailDTO` để trả về)*

### `MarkNotificationReadRequest` (Implicit - No body needed for PUT)

*(Không cần DTO request body riêng, ID lấy từ path)*

## DTOs cho Notification Preference API (`/api/preferences/*`)

### `UpdateNotificationPreferenceRequest`

DTO cho request cập nhật tùy chọn thông báo.

```java
public class UpdateNotificationPreferenceRequest {
    private Boolean emailEnabled;
    private Boolean smsEnabled;
    private Boolean pushEnabled;
    // Add specific notification type preferences if needed
    // Getters and Setters
}
```

## DTOs cho Template Management API (`/api/templates/*`)

### `CreateNotificationTemplateRequest`

DTO cho request tạo mẫu thông báo mới.

```java
public class CreateNotificationTemplateRequest {
    private String name;
    private String type; // EMAIL, SMS, PUSH
    private String subjectTemplate;
    private String contentTemplate;
    private Boolean isActive = true;
    // Getters and Setters
}
```

### `UpdateNotificationTemplateRequest`

DTO cho request cập nhật mẫu thông báo.

```java
public class UpdateNotificationTemplateRequest {
    private String name;
    private String type;
    private String subjectTemplate;
    private String contentTemplate;
    private Boolean isActive;
    // Getters and Setters
}
```

