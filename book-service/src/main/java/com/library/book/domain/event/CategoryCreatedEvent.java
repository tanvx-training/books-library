package com.library.book.domain.event;

import com.library.book.domain.model.category.CategoryId;
import com.library.book.domain.model.shared.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CategoryCreatedEvent extends DomainEvent {
    private final CategoryId categoryId;
}