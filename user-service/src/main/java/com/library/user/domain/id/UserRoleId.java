package com.library.user.domain.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class UserRoleId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "user_id")
    private Long user;

    @Column(name = "role_id")
    private Long role;
}