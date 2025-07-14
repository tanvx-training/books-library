package com.library.book.domain.model.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot {

    private final List<AuditEvent> domainEvents = new ArrayList<>();

    protected void registerEvent(AuditEvent event) {
        domainEvents.add(event);
    }

    public List<AuditEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearEvents() {
        domainEvents.clear();
    }

}
