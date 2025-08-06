package com.library.catalog.business;

import com.library.catalog.business.dto.request.CreatePublisherRequest;
import com.library.catalog.business.dto.request.PublisherSearchRequest;
import com.library.catalog.business.dto.request.UpdatePublisherRequest;
import com.library.catalog.business.dto.response.PublisherResponse;
import com.library.catalog.business.dto.response.PagedPublisherResponse;

import java.util.UUID;

public interface PublisherBusiness {

    PublisherResponse createPublisher(CreatePublisherRequest request);

    PublisherResponse getPublisherByPublicId(UUID publicId);

    PagedPublisherResponse getAllPublishers(PublisherSearchRequest request);

    PublisherResponse updatePublisher(UUID publicId, UpdatePublisherRequest request);

    void deletePublisher(UUID publicId);
}