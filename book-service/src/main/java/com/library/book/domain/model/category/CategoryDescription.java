package com.library.book.domain.model.category;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class CategoryDescription {
    private String value;

    private CategoryDescription(String value) {
        this.value = value;
    }

    public static CategoryDescription of(String description) {
        return new CategoryDescription(description);
    }

    public static CategoryDescription empty() {
        return new CategoryDescription(null);
    }

    @Override
    public String toString() {
        return value != null ? value : "";
    }
}