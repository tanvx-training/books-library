package com.library.book.domain.service;

import com.library.book.domain.model.publisher.Address;
import com.library.book.domain.model.publisher.Publisher;
import com.library.book.domain.model.publisher.PublisherName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublisherDomainService {

    public Publisher createNewPublisher(String name, String address, String createdByKeycloakId) {
        PublisherName publisherName = PublisherName.of(name);
        Address publisherAddress = address != null ? Address.of(address) : Address.empty();
        com.library.book.domain.model.publisher.ContactInfo contactInfo = 
            com.library.book.domain.model.publisher.ContactInfo.empty();

        return Publisher.create(publisherName, publisherAddress, contactInfo, null, createdByKeycloakId);
    }
}