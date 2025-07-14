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
public class Email implements Serializable {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private String value;

    public Email(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidUserDataException("email", "Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidUserDataException("email", "Invalid email format");
        }
        if (value.length() > 100) {
            throw new InvalidUserDataException("email", "Email cannot exceed 100 characters");
        }
        this.value = value;
    }

    public static Email of(String value) {
        return new Email(value);
    }
}