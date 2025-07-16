package com.library.user.domain.event;

import com.library.user.domain.model.shared.DomainEvent;
import com.library.user.domain.model.user.UserId;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Domain event fired when a user is suspended
 */
@Getter
public class UserSuspendedEvent extends DomainEvent {
    
    private final UserId userId;
    private final SuspensionReason reason;
    private final LocalDateTime suspensionTime;
    private final String suspendedBy;
    private final String notes;
    
    public UserSuspendedEvent(UserId userId, SuspensionReason reason, String suspendedBy, String notes) {
        super();
        this.userId = userId;
        this.reason = reason;
        this.suspensionTime = LocalDateTime.now();
        this.suspendedBy = suspendedBy;
        this.notes = notes;
    }
    
    public String getEventType() {
        return "USER_SUSPENDED";
    }
    
    public String getAggregateId() {
        return userId.getValue().toString();
    }
    
    /**
     * Reasons for user suspension
     */
    public enum SuspensionReason {
        OVERDUE_BOOKS("Overdue books"),
        POLICY_VIOLATION("Policy violation"),
        INAPPROPRIATE_BEHAVIOR("Inappropriate behavior"),
        SYSTEM_ABUSE("System abuse"),
        ADMINISTRATIVE("Administrative decision"),
        OTHER("Other");
        
        private final String description;
        
        SuspensionReason(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}