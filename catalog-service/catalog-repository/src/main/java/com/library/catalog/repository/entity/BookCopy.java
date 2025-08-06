package com.library.catalog.repository.entity;

import com.library.catalog.repository.enums.BookCopyCondition;
import com.library.catalog.repository.enums.BookCopyStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book_copies")
public class BookCopy extends BaseSoftDeleteEntity {

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

    @PrePersist
    void generatePublicId() {
        if (this.publicId == null) {
            this.publicId = UUID.randomUUID();
        }
    }
}