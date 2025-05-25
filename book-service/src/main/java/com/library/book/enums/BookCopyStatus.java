package com.library.book.enums;

/**
 * Enum đại diện cho các trạng thái có thể có của một bản sao sách.
 */
public enum BookCopyStatus {
    AVAILABLE,   // Sẵn sàng cho mượn
    BORROWED,    // Đã được mượn
    RESERVED,    // Đã được đặt giữ
    LOST,        // Bị mất
    DAMAGED;     // Bị hỏng
}
