package com.library.book.utils.enums;

/**
 * Enum đại diện cho các trạng thái có thể có của một bản sao sách.
 */
public enum BookCopyStatus {
    AVAILABLE,   // Có sẵn (sẵn sàng cho mượn)
    BORROWED,    // Đang mượn
    RESERVED,    // Đặt trước
    MAINTENANCE, // Bảo trì
    LOST,        // Bị mất
    DAMAGED;     // Bị hỏng
}
