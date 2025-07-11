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
public class Phone {
    private String value;

    private Phone(String value) {
        this.value = value;
    }

    public static Phone of(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return new Phone(null);
        }
        validate(phone);
        return new Phone(phone);
    }

    private static void validate(String phone) {
        if (phone.length() > 20) {
            throw new InvalidUserDataException("phone", "Số điện thoại không được vượt quá 20 ký tự");
        }
    }

    @Override
    public String toString() {
        return value != null ? value : "";
    }
}