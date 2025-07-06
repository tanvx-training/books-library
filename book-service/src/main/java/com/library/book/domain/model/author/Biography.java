package com.library.book.domain.model.author;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class Biography {

    private String value;

    private Biography(String value) {
        this.value = value;
    }

    public static Biography of(String biography) {
        return new Biography(biography);
    }

    public static Biography empty() {
        return new Biography(null);
    }

    @Override
    public String toString() {
        return value != null ? value : "";
    }
}
