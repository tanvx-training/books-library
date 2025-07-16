package com.library.user.domain.event;

import com.library.user.domain.model.shared.DomainEvent;
import com.library.user.domain.model.user.User;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Domain event fired when a new user is registered
 */
@Getter
public class UserRegisteredEvent extends DomainEvent {
    
    private final User user;
    private final LocalDateTime registrationTime;
    private final String registrationMethod; // "STANDARD", "KEYCLOAK", "SOCIAL"
    
    public UserRegisteredEvent(User user, String registrationMethod) {
        super();
        this.user = user;
        this.registrationTime = LocalDateTime.now();
        this.registrationMethod = registrationMethod;
    }
    
    public UserRegisteredEvent(User user) {
        this(user, "STANDARD");
    }
    
    public String getEventType() {
        return "USER_REGISTERED";
    }
    
    public String getAggregateId() {
        return user.getId().getValue().toString();
    }
}