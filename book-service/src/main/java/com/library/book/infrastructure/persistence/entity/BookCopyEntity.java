package com.library.book.infrastructure.persistence.entity;

import com.library.book.domain.model.bookcopy.BookCondition;
import com.library.book.domain.model.bookcopy.BookCopyStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * JPA Entity for BookCopy
 */
@Getter
@Setter
@Entity
@Table(name = "book_copies")
public class BookCopyEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "copy_number", nullable = false, length = 20)
    private String copyNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BookCopyStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition", length = 20)
    private BookCondition condition;

    @Column(name = "location", length = 50)
    private String location;

    @Column(name = "acquired_date")
    private LocalDateTime acquiredDate;

    @Column(name = "current_borrower_keycloak_id", length = 36)
    private String currentBorrowerKeycloakId;

    @Column(name = "borrowed_date")
    private LocalDateTime borrowedDate;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 36)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 36)
    private String updatedBy;

    @Column(name = "delete_flg", nullable = false)
    private boolean deleteFlg = false;

    // Unique constraint on book_id and copy_number
    @Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"book_id", "copy_number"})
    })
    public static class UniqueConstraints {}
}