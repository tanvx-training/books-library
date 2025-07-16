package com.library.book.domain.model.publisher;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class ContactInfo {
    private final String email;
    private final String phone;
    private final String website;

    public ContactInfo(String email, String phone, String website) {
        this.email = email;
        this.phone = phone;
        this.website = website;
    }

    public static ContactInfo of(String email, String phone, String website) {
        return new ContactInfo(email, phone, website);
    }

    public static ContactInfo empty() {
        return new ContactInfo(null, null, null);
    }

    public boolean hasEmail() {
        return email != null && !email.trim().isEmpty();
    }

    public boolean hasPhone() {
        return phone != null && !phone.trim().isEmpty();
    }

    public boolean hasWebsite() {
        return website != null && !website.trim().isEmpty();
    }

    @Override
    public String toString() {
        return String.format("ContactInfo{email='%s', phone='%s', website='%s'}", email, phone, website);
    }
}