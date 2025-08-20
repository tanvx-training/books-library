package com.library.catalog.repository;

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
@Table(name = "book_authors")
@IdClass(BookAuthor.BookAuthorId.class)
@EntityListeners(AuditingEntityListener.class)
public class BookAuthor {

    @Id
    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Id
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    // Constructor with required fields
    public BookAuthor(Long bookId, Long authorId) {
        this.bookId = bookId;
        this.authorId = authorId;
    }

    // Composite key class
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookAuthorId implements Serializable {
        private Long bookId;
        private Long authorId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BookAuthorId that = (BookAuthorId) o;
            return Objects.equals(bookId, that.bookId) && Objects.equals(authorId, that.authorId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(bookId, authorId);
        }
    }

    // Business methods
    public boolean hasValidBookId() {
        return bookId != null && bookId > 0;
    }

    public boolean hasValidAuthorId() {
        return authorId != null && authorId > 0;
    }

    public boolean isValidAssociation() {
        return hasValidBookId() && hasValidAuthorId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookAuthor that = (BookAuthor) o;
        return Objects.equals(bookId, that.bookId) && Objects.equals(authorId, that.authorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, authorId);
    }
}