package com.library.user.domain.model.user;

import com.library.user.domain.exception.InvalidUserDataException;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class Username implements Serializable {
    private String value;

    public Username(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidUserDataException("username", "Username cannot be empty");
        }
        if (value.length() < 3) {
            throw new InvalidUserDataException("username", "Username must be at least 3 characters");
        }
        if (value.length() > 50) {
            throw new InvalidUserDataException("username", "Username cannot exceed 50 characters");
        }
        this.value = value;
    }

    public static Username of(String value) {
        return new Username(value);
    }
}