package com.library.book.domain.model.category;

import com.library.book.domain.event.CategoryCreatedEvent;
import com.library.book.domain.event.CategoryUpdatedEvent;
import com.library.book.domain.exception.InvalidCategoryDataException;
import com.library.book.domain.model.shared.AggregateRoot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Category extends AggregateRoot {
    private CategoryId id;
    private CategoryName name;
    private CategorySlug slug;
    private CategoryDescription description;
    private Set<Long> bookIds; // Books in this category
    private CategoryId parentCategoryId; // For hierarchical categories
    private Set<CategoryId> childCategoryIds; // Child categories
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdByKeycloakId;
    private String updatedByKeycloakId;
    private boolean deleted;

    // Factory method
    public static Category create(
            CategoryName name, 
            CategorySlug slug, 
            CategoryDescription description,
            String createdByKeycloakId) {
        
        validateCreationData(name, slug, description);
        
        Category category = new Category();
        category.id = CategoryId.createNew();
        category.name = name;
        category.slug = slug;
        category.description = description;
        category.bookIds = new HashSet<>();
        category.childCategoryIds = new HashSet<>();
        category.createdAt = LocalDateTime.now();
        category.updatedAt = LocalDateTime.now();
        category.createdByKeycloakId = createdByKeycloakId;
        category.deleted = false;

        // Register domain event
        category.registerEvent(new CategoryCreatedEvent(category.id.getValue(), category.name.getValue()));

        return category;
    }

    // Factory method for subcategory
    public static Category createSubcategory(
            CategoryName name,
            CategorySlug slug,
            CategoryDescription description,
            CategoryId parentCategoryId,
            String createdByKeycloakId) {
        
        Category category = create(name, slug, description, createdByKeycloakId);
        category.parentCategoryId = parentCategoryId;
        
        return category;
    }

    // Business methods
    public void updateName(CategoryName newName, String updatedByKeycloakId) {
        if (newName == null) {
            throw new InvalidCategoryDataException("Category name cannot be null");
        }
        
        if (!this.name.equals(newName)) {
            this.name = newName;
            this.updatedAt = LocalDateTime.now();
            this.updatedByKeycloakId = updatedByKeycloakId;
            
            registerEvent(new CategoryUpdatedEvent(this.id.getValue(), "name", newName.getValue()));
        }
    }

    public void updateSlug(CategorySlug newSlug, String updatedByKeycloakId) {
        if (newSlug == null) {
            throw new InvalidCategoryDataException("Category slug cannot be null");
        }
        
        if (!this.slug.equals(newSlug)) {
            this.slug = newSlug;
            this.updatedAt = LocalDateTime.now();
            this.updatedByKeycloakId = updatedByKeycloakId;
            
            registerEvent(new CategoryUpdatedEvent(this.id.getValue(), "slug", newSlug.getValue()));
        }
    }

    public void updateDescription(CategoryDescription newDescription, String updatedByKeycloakId) {
        if (newDescription == null) {
            throw new InvalidCategoryDataException("Category description cannot be null");
        }
        
        if (!this.description.equals(newDescription)) {
            this.description = newDescription;
            this.updatedAt = LocalDateTime.now();
            this.updatedByKeycloakId = updatedByKeycloakId;
            
            registerEvent(new CategoryUpdatedEvent(this.id.getValue(), "description", "Description updated"));
        }
    }

    public void addBook(Long bookId) {
        if (bookId == null) {
            throw new InvalidCategoryDataException("Book ID cannot be null");
        }
        
        if (this.bookIds.add(bookId)) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeBook(Long bookId) {
        if (bookId == null) {
            throw new InvalidCategoryDataException("Book ID cannot be null");
        }
        
        if (this.bookIds.remove(bookId)) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void addChildCategory(CategoryId childCategoryId) {
        if (childCategoryId == null) {
            throw new InvalidCategoryDataException("Child category ID cannot be null");
        }
        
        if (childCategoryId.equals(this.id)) {
            throw new InvalidCategoryDataException("Category cannot be its own child");
        }
        
        if (this.childCategoryIds.add(childCategoryId)) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeChildCategory(CategoryId childCategoryId) {
        if (childCategoryId == null) {
            throw new InvalidCategoryDataException("Child category ID cannot be null");
        }
        
        if (this.childCategoryIds.remove(childCategoryId)) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void markAsDeleted(String deletedByKeycloakId) {
        if (canBeDeleted()) {
            this.deleted = true;
            this.updatedAt = LocalDateTime.now();
            this.updatedByKeycloakId = deletedByKeycloakId;
        } else {
            throw new InvalidCategoryDataException("Cannot delete category with associated books or child categories");
        }
    }

    // Business rules
    public boolean canBeDeleted() {
        return bookIds.isEmpty() && childCategoryIds.isEmpty();
    }

    public boolean hasBooks() {
        return !bookIds.isEmpty();
    }

    public boolean hasChildCategories() {
        return !childCategoryIds.isEmpty();
    }

    public boolean isRootCategory() {
        return parentCategoryId == null;
    }

    public boolean isSubcategory() {
        return parentCategoryId != null;
    }

    public int getBookCount() {
        return bookIds.size();
    }

    public int getChildCategoryCount() {
        return childCategoryIds.size();
    }

    public Set<Long> getBookIds() {
        return new HashSet<>(bookIds);
    }

    public Set<CategoryId> getChildCategoryIds() {
        return new HashSet<>(childCategoryIds);
    }

    private static void validateCreationData(CategoryName name, CategorySlug slug, CategoryDescription description) {
        if (name == null) {
            throw new InvalidCategoryDataException("Category name is required");
        }
        if (slug == null) {
            throw new InvalidCategoryDataException("Category slug is required");
        }
        if (description == null) {
            throw new InvalidCategoryDataException("Category description is required");
        }
    }

    // For JPA/ORM reconstruction
    public static Category reconstitute(
            CategoryId id,
            CategoryName name,
            CategorySlug slug,
            CategoryDescription description,
            Set<Long> bookIds,
            CategoryId parentCategoryId,
            Set<CategoryId> childCategoryIds,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String createdByKeycloakId,
            String updatedByKeycloakId,
            boolean deleted) {
        
        Category category = new Category();
        category.id = id;
        category.name = name;
        category.slug = slug;
        category.description = description;
        category.bookIds = bookIds != null ? new HashSet<>(bookIds) : new HashSet<>();
        category.parentCategoryId = parentCategoryId;
        category.childCategoryIds = childCategoryIds != null ? new HashSet<>(childCategoryIds) : new HashSet<>();
        category.createdAt = createdAt;
        category.updatedAt = updatedAt;
        category.createdByKeycloakId = createdByKeycloakId;
        category.updatedByKeycloakId = updatedByKeycloakId;
        category.deleted = deleted;
        
        return category;
    }
}