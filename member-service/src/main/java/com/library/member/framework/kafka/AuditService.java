package com.library.member.framework.kafka;

import com.library.member.framework.kafka.EventType;

public interface AuditService {

    void publishCreateEvent(String entityType, String entityId, Object newValue, String userId);

    void publishUpdateEvent(String entityType, String entityId, Object oldValue, Object newValue, String userId);

    void publishDeleteEvent(String entityType, String entityId, Object oldValue, String userId);

    void publishEvent(EventType eventType, String entityType, String entityId,
                      Object oldValue, Object newValue, String userId);
}