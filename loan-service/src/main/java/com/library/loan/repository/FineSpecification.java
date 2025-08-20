package com.library.loan.repository;

import com.library.loan.repository.Borrowing;
import com.library.loan.repository.Fine;
import com.library.loan.repository.FineStatus;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class FineSpecification {

    public static Specification<Fine> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get("deletedAt"));
    }

    public static Specification<Fine> hasBorrowingId(Long borrowingId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("borrowingId"), borrowingId);
    }

    public static Specification<Fine> hasUserPublicId(UUID userPublicId) {
        return (root, query, criteriaBuilder) -> {
            Join<Fine, Borrowing> borrowingJoin = root.join("borrowing");
            return criteriaBuilder.equal(borrowingJoin.get("userPublicId"), userPublicId);
        };
    }

    public static Specification<Fine> hasStatus(FineStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Fine> hasPublicId(UUID publicId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("publicId"), publicId);
    }
}