package com.library.book.domain.model.publisher;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class PublisherId implements Serializable {
    private Long value;

    public PublisherId(Long value) {
        this.value = value;
    }

    public static PublisherId createNew() {
        return new PublisherId(null); // ID sẽ được tạo bởi DB
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "new";
    }
}