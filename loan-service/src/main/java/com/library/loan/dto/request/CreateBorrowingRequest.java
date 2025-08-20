package com.library.loan.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBorrowingRequest {

    @NotNull(message = "Book copy public ID is required")
    private UUID bookCopyPublicId;

    @NotNull(message = "User public ID is required")
    private UUID userPublicId;

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate borrowDate;
}