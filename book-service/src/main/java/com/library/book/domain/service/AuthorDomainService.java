package com.library.book.domain.service;

import com.library.book.domain.model.author.Author;
import com.library.book.domain.model.author.AuthorName;
import com.library.book.domain.model.author.Biography;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthorDomainService {

    public Author createNewAuthor(String name, String biography) {

        AuthorName authorName = AuthorName.of(name);
        Biography bio = StringUtils.hasText(biography)
                ? Biography.of(biography)
                : Biography.empty();
        return Author.create(authorName, bio);
    }
}
