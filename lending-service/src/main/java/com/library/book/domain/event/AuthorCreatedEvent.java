package com.library.book.domain.event;

import com.library.book.domain.model.author.AuthorId;
import com.library.book.domain.model.shared.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthorCreatedEvent extends DomainEvent {

    private final AuthorId authorId;
}
