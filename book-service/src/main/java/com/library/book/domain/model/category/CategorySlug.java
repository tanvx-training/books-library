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
public class CategorySlug {
    private String value;

    private CategorySlug(String value) {
        this.value = value;
    }

    public static CategorySlug of(String slug) {
        validate(slug);
        return new CategorySlug(slug);
    }

    private static void validate(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            throw new InvalidCategoryDataException("slug", "Category slug cannot be empty");
        }
        if (slug.length() > 256) {
            throw new InvalidCategoryDataException("slug", "Category slug cannot exceed 256 characters");
        }
        if (!slug.matches("^[a-z0-9-]+$")) {
            throw new InvalidCategoryDataException("slug", "Category slug can only contain lowercase letters, numbers, and hyphens");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}