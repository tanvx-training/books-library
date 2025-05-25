package com.library.book.domain.service.impl;

import com.library.book.domain.model.Author;
import com.library.book.domain.service.AuthorService;
import com.library.book.infrastructure.repository.AuthorRepository;
import com.library.book.presentation.dto.request.AuthorCreateDTO;
import com.library.book.presentation.dto.response.AuthorResponseDTO;
import com.library.book.presentation.dto.response.BookResponseDTO;
import com.library.book.util.mapper.AuthorMapper;
import com.library.book.util.mapper.BookMapper;
import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;
import com.library.common.exception.ResourceNotFoundException;
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
    public PageResponseDTO<AuthorResponseDTO> getAllAuthors(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.toPageable();
        Page<AuthorResponseDTO> page = authorRepository.findAllByDeleteFlg(Boolean.FALSE, pageable)
                .map(authorMapper::toDto);
        return new PageResponseDTO<>(page);
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
