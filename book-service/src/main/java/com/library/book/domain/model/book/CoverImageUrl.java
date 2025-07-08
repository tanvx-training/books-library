package com.library.book.domain.model.book;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class CoverImageUrl {

    private String value;

    private CoverImageUrl(String value) {
        this.value = value;
    }

    public static CoverImageUrl of(String url) {
        return new CoverImageUrl(url);
    }

    public static CoverImageUrl empty() {
        return new CoverImageUrl(null);
    }

    @Override
    public String toString() {
        return value != null ? value : "";
    }
}
