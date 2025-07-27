package com.library.catalog.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
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
@Table(name = "books")
@EntityListeners(AuditingEntityListener.class)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 50, unique = true)
    private String isbn;

    @Column(name = "publication_year", nullable = false)
    private Short publicationYear;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 20)
    private String language;

    @Column(name = "number_of_pages")
    private Integer numberOfPages;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Column(name = "publisher_id", nullable = false)
    private Long publisherId;

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
    public Book(String title, String isbn, Short publicationYear, Long publisherId) {
        this.title = title;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.publisherId = publisherId;
    }

    // Constructor with all fields except audit fields
    public Book(String title, String isbn, Short publicationYear, String description,
                String language, Integer numberOfPages, String coverImageUrl, Long publisherId) {
        this.title = title;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.description = description;
        this.language = language;
        this.numberOfPages = numberOfPages;
        this.coverImageUrl = coverImageUrl;
        this.publisherId = publisherId;
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
    public boolean hasValidIsbn() {
        return isbn != null && !isbn.trim().isEmpty() && isbn.length() <= 50;
    }

    public boolean hasValidTitle() {
        return title != null && !title.trim().isEmpty() && title.length() <= 255;
    }

    public boolean hasValidPublicationYear() {
        return publicationYear != null && publicationYear > 0 && publicationYear <= java.time.Year.now().getValue();
    }

    public boolean hasValidPublisherId() {
        return publisherId != null && publisherId > 0;
    }

    public boolean hasValidLanguage() {
        return language == null || (!language.trim().isEmpty() && language.length() <= 20);
    }

    public boolean hasValidNumberOfPages() {
        return numberOfPages == null || numberOfPages > 0;
    }
}