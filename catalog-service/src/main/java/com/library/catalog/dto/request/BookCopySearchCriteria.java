package com.library.catalog.dto.request;

import com.library.catalog.repository.BookCopyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCopySearchCriteria {

    private Long bookId;
    private String copyNumber;
    private BookCopyStatus status;
    private String location;
    private LocalDateTime createdAtFrom;
    private LocalDateTime createdAtTo;
    private LocalDateTime updatedAtFrom;
    private LocalDateTime updatedAtTo;
}