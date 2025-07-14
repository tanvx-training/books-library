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
public class LastName implements Serializable {
    private String value;

    public LastName(String value) {
        if (value != null && value.length() > 50) {
            throw new InvalidUserDataException("lastName", "Last name cannot exceed 50 characters");
        }
        this.value = value;
    }

    public static LastName of(String value) {
        return new LastName(value);
    }
}