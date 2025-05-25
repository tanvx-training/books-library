package com.library.book.domain.service;

import com.library.book.presentation.dto.request.PublisherCreateDTO;
import com.library.book.presentation.dto.response.BookResponseDTO;
import com.library.book.presentation.dto.response.PublisherResponseDTO;
import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface PublisherService {
    PageResponseDTO<PublisherResponseDTO> getAllPublishers(PageRequestDTO pageRequestDTO);

    PageResponseDTO<BookResponseDTO> getBooksByPublisher(Long publisherId, PageRequestDTO pageRequestDTO);

    PublisherResponseDTO createPublisher(PublisherCreateDTO publisherCreateDTO);
}
