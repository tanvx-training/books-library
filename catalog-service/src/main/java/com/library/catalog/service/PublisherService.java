package com.library.catalog.service;

import com.library.catalog.dto.request.CreatePublisherRequest;
import com.library.catalog.dto.request.PublisherSearchRequest;
import com.library.catalog.dto.request.UpdatePublisherRequest;
import com.library.catalog.dto.response.PagedPublisherResponse;
import com.library.catalog.dto.response.PublisherResponse;

import java.util.UUID;

public interface PublisherService {

    PublisherResponse createPublisher(CreatePublisherRequest request);

    PublisherResponse getPublisherByPublicId(UUID publicId);

    PagedPublisherResponse getAllPublishers(PublisherSearchRequest request);

    PublisherResponse updatePublisher(UUID publicId, UpdatePublisherRequest request);

    void deletePublisher(UUID publicId);
}