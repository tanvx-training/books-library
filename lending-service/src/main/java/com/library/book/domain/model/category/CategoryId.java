package com.library.book.domain.model.category;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class CategoryId implements Serializable {
    private Long value;

    public CategoryId(Long value) {
        this.value = value;
    }

    public static CategoryId createNew() {
        return new CategoryId(null); // ID sẽ được tạo bởi DB
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "new";
    }
}