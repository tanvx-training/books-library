package com.library.catalog.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "book_categories")
@IdClass(BookCategory.BookCategoryId.class)
@EntityListeners(AuditingEntityListener.class)
public class BookCategory {

    @Id
    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Id
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    // Constructor with required fields
    public BookCategory(Long bookId, Long categoryId) {
        this.bookId = bookId;
        this.categoryId = categoryId;
    }

    // Composite key class
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookCategoryId implements Serializable {
        private Long bookId;
        private Long categoryId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BookCategoryId that = (BookCategoryId) o;
            return Objects.equals(bookId, that.bookId) && Objects.equals(categoryId, that.categoryId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(bookId, categoryId);
        }
    }

    // Business methods
    public boolean hasValidBookId() {
        return bookId != null && bookId > 0;
    }

    public boolean hasValidCategoryId() {
        return categoryId != null && categoryId > 0;
    }

    public boolean isValidAssociation() {
        return hasValidBookId() && hasValidCategoryId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookCategory that = (BookCategory) o;
        return Objects.equals(bookId, that.bookId) && Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, categoryId);
    }
}