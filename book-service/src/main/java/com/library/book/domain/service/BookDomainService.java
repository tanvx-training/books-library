package com.library.book.domain.service;

import com.library.book.domain.model.author.AuthorId;
import com.library.book.domain.model.book.*;
import com.library.book.domain.model.category.CategoryId;
import com.library.book.domain.model.publisher.PublisherId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookDomainService {

    public Book createNewBook(
            String title,
            String isbn,
            Long publisherId,
            Integer publicationYear,
            String description,
            String coverImageUrl,
            List<Long> authorIds,
            List<Long> categoryIds
    ) {
        BookTitle bookTitle = BookTitle.of(title);
        ISBN bookIsbn = ISBN.of(isbn);
        PublisherId bookPublisherId = new PublisherId(publisherId);
        
        PublicationYear bookPublicationYear = publicationYear != null 
                ? PublicationYear.of(publicationYear)
                : PublicationYear.empty();
                
        Description bookDescription = StringUtils.hasText(description)
                ? Description.of(description)
                : Description.empty();
                
        CoverImageUrl bookCoverImageUrl = StringUtils.hasText(coverImageUrl)
                ? CoverImageUrl.of(coverImageUrl)
                : CoverImageUrl.empty();
                
        List<AuthorId> bookAuthorIds = authorIds.stream()
                .map(AuthorId::new)
                .collect(Collectors.toList());
                
        List<CategoryId> bookCategoryIds = categoryIds.stream()
                .map(CategoryId::new)
                .collect(Collectors.toList());

        return Book.create(
                bookTitle,
                bookIsbn,
                bookPublisherId,
                bookPublicationYear,
                bookDescription,
                bookCoverImageUrl,
                bookAuthorIds,
                bookCategoryIds
        );
    }
} 