package com.library.member.business.kafka.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditEventMessage {

    @JsonProperty("eventId")
    private String eventId;

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("serviceName")
    private String serviceName;

    @JsonProperty("entityType")
    private String entityType;

    @JsonProperty("entityId")
    private String entityId;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("userInfo")
    private String userInfo;

    @JsonProperty("oldValue")
    private String oldValue;

    @JsonProperty("newValue")
    private String newValue;

    @JsonProperty("changes")
    private String changes;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    public boolean isValid() {
        return eventType == null || entityType == null || entityId == null;
    }

    public boolean hasUserInfo() {
        return userId != null;
    }

    public boolean hasChangeInfo() {
        return oldValue != null || newValue != null || changes != null;
    }
}