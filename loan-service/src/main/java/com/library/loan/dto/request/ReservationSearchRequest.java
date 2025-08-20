package com.library.loan.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationSearchRequest {

    private int page = 0;

    private int size = 10;

    private String userPublicId;

    private String bookPublicId;

    private String status;

    private LocalDate reservationDateFrom;
    private LocalDate reservationDateTo;

    private String sortBy = "reservationDate";
    private String order = "desc";

    public Pageable toPageable() {
        Sort.Direction direction = "asc".equalsIgnoreCase(order) 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;

        String sortField = mapSortField(sortBy);
        Sort sort = Sort.by(direction, sortField);
        
        return PageRequest.of(page, size, sort);
    }

    private String mapSortField(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "reservationDate";
        }

        // Map API field names to entity field names
        return switch (sortBy) {
            case "reservation_date" -> "reservationDate";
            case "expiry_date" -> "expiryDate";
            case "created_at" -> "createdAt";
            case "updated_at" -> "updatedAt";
            case "fulfilled_date" -> "fulfilledDate";
            case "pickup_expiry_date" -> "pickupExpiryDate";
            case "queue_position" -> "queuePosition";
            default -> sortBy; // Keep as is for other fields like status
        };
    }
}