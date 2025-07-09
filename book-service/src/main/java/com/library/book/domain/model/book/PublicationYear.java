package com.library.book.domain.model.book;

import com.library.book.domain.exception.InvalidBookDataException;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Year;

@Getter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class PublicationYear {

    private Integer value;

    private PublicationYear(Integer value) {
        this.value = value;
    }

    public static PublicationYear of(Integer year) {
        validate(year);
        return new PublicationYear(year);
    }

    public static PublicationYear empty() {
        return new PublicationYear(null);
    }

    private static void validate(Integer year) {
        if (year != null) {
            int currentYear = Year.now().getValue();
            if (year < 1000 || year > currentYear + 2) { // Allow up to 2 years in the future for pre-publications
                throw new InvalidBookDataException("publicationYear", 
                        "Publication year must be between 1000 and " + (currentYear + 2));
            }
        }
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "";
    }
}
