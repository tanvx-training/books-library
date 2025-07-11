package com.library.user.domain.model.librarycard;

import com.library.user.domain.exception.InvalidUserDataException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public class CardNumber {
    private final String value;

    private CardNumber(String value) {
        this.value = value;
    }

    public static CardNumber of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidUserDataException("cardNumber", "Card number cannot be empty");
        }
        return new CardNumber(value);
    }

    public static CardNumber generate() {
        return new CardNumber(UUID.randomUUID().toString());
    }
}