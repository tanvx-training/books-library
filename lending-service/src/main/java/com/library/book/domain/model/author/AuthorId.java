package com.library.book.domain.model.author;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class AuthorId implements Serializable {

    private Long value;

    public AuthorId(Long value) {
        this.value = value;
    }

    public static AuthorId createNew() {
        return new AuthorId(null);
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "new";
    }
}
