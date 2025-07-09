package com.library.book.domain.event;

import com.library.book.domain.model.book.BookId;
import com.library.book.domain.model.shared.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BookCreatedEvent extends DomainEvent {

    private final BookId bookId;
} 