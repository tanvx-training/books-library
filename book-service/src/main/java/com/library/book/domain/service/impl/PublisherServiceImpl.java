package com.library.book.domain.service.impl;

import com.library.book.domain.model.Publisher;
import com.library.book.domain.service.PublisherService;
import com.library.book.infrastructure.repository.BookRepository;
import com.library.book.infrastructure.repository.PublisherRepository;
import com.library.book.presentation.dto.request.PublisherCreateDTO;
import com.library.book.presentation.dto.response.BookResponseDTO;
import com.library.book.presentation.dto.response.PublisherResponseDTO;
import com.library.book.util.mapper.BookMapper;
import com.library.book.util.mapper.PublisherMapper;
import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;
import com.library.common.exception.ResourceExistedException;
import com.library.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {

    private final PublisherMapper publisherMapper;

    private final BookMapper bookMapper;

    private final PublisherRepository publisherRepository;

    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<PublisherResponseDTO> getAllPublishers(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.toPageable();
        Page<PublisherResponseDTO> page = publisherRepository.findAllByDeleteFlg(Boolean.FALSE, pageable)
                .map(publisherMapper::toDto);
        return new PageResponseDTO<>(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BookResponseDTO> getBooksByPublisher(Long publisherId, PageRequestDTO pageRequestDTO) {
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher", "id", publisherId));
        Pageable pageable = pageRequestDTO.toPageable();
        Page<BookResponseDTO> page = bookRepository.findAllByPublisherAndDeleteFlg(publisher, Boolean.FALSE, pageable)
                .map(bookMapper::toDto);
        return new PageResponseDTO<>(page);
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
