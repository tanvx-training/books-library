package com.library.book.application.service;

import com.library.book.application.dto.request.PublisherCreateRequest;
import com.library.book.application.dto.response.BookResponse;
import com.library.book.application.dto.response.PublisherResponse;
import com.library.book.application.exception.PublisherApplicationException;
import com.library.book.application.exception.PublisherNotFoundException;
import com.library.book.domain.model.publisher.Publisher;
import com.library.book.domain.model.publisher.PublisherId;
import com.library.book.domain.model.publisher.PublisherName;
import com.library.book.domain.repository.PublisherRepository;
import com.library.book.domain.service.PublisherDomainService;
import com.library.book.repository.BookRepository;
import com.library.book.utils.mapper.BookMapper;
import com.library.common.aop.annotation.Loggable;
import com.library.common.dto.PaginatedRequest;
import com.library.common.dto.PaginatedResponse;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublisherApplicationService {

    private final PublisherRepository publisherRepository;
    private final PublisherDomainService publisherDomainService;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Transactional(readOnly = true)
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.READ,
            resourceType = "Publisher",
            logReturnValue = false,
            performanceThresholdMs = 800L,
            messagePrefix = "PUBLISHER_APP_SERVICE_LIST"
    )
    public PaginatedResponse<PublisherResponse> getAllPublishers(PaginatedRequest paginatedRequest) {
        int page = paginatedRequest.getPage();
        int size = paginatedRequest.getSize();

        Page<PublisherResponse> publisherResponses = publisherRepository.findAll(page, size)
                .map(this::mapToPublisherResponse);

        return PaginatedResponse.from(publisherResponses);
    }

//    @Transactional(readOnly = true)
//    @Loggable(
//            level = LogLevel.DETAILED,
//            operationType = OperationType.READ,
//            resourceType = "Publisher",
//            logReturnValue = false,
//            performanceThresholdMs = 1200L,
//            messagePrefix = "PUBLISHER_APP_SERVICE_BOOKS"
//    )
//    public PaginatedResponse<BookResponse> getBooksByPublisher(Long publisherId, PaginatedRequest paginatedRequest) {
//        Publisher publisher = publisherRepository.findById(new PublisherId(publisherId))
//                .orElseThrow(() -> new PublisherNotFoundException(publisherId));
//
//        // Tạm thời sử dụng BookRepository hiện tại
//        // Trong tương lai, nên migrate Book sang DDD và sử dụng BookRepository domain interface
//        Pageable pageable = PageRequest.of(
//                paginatedRequest.getPage(),
//                paginatedRequest.getSize()
//        );
//
//        Page<BookResponse> page = bookRepository.findAllByPublisherAndDeleteFlg(
//                // Convert từ domain Publisher sang entity Publisher
//                // Đây là giải pháp tạm thời cho đến khi Book được migrate sang DDD
//                convertDomainPublisherToEntity(publisher),
//                false,
//                pageable
//        ).map(bookMapper::toDto);
//
//        return PaginatedResponse.from(page);
//    }


    @Transactional
    @Loggable(
            level = LogLevel.ADVANCED,
            operationType = OperationType.CREATE,
            resourceType = "Publisher",
            performanceThresholdMs = 1500L,
            messagePrefix = "PUBLISHER_APP_SERVICE_CREATE"
    )
    public PublisherResponse createPublisher(PublisherCreateRequest request) {
        try {
            // Check if publisher with the same name already exists
            if (publisherRepository.existsByName(PublisherName.of(request.getName()))) {
                throw new PublisherApplicationException("Publisher with name '" + request.getName() + "' already exists");
            }

            Publisher publisher = publisherDomainService.createNewPublisher(
                    request.getName(),
                    request.getAddress()
            );

            Publisher savedPublisher = publisherRepository.save(publisher);

            // Xử lý domain events nếu cần
            // eventPublisher.publish(savedPublisher.getDomainEvents());

            return mapToPublisherResponse(savedPublisher);
        } catch (Exception e) {
            log.error("Error creating publisher", e);
            throw new PublisherApplicationException("Failed to create publisher", e);
        }
    }

    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.READ,
            resourceType = "Publisher",
            logReturnValue = false,
            messagePrefix = "PUBLISHER_APP_SERVICE_BOOK"
    )
    @Transactional(readOnly = true)
    public PublisherResponse getPublisherById(Long id) {
        return publisherRepository.findById(new PublisherId(id))
                .map(this::mapToPublisherResponse)
                .orElseThrow(() -> new PublisherNotFoundException(id));
    }

    private PublisherResponse mapToPublisherResponse(Publisher publisher) {
        return PublisherResponse.builder()
                .id(publisher.getId().getValue())
                .name(publisher.getName().getValue())
                .address(publisher.getAddress().getValue())
                .build();
    }

    private com.library.book.model.Publisher convertDomainPublisherToEntity(Publisher domainPublisher) {
        com.library.book.model.Publisher entityPublisher = new com.library.book.model.Publisher();
        entityPublisher.setId(domainPublisher.getId().getValue());
        entityPublisher.setName(domainPublisher.getName().getValue());
        entityPublisher.setAddress(domainPublisher.getAddress().getValue());
        return entityPublisher;
    }
}