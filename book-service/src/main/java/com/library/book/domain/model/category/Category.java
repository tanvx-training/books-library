package com.library.book.domain.model.category;

import com.library.book.domain.event.CategoryCreatedEvent;
import com.library.book.domain.model.shared.AggregateRoot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Category extends AggregateRoot {
    private CategoryId id;
    private CategoryName name;
    private CategorySlug slug;
    private CategoryDescription description;
    private boolean deleted;

    // Factory method
    public static Category create(CategoryName name, CategorySlug slug, CategoryDescription description) {
        Category category = new Category();
        category.id = CategoryId.createNew();
        category.name = name;
        category.slug = slug;
        category.description = description;
        category.deleted = false;

        // Register domain event
        category.registerEvent(new CategoryCreatedEvent(category.id));

        return category;
    }

    // Business methods
    public void updateName(CategoryName newName) {
        this.name = newName;
    }

    public void updateSlug(CategorySlug newSlug) {
        this.slug = newSlug;
    }

    public void updateDescription(CategoryDescription newDescription) {
        this.description = newDescription;
    }

    public void markAsDeleted() {
        this.deleted = true;
    }
}