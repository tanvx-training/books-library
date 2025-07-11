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
public class LastName {
    private String value;

    private LastName(String value) {
        this.value = value;
    }

    public static LastName of(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            return new LastName(null);
        }
        validate(lastName);
        return new LastName(lastName);
    }

    private static void validate(String lastName) {
        if (lastName.length() > 50) {
            throw new InvalidUserDataException("lastName", "Họ không được vượt quá 50 ký tự");
        }
    }

    @Override
    public String toString() {
        return value != null ? value : "";
    }
}