package com.library.book.domain.model.publisher;

import com.library.book.domain.event.PublisherCreatedEvent;
import com.library.book.domain.model.shared.AggregateRoot;
import lombok.Getter;

@Getter
public class Publisher extends AggregateRoot {
    private PublisherId id;
    private PublisherName name;
    private Address address;
    @Getter
    private boolean deleted;

    // Private constructor for factory method
    private Publisher() {}

    // Factory method
    public static Publisher create(PublisherName name, Address address) {
        Publisher publisher = new Publisher();
        publisher.id = PublisherId.createNew();
        publisher.name = name;
        publisher.address = address;
        publisher.deleted = false;

        // Register domain event
        publisher.registerEvent(new PublisherCreatedEvent());

        return publisher;
    }

    // Business methods
    public void updateName(PublisherName newName) {
        this.name = newName;
    }

    public void updateAddress(Address newAddress) {
        this.address = newAddress;
    }

    public void markAsDeleted() {
        this.deleted = true;
    }

    // Business validation
    public boolean canBeDeleted() {
        // Trong thực tế, có thể kiểm tra xem publisher có sách nào không
        // Nếu có, không cho phép xóa
        return true;
    }
}