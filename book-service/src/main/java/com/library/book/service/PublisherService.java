package com.library.book.service;

import com.library.book.dto.request.PublisherCreateDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.book.dto.response.PublisherResponseDTO;
import com.library.common.dto.PaginatedRequest;
import com.library.common.dto.PaginatedResponse;

public interface PublisherService {
    PaginatedResponse<PublisherResponseDTO> getAllPublishers(PaginatedRequest paginatedRequest);

    PaginatedResponse<BookResponseDTO> getBooksByPublisher(Long publisherId, PaginatedRequest paginatedRequest);

    PublisherResponseDTO createPublisher(PublisherCreateDTO publisherCreateDTO);
}
