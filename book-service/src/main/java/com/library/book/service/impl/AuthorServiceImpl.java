package com.library.book.service.impl;

import com.library.book.model.Author;
import com.library.book.service.AuthorService;
import com.library.book.repository.AuthorRepository;
import com.library.book.dto.request.AuthorCreateDTO;
import com.library.book.dto.response.AuthorResponseDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.book.utils.mapper.AuthorMapper;
import com.library.book.utils.mapper.BookMapper;
import com.library.common.dto.PaginatedRequest;
import com.library.common.aop.exception.ResourceNotFoundException;
import com.library.common.dto.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorMapper authorMapper;

    private final BookMapper bookMapper;

    private final AuthorRepository authorRepository;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<AuthorResponseDTO> getAllAuthors(PaginatedRequest paginatedRequest) {
        Pageable pageable = paginatedRequest.toPageable();
        Page<AuthorResponseDTO> page = authorRepository.findAllByDeleteFlg(Boolean.FALSE, pageable)
                .map(authorMapper::toDto);
        return PaginatedResponse.from(page);
    }

    @Override
    @Transactional
    public AuthorResponseDTO createAuthor(AuthorCreateDTO authorCreateDTO) {
        Author author = authorMapper.toEntity(authorCreateDTO);
        authorRepository.save(author);
        return authorMapper.toDto(author);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDTO> getBooksByAuthor(Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", authorId));
        return author.getBooks()
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
