package com.library.catalog.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "books")
public class Book extends BaseSoftDeleteEntity {

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

    @PrePersist
    void generatePublicId() {
        if (this.publicId == null) {
            this.publicId = UUID.randomUUID();
        }
    }
}