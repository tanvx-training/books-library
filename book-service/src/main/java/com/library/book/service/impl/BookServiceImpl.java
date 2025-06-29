package com.library.book.service.impl;

import com.library.book.repository.AuthorRepository;
import com.library.book.repository.BookRepository;
import com.library.book.repository.CategoryRepository;
import com.library.book.repository.PublisherRepository;
import com.library.book.service.BookService;
import com.library.book.model.Author;
import com.library.book.model.Book;
import com.library.book.model.Category;
import com.library.book.model.Publisher;
import com.library.book.dto.request.BookCreateDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.book.utils.mapper.BookMapper;
import com.library.common.aop.annotation.Loggable;
import com.library.common.dto.PaginatedRequest;
import com.library.common.aop.exception.ResourceExistedException;
import com.library.common.aop.exception.ResourceNotFoundException;
import com.library.common.dto.PaginatedResponse;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;

    private final BookRepository bookRepository;

    private final PublisherRepository publisherRepository;

    private final AuthorRepository authorRepository;

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "Book",
        logArguments = true,
        logReturnValue = false, // Large collection - don't log full result
        logExecutionTime = true,
        performanceThresholdMs = 1000L,
        messagePrefix = "BOOK_SERVICE_LIST",
        customTags = {"layer=service", "transaction=readonly", "soft_delete_filter=true", "pagination=true"}
    )
    public PaginatedResponse<BookResponseDTO> getAllBooks(PaginatedRequest paginatedRequest) {
        Pageable pageable = paginatedRequest.toPageable();
        Page<BookResponseDTO> page = bookRepository.findAllByDeleteFlg(Boolean.FALSE, pageable)
                .map(bookMapper::toDto);
        return PaginatedResponse.from(page);
    }

    @Override
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.CREATE,
        resourceType = "Book",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 1500L,
        messagePrefix = "BOOK_SERVICE_CREATE",
        customTags = {
            "layer=service", 
            "transaction=write", 
            "business_validation=true", 
            "multi_entity_operation=true",
            "includes_relations=true",
            "isbn_validation=true"
        }
    )
    public BookResponseDTO createBook(BookCreateDTO bookCreateDTO) {
        if (bookRepository.existsByIsbn(bookCreateDTO.getIsbn())) {
            throw new ResourceExistedException("Book", "isbn", bookCreateDTO.getIsbn());
        }
        Publisher publisher = publisherRepository.findById(bookCreateDTO.getPublisherId())
                .orElseThrow(() -> new ResourceNotFoundException("Publisher", "id", bookCreateDTO.getPublisherId()));
        List<Author> authors = authorRepository.findAllById(bookCreateDTO.getAuthors());
        if (!Objects.equals(authors.size(), bookCreateDTO.getAuthors().size())) {
            throw new ResourceNotFoundException("Authors", "ids", bookCreateDTO.getAuthors());
        }
        List<Category> categories = categoryRepository.findAllById(bookCreateDTO.getCategories());
        if (!Objects.equals(categories.size(), bookCreateDTO.getCategories().size())) {
            throw new ResourceNotFoundException("Categories", "ids", bookCreateDTO.getCategories());
        }
        Book book = bookMapper.toEntity(bookCreateDTO);
        book.setPublisher(publisher);
        book.setAuthors(authors);
        book.setCategories(categories);

        book.getBookCopies()
                .forEach(copier -> copier.setBook(book));

        bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.READ,
        resourceType = "Book",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 400L,
        messagePrefix = "BOOK_SERVICE_DETAIL",
        customTags = {"layer=service", "transaction=readonly", "single_entity=true", "includes_mapping=true"}
    )
    public BookResponseDTO getBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));
    }

    @Override
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.SEARCH,
        resourceType = "Book",
        logArguments = true,
        logReturnValue = false, // Don't log search results - can be large
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L, // Search operations can be slower
        messagePrefix = "BOOK_SERVICE_SEARCH",
        customTags = {
            "layer=service", 
            "transaction=readonly", 
            "full_text_search=true",
            "multi_table_join=true",
            "dynamic_criteria=true",
            "specification_pattern=true"
        }
    )
    public PaginatedResponse<BookResponseDTO> searchBooks(String keyword, PaginatedRequest paginatedRequest) {
        Pageable pageable = paginatedRequest.toPageable();
        Page<BookResponseDTO> page = bookRepository.findAll(createSpecification(keyword), pageable)
                .map(bookMapper::toDto);
        return PaginatedResponse.from(page);
    }

    private Specification<Book> createSpecification(String keyword) {
        return (root, query, cb) -> {
            Objects.requireNonNull(query);
            if (!query.getResultType().equals(Long.class)) {
                query.distinct(true);
            }

            String searchKeyword = "%" + keyword.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.like(cb.lower(root.get("title")), searchKeyword));
            predicates.add(cb.like(cb.lower(root.get("isbn")), searchKeyword));

            Join<Object, Object> authorJoin = root.join("authors", JoinType.LEFT);
            predicates.add(cb.like(cb.lower(authorJoin.get("name")), searchKeyword));

            Join<Object, Object> categoryJoin = root.join("categories", JoinType.LEFT);
            predicates.add(cb.like(cb.lower(categoryJoin.get("name")), searchKeyword));

            Join<Object, Object> publisherJoin = root.join("publisher", JoinType.LEFT);
            predicates.add(cb.like(cb.lower(publisherJoin.get("name")), searchKeyword));

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

}
