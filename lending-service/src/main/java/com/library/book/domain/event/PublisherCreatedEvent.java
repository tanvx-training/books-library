package com.library.book.domain.event;

import com.library.book.domain.model.publisher.PublisherId;
import com.library.book.domain.model.shared.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PublisherCreatedEvent extends DomainEvent {
    private final PublisherId publisherId;
}