package com.library.book.domain.model.bookcopy;

import com.library.book.domain.exception.InvalidBookCopyDataException;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class CopyNumber {
    private String value;
    
    private CopyNumber(String value) {
        this.value = value;
    }
    
    public static CopyNumber of(String copyNumber) {
        validate(copyNumber);
        return new CopyNumber(copyNumber);
    }
    
    private static void validate(String copyNumber) {
        if (copyNumber == null || copyNumber.trim().isEmpty()) {
            throw new InvalidBookCopyDataException("copyNumber", "Copy number cannot be empty");
        }
        if (copyNumber.length() > 20) {
            throw new InvalidBookCopyDataException("copyNumber", "Copy number cannot exceed 20 characters");
        }
    }
    
    @Override
    public String toString() {
        return value;
    }
} 