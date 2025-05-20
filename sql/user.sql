### **2. User Service**
User Service quản lý thông tin người dùng, quyền truy cập, lịch sử mượn/trả sách và thông báo. Dưới đây là các bảng chính trong cơ sở dữ liệu của service này:

#### **Bảng `users`**
Lưu trữ thông tin người dùng (admin, thủ thư, thành viên).
- `id` (INT hoặc UUID, khóa chính)
- `username` (VARCHAR, tên đăng nhập, unique)
- `email` (VARCHAR, email, unique)
- `password_hash` (VARCHAR, mật khẩu mã hóa)
- `role` (ENUM hoặc VARCHAR, vai trò: 'admin', 'librarian', 'member')
- `created_at` (TIMESTAMP, thời gian tạo)
- `updated_at` (TIMESTAMP, thời gian cập nhật)

#### **Bảng `permissions`**
Quản lý quyền truy cập chi tiết cho từng người dùng.
- `id` (INT hoặc UUID, khóa chính)
- `user_id` (INT hoặc UUID, khóa ngoại liên kết với bảng `users`)
- `permission_type` (VARCHAR, loại quyền, ví dụ: 'borrow', 'manage_books')
- `created_at` (TIMESTAMP, thời gian tạo)
- `updated_at` (TIMESTAMP, thời gian cập nhật)

#### **Bảng `borrow_history`**
Theo dõi lịch sử mượn và trả sách của người dùng.
- `id` (INT hoặc UUID, khóa chính)
- `user_id` (INT hoặc UUID, khóa ngoại liên kết với bảng `users`)
- `book_id` (INT hoặc UUID, khóa ngoại liên kết với bảng `books`)
- `borrow_date` (TIMESTAMP, ngày mượn)
- `return_date` (TIMESTAMP, ngày trả, có thể null nếu chưa trả)
- `status` (ENUM hoặc VARCHAR, trạng thái: 'borrowed', 'returned', 'overdue')
- `created_at` (TIMESTAMP, thời gian tạo)
- `updated_at` (TIMESTAMP, thời gian cập nhật)