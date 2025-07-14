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
public class KeycloakId implements Serializable {
    private String value;

    public KeycloakId(String value) {
        this.value = value;
    }

    public static KeycloakId of(String id) {
        return new KeycloakId(id);
    }

    @Override
    public String toString() {
        return value;
    }
}