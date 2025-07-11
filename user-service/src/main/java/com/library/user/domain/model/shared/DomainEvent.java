package com.library.user.domain.model.shared;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class DomainEvent {
    private final LocalDateTime occurredOn;
    
    protected DomainEvent() {
        this.occurredOn = LocalDateTime.now();
    }

}
