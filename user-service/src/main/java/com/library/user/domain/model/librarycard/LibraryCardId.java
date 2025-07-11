package com.library.user.domain.model.librarycard;

import com.library.user.domain.exception.InvalidUserDataException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class LibraryCardId {
    private final Long value;

    private LibraryCardId(Long value) {
        this.value = value;
    }

    public static LibraryCardId of(Long value) {
        if (value == null) {
            throw new InvalidUserDataException("libraryCardId", "Library card ID cannot be null");
        }
        return new LibraryCardId(value);
    }

    public static LibraryCardId createNew() {
        return new LibraryCardId(null); // ID will be assigned by persistence
    }
}