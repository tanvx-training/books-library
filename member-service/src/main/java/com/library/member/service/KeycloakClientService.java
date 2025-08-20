package com.library.member.service;

import org.keycloak.representations.idm.UserRepresentation;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface KeycloakClientService {

    List<UserRepresentation> getAllUsers();

    List<UserRepresentation> getUpdatedUsers(Instant since);

    Optional<UserRepresentation> getUserById(String keycloakId);

    List<String> getAllUserIds();
}
