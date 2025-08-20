package com.library.member.service;

import com.library.member.dto.request.CreateUserRequest;
import com.library.member.dto.request.UpdateUserRequest;
import com.library.member.dto.request.UserSearchCriteria;
import com.library.member.dto.response.UserResponse;
import com.library.member.dto.sync.UserSyncRequest;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface UserManagementService {

    UserResponse getCurrentUserProfile();

    UserResponse updateCurrentUserProfile(UpdateUserRequest updateRequest);

    UserResponse createUser(CreateUserRequest createRequest);

    UserResponse getUserByPublicId(UUID publicId);

    UserResponse getUserByKeycloakId(String keycloakId);

    Page<UserResponse> getAllUsers(UserSearchCriteria criteria);

    UserResponse syncUserFromKeycloak(String keycloakId, UserSyncRequest syncRequest);
}