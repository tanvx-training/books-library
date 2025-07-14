package com.library.user.domain.model.user;

import com.library.user.domain.exception.InvalidUserDataException;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.regex.Pattern;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class Phone implements Serializable {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");
    private String value;

    public Phone(String value) {
        if (value != null && !value.trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(value).matches()) {
                throw new InvalidUserDataException("phone", "Invalid phone number format");
            }
            if (value.length() > 20) {
                throw new InvalidUserDataException("phone", "Phone number cannot exceed 20 characters");
            }
        }
        this.value = value;
    }

    public static Phone of(String value) {
        return new Phone(value);
    }
}