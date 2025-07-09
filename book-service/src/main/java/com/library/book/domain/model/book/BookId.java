package com.library.book.domain.model.book;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class BookId implements Serializable {

    private Long value;

    public BookId(Long value) {
        this.value = value;
    }

    public static BookId createNew() {
        return new BookId(null);
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "new";
    }
}
