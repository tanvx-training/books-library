package com.library.book.service;

import com.library.book.dto.request.PublisherCreateDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.book.dto.response.PublisherResponseDTO;
import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;

public interface PublisherService {
    PageResponseDTO<PublisherResponseDTO> getAllPublishers(PageRequestDTO pageRequestDTO);

    PageResponseDTO<BookResponseDTO> getBooksByPublisher(Long publisherId, PageRequestDTO pageRequestDTO);

    PublisherResponseDTO createPublisher(PublisherCreateDTO publisherCreateDTO);
}
