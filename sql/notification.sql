Quản lý thông báo gửi đến người dùng (ví dụ: thông báo mượn, trả, quá hạn).
- `id` (INT hoặc UUID, khóa chính)
- `user_id` (INT hoặc UUID, khóa ngoại liên kết với bảng `users`)
- `message` (TEXT, nội dung thông báo)
- `notification_type` (VARCHAR, loại thông báo: 'borrow', 'return', 'overdue')
- `is_read` (BOOLEAN, đã đọc hay chưa)
- `created_at` (TIMESTAMP, thời gian tạo)
- `updated_at` (TIMESTAMP, thời gian cập nhật)