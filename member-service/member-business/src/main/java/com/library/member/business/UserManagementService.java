package com.library.member.business;

import com.library.member.business.dto.request.CreateUserRequest;
import com.library.member.business.dto.request.UpdateUserRequest;
import com.library.member.business.dto.request.UserSearchCriteria;
import com.library.member.business.dto.response.UserResponse;
import com.library.member.business.dto.sync.UserSyncRequest;
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