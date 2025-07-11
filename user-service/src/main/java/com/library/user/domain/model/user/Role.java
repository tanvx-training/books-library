package com.library.user.domain.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Role {
    private Long id;
    private String name;

    private Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Role of(String name) {
        return new Role(null, name);
    }

    public static Role of(Long id, String name) {
        return new Role(id, name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;
        return name.equals(role.name);
    }

    @Override
    public String toString() {
        return name;
    }
}