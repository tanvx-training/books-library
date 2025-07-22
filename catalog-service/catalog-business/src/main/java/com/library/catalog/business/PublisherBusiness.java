package com.library.catalog.business;

import com.library.catalog.business.dto.request.CreatePublisherRequest;
import com.library.catalog.business.dto.request.UpdatePublisherRequest;
import com.library.catalog.business.dto.response.PublisherResponse;
import com.library.catalog.business.dto.response.PagedPublisherResponse;
import org.springframework.data.domain.Pageable;

public interface PublisherBusiness {

    PublisherResponse createPublisher(CreatePublisherRequest request, String currentUser);

    PublisherResponse getPublisherById(Integer id);

    PagedPublisherResponse getAllPublishers(Pageable pageable);

    PagedPublisherResponse searchPublishersByName(String name, Pageable pageable);

    PublisherResponse updatePublisher(Integer id, UpdatePublisherRequest request, String currentUser);

    void deletePublisher(Integer id, String currentUser);
}