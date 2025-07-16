package com.library.book.domain.model.publisher;

import com.library.book.domain.event.PublisherCreatedEvent;
import com.library.book.domain.event.PublisherUpdatedEvent;
import com.library.book.domain.exception.InvalidPublisherDataException;
import com.library.book.domain.model.shared.AggregateRoot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Publisher extends AggregateRoot {
    private PublisherId id;
    private PublisherName name;
    private Address address;
    private ContactInfo contactInfo;
    private Set<Long> bookIds; // Books published by this publisher
    private LocalDateTime establishedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdByKeycloakId;
    private String updatedByKeycloakId;
    private boolean deleted;

    // Factory method
    public static Publisher create(
            PublisherName name, 
            Address address, 
            ContactInfo contactInfo,
            LocalDateTime establishedDate,
            String createdByKeycloakId) {
        
        validateCreationData(name, address);
        
        Publisher publisher = new Publisher();
        publisher.id = PublisherId.createNew();
        publisher.name = name;
        publisher.address = address;
        publisher.contactInfo = contactInfo;
        publisher.bookIds = new HashSet<>();
        publisher.establishedDate = establishedDate;
        publisher.createdAt = LocalDateTime.now();
        publisher.updatedAt = LocalDateTime.now();
        publisher.createdByKeycloakId = createdByKeycloakId;
        publisher.deleted = false;

        // Register domain event
        publisher.registerEvent(new PublisherCreatedEvent(publisher.id.getValue(), publisher.name.getValue()));

        return publisher;
    }

    // Business methods
    public void updateName(PublisherName newName, String updatedByKeycloakId) {
        if (newName == null) {
            throw new InvalidPublisherDataException("Publisher name cannot be null");
        }
        
        if (!this.name.equals(newName)) {
            this.name = newName;
            this.updatedAt = LocalDateTime.now();
            this.updatedByKeycloakId = updatedByKeycloakId;
            
            registerEvent(new PublisherUpdatedEvent(this.id.getValue(), "name", newName.getValue()));
        }
    }

    public void updateAddress(Address newAddress, String updatedByKeycloakId) {
        if (newAddress == null) {
            throw new InvalidPublisherDataException("Address cannot be null");
        }
        
        if (!this.address.equals(newAddress)) {
            this.address = newAddress;
            this.updatedAt = LocalDateTime.now();
            this.updatedByKeycloakId = updatedByKeycloakId;
            
            registerEvent(new PublisherUpdatedEvent(this.id.getValue(), "address", newAddress.getValue()));
        }
    }

    public void updateContactInfo(ContactInfo newContactInfo, String updatedByKeycloakId) {
        if (newContactInfo == null) {
            throw new InvalidPublisherDataException("Contact info cannot be null");
        }
        
        if (!this.contactInfo.equals(newContactInfo)) {
            this.contactInfo = newContactInfo;
            this.updatedAt = LocalDateTime.now();
            this.updatedByKeycloakId = updatedByKeycloakId;
            
            registerEvent(new PublisherUpdatedEvent(this.id.getValue(), "contactInfo", "Contact info updated"));
        }
    }

    public void addBook(Long bookId) {
        if (bookId == null) {
            throw new InvalidPublisherDataException("Book ID cannot be null");
        }
        
        if (this.bookIds.add(bookId)) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeBook(Long bookId) {
        if (bookId == null) {
            throw new InvalidPublisherDataException("Book ID cannot be null");
        }
        
        if (this.bookIds.remove(bookId)) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void markAsDeleted(String deletedByKeycloakId) {
        if (canBeDeleted()) {
            this.deleted = true;
            this.updatedAt = LocalDateTime.now();
            this.updatedByKeycloakId = deletedByKeycloakId;
        } else {
            throw new InvalidPublisherDataException("Cannot delete publisher with associated books");
        }
    }

    // Business validation
    public boolean canBeDeleted() {
        return bookIds.isEmpty();
    }

    public boolean hasBooks() {
        return !bookIds.isEmpty();
    }

    public int getBookCount() {
        return bookIds.size();
    }

    public Set<Long> getBookIds() {
        return new HashSet<>(bookIds);
    }

    public boolean isEstablishedBefore(LocalDateTime date) {
        return establishedDate != null && establishedDate.isBefore(date);
    }

    public boolean isEstablishedAfter(LocalDateTime date) {
        return establishedDate != null && establishedDate.isAfter(date);
    }

    private static void validateCreationData(PublisherName name, Address address) {
        if (name == null) {
            throw new InvalidPublisherDataException("Publisher name is required");
        }
        if (address == null) {
            throw new InvalidPublisherDataException("Publisher address is required");
        }
    }

    // For JPA/ORM reconstruction
    public static Publisher reconstitute(
            PublisherId id,
            PublisherName name,
            Address address,
            ContactInfo contactInfo,
            Set<Long> bookIds,
            LocalDateTime establishedDate,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String createdByKeycloakId,
            String updatedByKeycloakId,
            boolean deleted) {
        
        Publisher publisher = new Publisher();
        publisher.id = id;
        publisher.name = name;
        publisher.address = address;
        publisher.contactInfo = contactInfo;
        publisher.bookIds = bookIds != null ? new HashSet<>(bookIds) : new HashSet<>();
        publisher.establishedDate = establishedDate;
        publisher.createdAt = createdAt;
        publisher.updatedAt = updatedAt;
        publisher.createdByKeycloakId = createdByKeycloakId;
        publisher.updatedByKeycloakId = updatedByKeycloakId;
        publisher.deleted = deleted;
        
        return publisher;
    }
}