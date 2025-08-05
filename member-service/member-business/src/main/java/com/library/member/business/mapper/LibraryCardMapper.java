package com.library.member.business.mapper;

import com.library.member.business.dto.request.CreateLibraryCardRequest;
import com.library.member.business.dto.response.LibraryCardResponse;
import com.library.member.repository.entity.LibraryCardEntity;
import com.library.member.repository.entity.UserEntity;
import com.library.member.repository.enums.LibraryCardStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between LibraryCard entities and DTOs.
 * Handles the transformation of library card data between different layers.
 */
@Component
public class LibraryCardMapper {

    /**
     * Converts a LibraryCardEntity to LibraryCardResponse DTO.
     *
     * @param entity the library card entity to convert
     * @param userPublicId the public ID of the user who owns the card
     * @return the converted LibraryCardResponse DTO
     */
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

    /**
     * Converts a list of LibraryCardEntity objects to LibraryCardResponse DTOs.
     *
     * @param entities the list of library card entities to convert
     * @param userPublicId the public ID of the user who owns the cards
     * @return the list of converted LibraryCardResponse DTOs
     */
    public List<LibraryCardResponse> toResponseList(List<LibraryCardEntity> entities, UUID userPublicId) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(entity -> toResponse(entity, userPublicId))
                .collect(Collectors.toList());
    }

    /**
     * Creates a new LibraryCardEntity from a CreateLibraryCardRequest and UserEntity.
     *
     * @param request the create request containing card details
     * @param user the user entity for whom the card is being created
     * @param cardNumber the generated card number
     * @return the new LibraryCardEntity
     */
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

    /**
     * Updates an existing LibraryCardEntity with new status information.
     *
     * @param entity the entity to update
     * @param newStatus the new status to set
     * @return the updated entity
     */
    public LibraryCardEntity updateStatus(LibraryCardEntity entity, LibraryCardStatus newStatus) {
        if (entity == null) {
            return null;
        }

        entity.setStatus(newStatus);
        return entity;
    }
}