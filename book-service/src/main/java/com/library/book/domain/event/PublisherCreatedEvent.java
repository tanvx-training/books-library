package com.library.book.domain.event;

import com.library.book.domain.model.shared.AuditEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PublisherCreatedEvent extends AuditEvent {
}