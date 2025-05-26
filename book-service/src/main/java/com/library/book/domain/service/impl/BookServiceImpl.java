package com.library.book.domain.service.impl;

import com.library.book.domain.model.*;
import com.library.book.domain.service.BookService;
import com.library.book.infrastructure.repository.*;
import com.library.book.presentation.dto.request.BookCreateDTO;
import com.library.book.presentation.dto.response.BookResponseDTO;
import com.library.book.util.mapper.BookMapper;
import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;
import com.library.common.exception.ResourceExistedException;
import com.library.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public PageResponseDTO<BookResponseDTO> getAllBooks(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.toPageable();
        Page<BookResponseDTO> page = bookRepository.findAllByDeleteFlg(Boolean.FALSE, pageable)
                .map(bookMapper::toDto);
        return new PageResponseDTO<>(page);
    }

    @Override
    @Transactional
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
}
