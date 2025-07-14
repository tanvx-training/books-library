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
public class FirstName implements Serializable {
    private String value;

    public FirstName(String value) {
        if (value != null && value.length() > 50) {
            throw new InvalidUserDataException("firstName", "First name cannot exceed 50 characters");
        }
        this.value = value;
    }

    public static FirstName of(String value) {
        return new FirstName(value);
    }
}