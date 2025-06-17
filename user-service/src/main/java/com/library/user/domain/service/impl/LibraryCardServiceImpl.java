package com.library.user.domain.service.impl;

import com.library.common.exception.ResourceNotFoundException;
import com.library.user.domain.enums.LibraryCardStatus;
import com.library.user.domain.model.LibraryCard;
import com.library.user.domain.model.User;
import com.library.user.domain.service.LibraryCardService;
import com.library.user.infrastructure.repository.LibraryCardRepository;
import com.library.user.infrastructure.repository.UserRepository;
import com.library.user.presentation.dto.request.CreateLibraryCardRequestDTO;
import com.library.user.presentation.dto.response.LibraryCardResponseDTO;
import com.library.user.util.mapper.LibraryCardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LibraryCardServiceImpl implements LibraryCardService {

    private final UserRepository userRepository;

    private final LibraryCardRepository libraryCardRepository;

    private final LibraryCardMapper libraryCardMapper;

    @Override
    @Transactional
    public LibraryCardResponseDTO createLibraryCard(CreateLibraryCardRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", requestDTO.getUserId()));
        LibraryCard libraryCard = new LibraryCard();
        libraryCard.setCardNumber(UUID.randomUUID().toString());
        libraryCard.setIssueDate(LocalDate.now());
        libraryCard.setExpiryDate(requestDTO.getExpiryDate());
        libraryCard.setStatus(LibraryCardStatus.ACTIVE.name());
        libraryCard.setUser(user);
        libraryCardRepository.save(libraryCard);
        return libraryCardMapper.toLibraryCardResponseDTO(libraryCard);
    }
}
