package com.library.catalog.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "authors")
@EntityListeners(AuditingEntityListener.class)
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 256)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Column(name = "delete_flg", nullable = false)
    private Boolean deleteFlag = false;

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
    public Author() {
    }

    // Constructor with required fields
    public Author(String name) {
        this.name = name;
        this.deleteFlag = false;
    }

    // Constructor with name and biography
    public Author(String name, String biography) {
        this.name = name;
        this.biography = biography;
        this.deleteFlag = false;
    }

    // Business methods
    public boolean isDeleted() {
        return Boolean.TRUE.equals(deleteFlag);
    }

    public void markAsDeleted() {
        this.deleteFlag = true;
    }

    public void markAsActive() {
        this.deleteFlag = false;
    }
}