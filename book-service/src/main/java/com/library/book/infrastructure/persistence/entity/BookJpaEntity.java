package com.library.book.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class BookJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "isbn", unique = true, length = 20)
    private String isbn;

    @Column(name = "publisher_id")
    private Long publisherId;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @ElementCollection
    @CollectionTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id")
    )
    @Column(name = "author_id")
    private java.util.List<Long> authorIds = new java.util.ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_id")
    )
    @Column(name = "category_id")
    private java.util.List<Long> categoryIds = new java.util.ArrayList<>();

    @Column(name = "delete_flg", nullable = false)
    private boolean deleteFlg;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;
} 