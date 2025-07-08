package com.library.book.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "book_copies",
        uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "copy_number"})
)
public class BookCopyJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "book_id", nullable = false)
    private Long bookId;
    
    @Column(name = "book_title")
    private String bookTitle;

    @Column(name = "copy_number", nullable = false, length = 20)
    private String copyNumber;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "condition", length = 20)
    private String condition;

    @Column(name = "location", length = 50)
    private String location;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "delete_flg", nullable = false)
    private boolean deleteFlg;
} 