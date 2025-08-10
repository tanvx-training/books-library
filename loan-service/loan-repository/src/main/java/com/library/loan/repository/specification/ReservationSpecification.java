package com.library.loan.repository.specification;

import com.library.loan.repository.entity.Reservation;
import com.library.loan.repository.enums.ReservationStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ReservationSpecification {

    public static Specification<Reservation> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get("deletedAt"));
    }

    public static Specification<Reservation> hasUserPublicId(UUID userPublicId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("userPublicId"), userPublicId);
    }

    public static Specification<Reservation> hasBookPublicId(UUID bookPublicId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("bookPublicId"), bookPublicId);
    }

    public static Specification<Reservation> hasStatus(ReservationStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Reservation> hasPublicId(UUID publicId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("publicId"), publicId);
    }

    public static Specification<Reservation> reservationDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate != null && endDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
                return criteriaBuilder.between(root.get("reservationDate"), startDateTime, endDateTime);
            } else if (startDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                return criteriaBuilder.greaterThanOrEqualTo(root.get("reservationDate"), startDateTime);
            } else if (endDate != null) {
                LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
                return criteriaBuilder.lessThanOrEqualTo(root.get("reservationDate"), endDateTime);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Reservation> expiryDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate != null && endDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
                return criteriaBuilder.between(root.get("expiryDate"), startDateTime, endDateTime);
            } else if (startDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                return criteriaBuilder.greaterThanOrEqualTo(root.get("expiryDate"), startDateTime);
            } else if (endDate != null) {
                LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
                return criteriaBuilder.lessThanOrEqualTo(root.get("expiryDate"), endDateTime);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Reservation> isExpired() {
        return (root, query, criteriaBuilder) -> {
            LocalDateTime now = LocalDateTime.now();
            return criteriaBuilder.or(
                // Pending reservations past their expiry date
                criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("status"), ReservationStatus.PENDING),
                    criteriaBuilder.lessThan(root.get("expiryDate"), now)
                ),
                // Fulfilled reservations past their pickup expiry date
                criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("status"), ReservationStatus.FULFILLED),
                    criteriaBuilder.lessThan(root.get("pickupExpiryDate"), now)
                )
            );
        };
    }

    public static Specification<Reservation> isActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                    criteriaBuilder.isNull(root.get("deletedAt")),
                    criteriaBuilder.in(root.get("status")).value(ReservationStatus.PENDING).value(ReservationStatus.FULFILLED)
                );
    }

    public static Specification<Reservation> searchWithFilters(UUID userPublicId, 
                                                              UUID bookPublicId, 
                                                              ReservationStatus status,
                                                              LocalDate reservationDateFrom,
                                                              LocalDate reservationDateTo) {
        Specification<Reservation> spec = isNotDeleted();

        if (userPublicId != null) {
            spec = spec.and(hasUserPublicId(userPublicId));
        }

        if (bookPublicId != null) {
            spec = spec.and(hasBookPublicId(bookPublicId));
        }

        if (status != null) {
            spec = spec.and(hasStatus(status));
        }

        if (reservationDateFrom != null || reservationDateTo != null) {
            spec = spec.and(reservationDateBetween(reservationDateFrom, reservationDateTo));
        }

        return spec;
    }
}