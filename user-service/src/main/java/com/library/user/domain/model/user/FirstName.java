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
public class FirstName {
    private String value;

    private FirstName(String value) {
        this.value = value;
    }

    public static FirstName of(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            return new FirstName(null);
        }
        validate(firstName);
        return new FirstName(firstName);
    }

    private static void validate(String firstName) {
        if (firstName.length() > 50) {
            throw new InvalidUserDataException("firstName", "Tên không được vượt quá 50 ký tự");
        }
    }

    @Override
    public String toString() {
        return value != null ? value : "";
    }
}