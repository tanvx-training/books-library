package com.library.book.service.impl;

import com.library.book.model.Publisher;
import com.library.book.service.PublisherService;
import com.library.book.repository.BookRepository;
import com.library.book.repository.PublisherRepository;
import com.library.book.dto.request.PublisherCreateDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.book.dto.response.PublisherResponseDTO;
import com.library.book.utils.mapper.BookMapper;
import com.library.book.utils.mapper.PublisherMapper;
import com.library.common.dto.PaginatedRequest;
import com.library.common.aop.exception.ResourceExistedException;
import com.library.common.aop.exception.ResourceNotFoundException;
import com.library.common.dto.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {

    private final PublisherMapper publisherMapper;

    private final BookMapper bookMapper;

    private final PublisherRepository publisherRepository;

    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<PublisherResponseDTO> getAllPublishers(PaginatedRequest paginatedRequest) {
        Pageable pageable = paginatedRequest.toPageable();
        Page<PublisherResponseDTO> page = publisherRepository.findAllByDeleteFlg(Boolean.FALSE, pageable)
                .map(publisherMapper::toDto);
        return PaginatedResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<BookResponseDTO> getBooksByPublisher(Long publisherId, PaginatedRequest paginatedRequest) {
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher", "id", publisherId));
        Pageable pageable = paginatedRequest.toPageable();
        Page<BookResponseDTO> page = bookRepository.findAllByPublisherAndDeleteFlg(publisher, Boolean.FALSE, pageable)
                .map(bookMapper::toDto);
        return PaginatedResponse.from(page);
    }

    @Override
    @Transactional
    public PublisherResponseDTO createPublisher(PublisherCreateDTO publisherCreateDTO) {
        if (publisherRepository.existsByName(publisherCreateDTO.getName())) {
            throw new ResourceExistedException("Publisher", "name", publisherCreateDTO.getName());
        }
        Publisher publisher = publisherMapper.toEntity(publisherCreateDTO);
        publisherRepository.save(publisher);
        return publisherMapper.toDto(publisher);
    }
}
