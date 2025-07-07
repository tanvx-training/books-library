package com.library.book.domain.model.publisher;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class Address {
    private String value;

    private Address(String value) {
        this.value = value;
    }

    public static Address of(String address) {
        return new Address(address);
    }

    public static Address empty() {
        return new Address(null);
    }

    @Override
    public String toString() {
        return value != null ? value : "";
    }
}