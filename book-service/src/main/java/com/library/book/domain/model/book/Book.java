package com.library.book.domain.model.book;

import com.library.book.domain.event.BookCreatedEvent;
import com.library.book.domain.model.author.AuthorId;
import com.library.book.domain.model.category.CategoryId;
import com.library.book.domain.model.publisher.PublisherId;
import com.library.book.domain.model.shared.AggregateRoot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Book extends AggregateRoot {

    private BookId id;
    private BookTitle title;
    private ISBN isbn;
    private PublisherId publisherId;
    private PublicationYear publicationYear;
    private Description description;
    private CoverImageUrl coverImageUrl;
    private List<AuthorId> authorIds = new ArrayList<>();
    private List<CategoryId> categoryIds = new ArrayList<>();
    private boolean deleted;

    public static Book create(
            BookTitle title,
            ISBN isbn,
            PublisherId publisherId,
            PublicationYear publicationYear,
            Description description,
            CoverImageUrl coverImageUrl,
            List<AuthorId> authorIds,
            List<CategoryId> categoryIds
    ) {
        Book book = new Book();
        book.id = BookId.createNew();
        book.title = title;
        book.isbn = isbn;
        book.publisherId = publisherId;
        book.publicationYear = publicationYear;
        book.description = description;
        book.coverImageUrl = coverImageUrl;
        book.authorIds = new ArrayList<>(authorIds);
        book.categoryIds = new ArrayList<>(categoryIds);
        book.deleted = false;

        book.registerEvent(new BookCreatedEvent(book.id.toString(), null));
        return book;
    }

    // Business methods
    public void updateTitle(BookTitle newTitle) {
        this.title = newTitle;
    }

    public void updateISBN(ISBN newIsbn) {
        this.isbn = newIsbn;
    }

    public void updatePublisher(PublisherId newPublisherId) {
        this.publisherId = newPublisherId;
    }

    public void updatePublicationYear(PublicationYear newPublicationYear) {
        this.publicationYear = newPublicationYear;
    }

    public void updateDescription(Description newDescription) {
        this.description = newDescription;
    }

    public void updateCoverImageUrl(CoverImageUrl newCoverImageUrl) {
        this.coverImageUrl = newCoverImageUrl;
    }

    public void updateAuthors(List<AuthorId> newAuthorIds) {
        this.authorIds = new ArrayList<>(newAuthorIds);
    }

    public void updateCategories(List<CategoryId> newCategoryIds) {
        this.categoryIds = new ArrayList<>(newCategoryIds);
    }

    public void markAsDeleted() {
        this.deleted = true;
    }

    public List<AuthorId> getAuthorIds() {
        return Collections.unmodifiableList(authorIds);
    }

    public List<CategoryId> getCategoryIds() {
        return Collections.unmodifiableList(categoryIds);
    }
}
