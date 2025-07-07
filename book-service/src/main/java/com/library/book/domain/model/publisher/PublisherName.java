package com.library.book.domain.model.publisher;

import com.library.book.domain.exception.InvalidPublisherDataException;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class PublisherName {
    private String value;

    private PublisherName(String value) {
        this.value = value;
    }

    public static PublisherName of(String name) {
        validate(name);
        return new PublisherName(name);
    }

    private static void validate(String name) {
        if (!StringUtils.hasText(name)) {
            throw new InvalidPublisherDataException("name", "Publisher name cannot be empty");
        }
        if (name.length() > 256) {
            throw new InvalidPublisherDataException("name", "Publisher name cannot exceed 256 characters");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}