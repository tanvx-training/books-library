package com.library.book.utils.enums;

/**
 * Enum đại diện cho trạng thái của một lần mượn sách.
 */
public enum LoanStatus {
    ACTIVE,    // Đang trong thời gian mượn (chưa trả)
    RETURNED,  // Đã trả sách
    OVERDUE    // Quá hạn chưa trả
}
