package com.library.user.application.mapper;

import com.library.user.application.dto.response.LibraryCardResponse;
import com.library.user.application.dto.response.UserDetailResponse;
import com.library.user.application.dto.response.UserResponse;
import com.library.user.domain.model.librarycard.LibraryCard;
import com.library.user.domain.model.user.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDtoMapper {

    /**
     * Convert domain entity to response DTO
     */
    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId().getValue())
                .username(user.getUsername().getValue())
                .email(user.getEmail().getValue())
                .firstName(user.getFirstName() != null ? user.getFirstName().getValue() : null)
                .lastName(user.getLastName() != null ? user.getLastName().getValue() : null)
                .phone(user.getPhone() != null ? user.getPhone().getValue() : null)
                .active(user.isActive())
                .roles(user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet()))
                .build();
    }

    /**
     * Convert domain entity to detail response DTO without library cards
     */
    public UserDetailResponse toUserDetailResponse(User user) {
        return toUserDetailResponse(user, Collections.emptyList());
    }

    /**
     * Convert domain entity to detail response DTO with library cards
     */
    public UserDetailResponse toUserDetailResponse(User user, List<LibraryCardResponse> libraryCards) {
        return UserDetailResponse.builder()
                .id(user.getId().getValue())
                .username(user.getUsername().getValue())
                .email(user.getEmail().getValue())
                .firstName(user.getFirstName() != null ? user.getFirstName().getValue() : null)
                .lastName(user.getLastName() != null ? user.getLastName().getValue() : null)
                .phone(user.getPhone() != null ? user.getPhone().getValue() : null)
                .active(user.isActive())
                .roles(user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet()))
                .libraryCards(libraryCards)
                .build();
    }

    /**
     * Convert LibraryCard domain entity to LibraryCardResponse
     */
    public LibraryCardResponse toLibraryCardResponse(LibraryCard libraryCard) {
        return LibraryCardResponse.builder()
                .id(libraryCard.getId().getValue())
                .cardNumber(libraryCard.getCardNumber().getValue())
                .issueDate(libraryCard.getIssueDate())
                .expiryDate(libraryCard.getExpiryDate())
                .status(libraryCard.getStatus().name())
                .build();
    }
}