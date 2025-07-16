package com.library.book.application.service;

import com.library.book.application.dto.request.PublisherCreateRequest;
import com.library.book.application.dto.response.PaginatedResponse;
import com.library.book.application.dto.response.PublisherResponse;
import com.library.book.application.exception.PublisherApplicationException;
import com.library.book.application.exception.PublisherNotFoundException;
import com.library.book.domain.exception.AuthorDomainException;
import com.library.book.domain.exception.InvalidPublisherDataException;
import com.library.book.domain.model.publisher.Publisher;
import com.library.book.domain.model.publisher.PublisherId;
import com.library.book.domain.model.publisher.PublisherName;
import com.library.book.domain.repository.PublisherRepository;
import com.library.book.domain.service.PublisherDomainService;
import com.library.book.infrastructure.enums.LogLevel;
import com.library.book.infrastructure.enums.OperationType;
import com.library.book.infrastructure.logging.Loggable;
import com.library.book.application.dto.request.PaginatedRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublisherApplicationService {

    private final PublisherRepository publisherRepository;
    private final PublisherDomainService publisherDomainService;

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
                    request.getAddress(),
                    "SYSTEM" // TODO: Add user context
            );

            Publisher savedPublisher = publisherRepository.save(publisher);

            // Xử lý domain events nếu cần
            // eventPublisher.publish(savedPublisher.getDomainEvents());

            return mapToPublisherResponse(savedPublisher);
        } catch (InvalidPublisherDataException e) {
            log.error("Invalid publisher data: {}", e.getMessage());
            throw e; // Rethrow để được xử lý bởi exception handler
        } catch (AuthorDomainException e) {
            log.error("Domain exception when creating publisher: {}", e.getMessage());
            throw e;
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
}