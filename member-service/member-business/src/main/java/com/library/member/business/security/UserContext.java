package com.library.member.business.security;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserContext {

    private String userId;
    private String email;
    private Set<String> roles;
}