package com.library.book.domain.model.category;

import com.library.book.domain.exception.InvalidCategoryDataException;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class CategoryName {
    private String value;

    private CategoryName(String value) {
        this.value = value;
    }

    public static CategoryName of(String name) {
        validate(name);
        return new CategoryName(name);
    }

    private static void validate(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidCategoryDataException("name", "Category name cannot be empty");
        }
        if (name.length() > 256) {
            throw new InvalidCategoryDataException("name", "Category name cannot exceed 256 characters");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}