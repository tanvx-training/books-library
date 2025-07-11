package com.library.user.domain.model.user;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class UserId implements Serializable {
    private Long value;

    public UserId(Long value) {
        this.value = value;
    }

    public static UserId createNew() {
        return new UserId(null); // ID sẽ được tạo bởi DB
    }

    public static UserId of(Long id) {
        return new UserId(id);
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "new";
    }
}