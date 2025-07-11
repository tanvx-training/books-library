package com.library.user.domain.model.user;

import com.library.user.domain.exception.InvalidUserDataException;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    private String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String email) {
        validate(email);
        return new Email(email);
    }

    private static void validate(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidUserDataException("email", "Email không được để trống");
        }
        if (email.length() > 100) {
            throw new InvalidUserDataException("email", "Email không được vượt quá 100 ký tự");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidUserDataException("email", "Email không hợp lệ");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}