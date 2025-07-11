package com.library.user.domain.repository;

import com.library.user.domain.model.librarycard.LibraryCard;
import com.library.user.domain.model.librarycard.LibraryCardId;
import com.library.user.domain.model.user.UserId;

import java.util.List;
import java.util.Optional;

public interface LibraryCardRepository {
    LibraryCard save(LibraryCard libraryCard);
    Optional<LibraryCard> findById(LibraryCardId id);
    List<LibraryCard> findByUserId(UserId userId);
    void delete(LibraryCard libraryCard);
}