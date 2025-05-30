package com.library.book.domain.enums;

/**
 * Enum đại diện cho trạng thái của một đơn đặt trước (reservation).
 */
public enum ReservationStatus {
    PENDING,    // Đơn đặt trước đã được tạo, chờ xử lý
    AVAILABLE,  // Sách đã có mặt và sẵn sàng cho người đặt đến mượn
    CANCELED,   // Đơn đặt trước đã bị hủy
    EXPIRED     // Đơn đặt trước đã hết hạn (người dùng không đến nhận trong thời gian quy định)
}
