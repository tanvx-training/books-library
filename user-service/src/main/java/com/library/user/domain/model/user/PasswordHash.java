package com.library.user.domain.model.user;

import com.library.user.domain.exception.InvalidUserDataException;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class PasswordHash {
    private String value;

    private PasswordHash(String value) {
        this.value = value;
    }

    public static PasswordHash of(String passwordHash) {
        validate(passwordHash);
        return new PasswordHash(passwordHash);
    }

    private static void validate(String passwordHash) {
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new InvalidUserDataException("password", "Password không được để trống");
        }
    }

    @Override
    public String toString() {
        return "[PROTECTED]";
    }
}