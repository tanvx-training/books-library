package com.library.book.domain.model.book;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class Description {

    private String value;

    private Description(String value) {
        this.value = value;
    }

    public static Description of(String description) {
        return new Description(description);
    }

    public static Description empty() {
        return new Description(null);
    }

    @Override
    public String toString() {
        return value != null ? value : "";
    }
}
