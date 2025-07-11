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
public class Username {
    private String value;

    private Username(String value) {
        this.value = value;
    }

    public static Username of(String username) {
        validate(username);
        return new Username(username);
    }

    private static void validate(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidUserDataException("username", "Username không được để trống");
        }
        if (username.length() < 3) {
            throw new InvalidUserDataException("username", "Username phải có ít nhất 3 ký tự");
        }
        if (username.length() > 50) {
            throw new InvalidUserDataException("username", "Username không được vượt quá 50 ký tự");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}