package com.library.member.service;

import com.library.member.dto.request.CreateLibraryCardRequest;
import com.library.member.dto.response.LibraryCardResponse;
import com.library.member.repository.LibraryCardEntity;
import com.library.member.repository.UserEntity;
import com.library.member.repository.LibraryCardStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class LibraryCardMapper {

    public LibraryCardResponse toResponse(LibraryCardEntity entity, UUID userPublicId) {
        if (entity == null) {
            return null;
        }

        return LibraryCardResponse.builder()
                .publicId(entity.getPublicId())
                .cardNumber(entity.getCardNumber())
                .userPublicId(userPublicId)
                .issueDate(entity.getIssueDate())
                .expiryDate(entity.getExpiryDate())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public List<LibraryCardResponse> toResponseList(List<LibraryCardEntity> entities, UUID userPublicId) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(entity -> toResponse(entity, userPublicId))
                .collect(Collectors.toList());
    }

    public LibraryCardEntity toEntity(CreateLibraryCardRequest request, UserEntity user, String cardNumber) {
        if (request == null || user == null) {
            return null;
        }

        LibraryCardEntity entity = new LibraryCardEntity();
        entity.setPublicId(UUID.randomUUID());
        entity.setCardNumber(cardNumber);
        entity.setUserId(user.getId());
        entity.setIssueDate(LocalDate.now());
        entity.setExpiryDate(request.getExpiryDate());
        entity.setStatus(LibraryCardStatus.ACTIVE);

        return entity;
    }

    public LibraryCardEntity updateStatus(LibraryCardEntity entity, LibraryCardStatus newStatus) {
        if (entity == null) {
            return null;
        }

        entity.setStatus(newStatus);
        return entity;
    }
}