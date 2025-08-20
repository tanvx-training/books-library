package com.library.loan.framework.kafka;

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

    @JsonProperty("correlationId")
    private String correlationId;

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("ipAddress")
    private String ipAddress;

    @JsonProperty("userAgent")
    private String userAgent;

    /**
     * Validates if the event message has required fields.
     * @return true if invalid (missing required fields), false if valid
     */
    public boolean isValid() {
        return eventType != null && entityType != null && entityId != null;
    }

    /**
     * Checks if the event has user information.
     * @return true if user information is present
     */
    public boolean hasUserInfo() {
        return userId != null;
    }

    /**
     * Checks if the event has change information.
     * @return true if change information is present
     */
    public boolean hasChangeInfo() {
        return oldValue != null || newValue != null || changes != null;
    }
}