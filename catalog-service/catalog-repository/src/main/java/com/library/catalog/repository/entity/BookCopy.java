package com.library.catalog.repository.entity;

import com.library.catalog.repository.enums.BookCopyCondition;
import com.library.catalog.repository.enums.BookCopyStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book_copies")
@EntityListeners(AuditingEntityListener.class)
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "copy_number", nullable = false, length = 20)
    private String copyNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookCopyStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BookCopyCondition condition;

    @Column(length = 50)
    private String location;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", length = 36)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 36)
    private String updatedBy;

    // Constructor with required fields
    public BookCopy(Long bookId, String copyNumber, BookCopyStatus status) {
        this.bookId = bookId;
        this.copyNumber = copyNumber;
        this.status = status;
    }

    // Constructor with all fields except audit fields
    public BookCopy(Long bookId, String copyNumber, BookCopyStatus status, BookCopyCondition condition, String location) {
        this.bookId = bookId;
        this.copyNumber = copyNumber;
        this.status = status;
        this.condition = condition;
        this.location = location;
    }

    @PrePersist
    void generatePublicId() {
        if (this.publicId == null) {
            this.publicId = UUID.randomUUID();
        }
    }

    // Business methods for soft deletion using timestamp
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    public void markAsActive() {
        this.deletedAt = null;
    }

    // Business validation methods
    public boolean hasValidCopyNumber() {
        return copyNumber != null && !copyNumber.trim().isEmpty() && copyNumber.length() <= 20;
    }

    public boolean hasValidBookId() {
        return bookId != null && bookId > 0;
    }

    public boolean hasValidStatus() {
        return status != null;
    }

    public boolean hasValidLocation() {
        return location == null || (!location.trim().isEmpty() && location.length() <= 50);
    }

    // Business logic methods
    public boolean isAvailable() {
        return status == BookCopyStatus.AVAILABLE && !isDeleted();
    }

    public boolean isBorrowed() {
        return status == BookCopyStatus.BORROWED && !isDeleted();
    }

    public boolean isReserved() {
        return status == BookCopyStatus.RESERVED && !isDeleted();
    }

    public boolean isInMaintenance() {
        return status == BookCopyStatus.MAINTENANCE && !isDeleted();
    }

    public boolean isLost() {
        return status == BookCopyStatus.LOST && !isDeleted();
    }

    public boolean canBeBorrowed() {
        return isAvailable() && (condition == null || condition.canBeBorrowed());
    }

    public boolean canBeReserved() {
        return (isAvailable() || isBorrowed()) && (condition == null || condition.canBeBorrowed());
    }
}