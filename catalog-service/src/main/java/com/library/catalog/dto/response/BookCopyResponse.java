package com.library.catalog.dto.response;

import com.library.catalog.repository.BookCopyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCopyResponse {

    private Long id;
    private Long bookId;
    private String copyNumber;
    private BookCopyStatus status;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private BookInfo book;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookInfo {
        private Long id;
        private String title;
        private String isbn;
    }
}