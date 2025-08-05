package com.library.catalog.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
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
@Table(name = "categories")
@EntityListeners(AuditingEntityListener.class)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(nullable = false, length = 100, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

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

    // Default constructor
    public Category() {
    }

    // Constructor with required fields
    public Category(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    // Constructor with name, slug and description
    public Category(String name, String slug, String description) {
        this.name = name;
        this.slug = slug;
        this.description = description;
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
}