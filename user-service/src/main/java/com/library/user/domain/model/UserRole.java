package com.library.user.domain.model;

import com.library.user.domain.id.UserRoleId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "user_roles")
public class UserRole implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private UserRoleId id;

    @ManyToOne
    @MapsId("user")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("role")
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}