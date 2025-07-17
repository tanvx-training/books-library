package com.library.book.domain.model.author;

import com.library.book.domain.event.AuthorCreatedEvent;
import com.library.book.domain.event.AuthorUpdatedEvent;
import com.library.book.domain.exception.InvalidAuthorDataException;
import com.library.book.domain.model.shared.AggregateRoot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Author extends AggregateRoot {

    private AuthorId id;
    private AuthorName name;
    private Biography biography;
    private Set<Long> bookIds; // Books written by this author
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdByKeycloakId;
    private String updatedByKeycloakId;
    private boolean deleted;

    public static Author create(AuthorName name, Biography biography, String createdByKeycloakId) {
        validateCreationData(name, biography);
        
        Author author = new Author();
        author.id = AuthorId.createNew();
        author.name = name;
        author.biography = biography;
        author.bookIds = new HashSet<>();
        author.createdAt = LocalDateTime.now();
        author.updatedAt = LocalDateTime.now();
        author.createdByKeycloakId = createdByKeycloakId;
        author.deleted = false;

        author.registerEvent(new AuthorCreatedEvent(author.id.getValue(), author.name.getValue()));
        return author;
    }

    // Business methods
    public void updateName(AuthorName newName, String updatedByKeycloakId) {
        if (newName == null) {
            throw new InvalidAuthorDataException("Author name cannot be null");
        }
        
        if (!this.name.equals(newName)) {
            this.name = newName;
            this.updatedAt = LocalDateTime.now();
            this.updatedByKeycloakId = updatedByKeycloakId;
            
            registerEvent(new AuthorUpdatedEvent(this.id.getValue(), "name", newName.getValue()));
        }
    }

    public void updateBiography(Biography newBiography, String updatedByKeycloakId) {
        if (newBiography == null) {
            throw new InvalidAuthorDataException("Biography cannot be null");
        }
        
        if (!this.biography.equals(newBiography)) {
            this.biography = newBiography;
            this.updatedAt = LocalDateTime.now();
            this.updatedByKeycloakId = updatedByKeycloakId;
            
            registerEvent(new AuthorUpdatedEvent(this.id.getValue(), "biography", "Biography updated"));
        }
    }

    public void addBook(Long bookId) {
        if (bookId == null) {
            throw new InvalidAuthorDataException("Book ID cannot be null");
        }
        
        if (this.bookIds.add(bookId)) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeBook(Long bookId) {
        if (bookId == null) {
            throw new InvalidAuthorDataException("Book ID cannot be null");
        }
        
        if (this.bookIds.remove(bookId)) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void markAsDeleted(String deletedByKeycloakId) {
        if (canBeDeleted()) {
            this.deleted = true;
            this.updatedAt = LocalDateTime.now();
            this.updatedByKeycloakId = deletedByKeycloakId;
        } else {
            throw new InvalidAuthorDataException("Cannot delete author with associated books");
        }
    }

    // Business rules
    public boolean canBeDeleted() {
        return bookIds.isEmpty();
    }

    public boolean hasBooks() {
        return !bookIds.isEmpty();
    }

    public int getBookCount() {
        return bookIds.size();
    }

    public Set<Long> getBookIds() {
        return new HashSet<>(bookIds);
    }

    private static void validateCreationData(AuthorName name, Biography biography) {
        if (name == null) {
            throw new InvalidAuthorDataException("Author name is required");
        }
        if (biography == null) {
            throw new InvalidAuthorDataException("Biography is required");
        }
    }
}
