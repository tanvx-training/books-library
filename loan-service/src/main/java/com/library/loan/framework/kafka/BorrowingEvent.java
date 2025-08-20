package com.library.loan.framework.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowingEvent {

    @JsonProperty("eventId")
    private String eventId;

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("borrowingPublicId")
    private UUID borrowingPublicId;

    @JsonProperty("bookCopyPublicId")
    private UUID bookCopyPublicId;

    @JsonProperty("userPublicId")
    private UUID userPublicId;

    @JsonProperty("borrowDate")
    private LocalDate borrowDate;

    @JsonProperty("dueDate")
    private LocalDate dueDate;

    @JsonProperty("returnDate")
    private LocalDate returnDate;

    @JsonProperty("status")
    private String status;

    @JsonProperty("fineAmount")
    private BigDecimal fineAmount;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("correlationId")
    private String correlationId;

    /**
     * Creates a borrowing event from borrowing data.
     */
    public static BorrowingEvent create(String eventType, UUID borrowingPublicId, 
                                      UUID bookCopyPublicId, UUID userPublicId,
                                      LocalDate borrowDate, LocalDate dueDate, 
                                      LocalDate returnDate, String status,
                                      BigDecimal fineAmount, String userId,
                                      String correlationId) {
        BorrowingEvent event = new BorrowingEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(eventType);
        event.setBorrowingPublicId(borrowingPublicId);
        event.setBookCopyPublicId(bookCopyPublicId);
        event.setUserPublicId(userPublicId);
        event.setBorrowDate(borrowDate);
        event.setDueDate(dueDate);
        event.setReturnDate(returnDate);
        event.setStatus(status);
        event.setFineAmount(fineAmount);
        event.setUserId(userId);
        event.setTimestamp(LocalDateTime.now());
        event.setCorrelationId(correlationId);
        return event;
    }
}