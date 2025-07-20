# API Endpoints Security Specification

## Tổng Quan

Tài liệu này định nghĩa đầy đủ các endpoint API trong hệ thống Library Management System cùng với các yêu cầu authentication và authorization tương ứng.

## Phân Cấp Quyền (Role Hierarchy)

### 1. **PUBLIC** - Không yêu cầu authentication
- Truy cập thông tin công khai
- Health checks
- Authentication endpoints

### 2. **USER** - Người dùng thông thường
- Quản lý profile cá nhân
- Mượn/trả sách
- Xem thông tin sách
- Quản lý reservation

### 3. **LIBRARIAN** - Thủ thư
- Tất cả quyền của USER
- Quản lý người dùng
- Quản lý sách và bản sao
- Xử lý lending operations
- Xem báo cáo và thống kê

### 4. **ADMIN** - Quản trị viên
- Tất cả quyền của LIBRARIAN
- Xóa dữ liệu
- Bulk operations
- System administration
- Configuration management

### 5. **SERVICE** - Service-to-service communication
- Internal API calls giữa các microservices

## Chi Tiết Endpoints

### 🔓 PUBLIC ENDPOINTS (Không yêu cầu authentication)

#### Health & Monitoring
```
GET    /actuator/**                    - Spring Actuator endpoints
GET    /health/**                      - Health check endpoints
```

#### Authentication
```
POST   /auth/login                     - User login
POST   /auth/register                  - User registration
POST   /auth/refresh                   - Refresh token
POST   /auth/logout                    - User logout
GET    /auth/verify-email              - Email verification
POST   /auth/forgot-password           - Password reset request
POST   /auth/reset-password            - Password reset
GET    /login/oauth2/code/**           - OAuth2 callback
GET    /oauth2/**                      - OAuth2 endpoints
```

#### Public Book Information
```
GET    /api/books/public/**            - Public book catalog
GET    /api/books/search               - Basic book search
GET    /api/books/{id}/details         - Public book details
GET    /api/categories/public          - Public categories
GET    /api/authors/public             - Public authors
GET    /api/publishers/public          - Public publishers
```

#### Webhooks & External Integration
```
POST   /webhooks/**                    - External system webhooks
```

---

### 👤 USER ENDPOINTS (Yêu cầu role: USER)

#### User Profile Management
```
GET    /api/users/me                   - Get own profile
PUT    /api/users/me                   - Update own profile
GET    /api/users/me/profile           - Get detailed profile
PUT    /api/users/me/profile           - Update detailed profile
POST   /api/users/me/change-password   - Change password
```

#### Library Card Operations
```
GET    /api/users/me/library-cards             - Get own library cards
POST   /api/users/me/library-cards/request     - Request new library card
GET    /api/users/me/library-cards/{cardId}    - Get specific card details
```

#### Borrowing Operations
```
GET    /api/users/me/borrowed-books            - Current borrowed books
GET    /api/users/me/borrowing-history         - Borrowing history
GET    /api/users/me/reservations              - Current reservations
POST   /api/users/me/reservations              - Create reservation
DELETE /api/users/me/reservations/{id}         - Cancel reservation
```

#### Notifications
```
GET    /api/users/me/notifications                     - Get notifications
PUT    /api/users/me/notifications/{id}/read           - Mark as read
```

#### Book Browsing (Authenticated)
```
GET    /api/books                              - Browse books
GET    /api/books/{id}                         - Get book details
GET    /api/books/{id}/availability            - Check availability
GET    /api/books/search/advanced              - Advanced search
GET    /api/books/recommendations              - Personalized recommendations
```

#### Book Copy Information
```
GET    /api/book-copies/book/{bookId}          - Get copies of a book
GET    /api/book-copies/{id}/status            - Get copy status
```

#### Catalog Browsing
```
GET    /api/categories                         - Browse categories
GET    /api/categories/{id}                    - Get category details
GET    /api/authors                            - Browse authors
GET    /api/authors/{id}                       - Get author details
GET    /api/publishers                         - Browse publishers
GET    /api/publishers/{id}                    - Get publisher details
```

#### Personal Reports
```
GET    /api/reports/user/borrowing-summary     - Personal borrowing summary
GET    /api/reports/user/reading-statistics    - Reading statistics
```

#### File Access
```
GET    /api/files/{fileId}                     - Download files (book covers, etc.)
```

---

### 📚 LIBRARIAN ENDPOINTS (Yêu cầu role: LIBRARIAN)

#### User Management
```
GET    /api/users                              - List all users
GET    /api/users/{id}                         - Get user details
POST   /api/users                              - Create new user
PUT    /api/users/{id}                         - Update user
GET    /api/users/active                       - Get active users
GET    /api/users/inactive                     - Get inactive users
GET    /api/users/role/{roleName}              - Get users by role
GET    /api/users/{id}/borrowing-eligibility   - Check borrowing eligibility
GET    /api/users/statistics                   - User statistics
```

#### Library Card Management
```
POST   /api/users/{id}/library-cards           - Issue library card
GET    /api/library-cards                      - List all cards
GET    /api/library-cards/{id}                 - Get card details
PUT    /api/library-cards/{id}/renew           - Renew card
PUT    /api/library-cards/{id}/deactivate      - Deactivate card
GET    /api/library-cards/expiring             - Get expiring cards
GET    /api/library-cards/statistics           - Card statistics
```

#### Book Management
```
POST   /api/books                              - Add new book
PUT    /api/books/{id}                         - Update book
GET    /api/books/management                   - Management view
GET    /api/books/{id}/copies                  - Get book copies
GET    /api/books/statistics                   - Book statistics
```

#### Book Copy Management
```
POST   /api/book-copies                        - Add new copy
POST   /api/book-copies/bulk                   - Bulk add copies
GET    /api/book-copies                        - List all copies
GET    /api/book-copies/{id}                   - Get copy details
PUT    /api/book-copies/{id}                   - Update copy
PUT    /api/book-copies/{id}/status            - Update copy status
GET    /api/book-copies/maintenance            - Copies needing maintenance
GET    /api/book-copies/statistics             - Copy statistics
```

#### Lending Operations
```
POST   /api/lending/borrow                     - Process borrowing
POST   /api/lending/return                     - Process return
POST   /api/lending/renew                      - Renew lending
GET    /api/lending/overdue                    - Get overdue items
GET    /api/lending/due-soon                   - Get items due soon
GET    /api/lending/active                     - Get active lendings
GET    /api/lending/history                    - Lending history
GET    /api/lending/statistics                 - Lending statistics
```

#### Catalog Management
```
POST   /api/categories                         - Add category
PUT    /api/categories/{id}                    - Update category
GET    /api/categories/management              - Category management
POST   /api/authors                            - Add author
PUT    /api/authors/{id}                       - Update author
GET    /api/authors/management                 - Author management
POST   /api/publishers                         - Add publisher
PUT    /api/publishers/{id}                    - Update publisher
GET    /api/publishers/management              - Publisher management
```

#### Notification Management
```
POST   /api/notifications/send                 - Send notification
POST   /api/notifications/broadcast            - Broadcast notification
GET    /api/notifications/templates            - Get templates
GET    /api/notifications/history              - Notification history
```

#### History & Audit (Read Access)
```
GET    /api/history/audit-logs                         - Get audit logs
GET    /api/history/audit-logs/entity/{name}/{id}      - Entity audit logs
GET    /api/history/audit-logs/user/{userId}           - User audit logs
GET    /api/history/audit-logs/search                  - Search audit logs
GET    /api/history/statistics                         - Audit statistics
```

#### File Management
```
POST   /api/files/upload                       - Upload files
DELETE /api/files/{fileId}                     - Delete files
```

#### Reports
```
GET    /api/reports/librarian/daily-summary    - Daily summary
GET    /api/reports/librarian/overdue-report   - Overdue report
GET    /api/reports/librarian/popular-books    - Popular books report
GET    /api/reports/librarian/user-activity    - User activity report
```

---

### 🔧 ADMIN ENDPOINTS (Yêu cầu role: ADMIN)

#### User Administration
```
DELETE /api/users/{id}                          - Delete user
POST   /api/users/{id}/suspend                  - Suspend user
POST   /api/users/{id}/reactivate               - Reactivate user
PUT    /api/users/{id}/roles                    - Update user roles
POST   /api/users/{id}/roles/{roleName}         - Add role to user
DELETE /api/users/{id}/roles/{roleName}         - Remove role from user
GET    /api/users/admin/all                     - Get all users (admin view)
POST   /api/users/admin/bulk-operations         - Bulk user operations
```

#### System Administration
```
GET    /api/admin/**                            - Admin panel endpoints
GET    /api/system/health                       - System health
GET    /api/system/metrics                      - System metrics
POST   /api/system/maintenance                  - Maintenance mode
GET    /api/system/logs                         - System logs
```

#### Book Administration
```
DELETE /api/books/{id}                          - Delete book
POST   /api/books/admin/bulk-import             - Bulk import books
POST   /api/books/admin/bulk-update             - Bulk update books
DELETE /api/books/admin/bulk-delete             - Bulk delete books
```

#### Book Copy Administration
```
DELETE /api/book-copies/{id}                    - Delete copy
POST   /api/book-copies/admin/bulk-operations   - Bulk copy operations
```

#### Catalog Administration
```
DELETE /api/categories/{id}                     - Delete category
POST   /api/categories/admin/merge              - Merge categories
DELETE /api/authors/{id}                        - Delete author
POST   /api/authors/admin/merge                 - Merge authors
DELETE /api/publishers/{id}                     - Delete publisher
POST   /api/publishers/admin/merge              - Merge publishers
```

#### Library Card Administration
```
DELETE /api/library-cards/{id}                  - Delete card
POST   /api/library-cards/admin/bulk-operations - Bulk card operations
```

#### Lending Administration
```
DELETE /api/lending/{id}                        - Delete lending record
POST   /api/lending/admin/force-return          - Force return
POST   /api/lending/admin/waive-fine            - Waive fine
GET    /api/lending/admin/all-overdue           - All overdue items
```

#### Notification Administration
```
DELETE /api/notifications/{id}                  - Delete notification
POST   /api/notifications/admin/system-alert    - System alert
GET    /api/notifications/admin/all             - All notifications
```

#### History Administration
```
DELETE /api/history/audit-logs/{id}             - Delete audit log
POST   /api/history/admin/cleanup               - Cleanup old logs
GET    /api/history/admin/full-audit            - Full audit report
```

#### Configuration Management
```
GET    /api/config/**                           - Get configuration
PUT    /api/config/**                           - Update configuration
POST   /api/config/reload                       - Reload configuration
```

#### Advanced Reports
```
GET    /api/reports/admin/system-usage          - System usage report
GET    /api/reports/admin/financial-summary     - Financial summary
GET    /api/reports/admin/user-analytics        - User analytics
GET    /api/reports/admin/performance-metrics   - Performance metrics
```

---

### 🔗 SERVICE ENDPOINTS (Service-to-service communication)

#### Internal Communication
```
GET    /internal/users/{id}/validate            - Validate user
GET    /internal/books/{id}/availability        - Check book availability
POST   /internal/notifications/send             - Send internal notification
GET    /internal/audit/log                      - Internal audit logging
```

---

## Security Implementation Details

### JWT Token Structure
```json
{
  "sub": "user-id",
  "email": "user@example.com",
  "roles": ["USER", "LIBRARIAN"],
  "iat": 1234567890,
  "exp": 1234567890,
  "iss": "library-system"
}
```

### Role Mapping
- Keycloak roles được map vào Spring Security authorities
- Format: `ROLE_<ROLE_NAME>` (e.g., `ROLE_USER`, `ROLE_LIBRARIAN`)

### Error Responses
```json
{
  "error": "UNAUTHORIZED",
  "message": "Authentication required",
  "timestamp": "2024-01-01T00:00:00Z",
  "path": "/api/users/me"
}
```

```json
{
  "error": "FORBIDDEN", 
  "message": "Insufficient privileges",
  "timestamp": "2024-01-01T00:00:00Z",
  "path": "/api/admin/users"
}
```

### Rate Limiting
- USER: 100 requests/minute
- LIBRARIAN: 500 requests/minute  
- ADMIN: 1000 requests/minute
- PUBLIC: 50 requests/minute

### CORS Configuration
- Allowed Origins: Configurable (development: localhost, production: specific domains)
- Allowed Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
- Allowed Headers: All
- Credentials: Allowed
- Max Age: 3600 seconds

## Best Practices

### 1. **Principle of Least Privilege**
- Users chỉ có quyền truy cập minimum cần thiết
- Role hierarchy được enforce strictly

### 2. **Defense in Depth**
- Gateway-level security
- Service-level security
- Method-level security annotations

### 3. **Audit Trail**
- Tất cả operations được log
- User actions được track
- Security events được monitor

### 4. **Token Management**
- JWT tokens có expiration time
- Refresh token mechanism
- Token revocation support

### 5. **Input Validation**
- All inputs được validate
- SQL injection prevention
- XSS protection

## Monitoring & Alerting

### Security Events
- Failed authentication attempts
- Unauthorized access attempts
- Privilege escalation attempts
- Suspicious activity patterns

### Metrics
- Authentication success/failure rates
- API usage by role
- Response times by endpoint
- Error rates by service

## Compliance

### Data Protection
- GDPR compliance for user data
- Data retention policies
- Right to be forgotten implementation

### Security Standards
- OWASP Top 10 compliance
- Regular security audits
- Penetration testing

---

*Tài liệu này được cập nhật thường xuyên để phản ánh các thay đổi trong hệ thống security.*