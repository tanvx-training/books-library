package com.library.user.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

/**
 * Contact information value object
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class ContactInfo implements Serializable {
    
    private final Email primaryEmail;
    private final Email secondaryEmail;
    private final Phone primaryPhone;
    private final Phone secondaryPhone;
    private final Address address;
    
    public static ContactInfo create(String email, String phone) {
        return new ContactInfo(
            Email.of(email),
            null,
            phone != null ? Phone.of(phone) : null,
            null,
            null
        );
    }
    
    public static ContactInfo create(String primaryEmail, String secondaryEmail, 
                                   String primaryPhone, String secondaryPhone) {
        return new ContactInfo(
            Email.of(primaryEmail),
            secondaryEmail != null ? Email.of(secondaryEmail) : null,
            primaryPhone != null ? Phone.of(primaryPhone) : null,
            secondaryPhone != null ? Phone.of(secondaryPhone) : null,
            null
        );
    }
    
    public boolean hasSecondaryContact() {
        return secondaryEmail != null || secondaryPhone != null;
    }
    
    public boolean hasCompleteContactInfo() {
        return primaryEmail != null && primaryPhone != null;
    }
    
    public String getPrimaryContactMethod() {
        if (primaryPhone != null) {
            return "phone";
        } else if (primaryEmail != null) {
            return "email";
        }
        return "none";
    }
}