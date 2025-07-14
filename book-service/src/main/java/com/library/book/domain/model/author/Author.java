package com.library.book.domain.model.author;

import com.library.book.domain.event.AuthorCreatedEvent;
import com.library.book.domain.model.shared.AggregateRoot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Author extends AggregateRoot {

    private AuthorId id;
    private AuthorName name;
    private Biography biography;
    @Getter
    private boolean deleted;

    public static Author create(AuthorName name, Biography biography) {
        Author author = new Author();
        author.id = AuthorId.createNew();
        author.name = name;
        author.biography = biography;
        author.deleted = false;

        author.registerEvent(new AuthorCreatedEvent());
        return author;
    }

    // Business methods
    public void updateName(AuthorName newName) {
        this.name = newName;
    }

    public void updateBiography(Biography newBiography) {
        this.biography = newBiography;
    }

    public void markAsDeleted() {
        this.deleted = true;
    }

}
