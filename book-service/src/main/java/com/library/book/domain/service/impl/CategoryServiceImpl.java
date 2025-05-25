package com.library.book.domain.service.impl;

import com.library.book.domain.model.Category;
import com.library.book.domain.service.CategoryService;
import com.library.book.infrastructure.repository.BookRepository;
import com.library.book.infrastructure.repository.CategoryRepository;
import com.library.book.presentation.dto.request.CategoryCreateDTO;
import com.library.book.presentation.dto.response.BookResponseDTO;
import com.library.book.presentation.dto.response.CategoryResponseDTO;
import com.library.book.util.mapper.BookMapper;
import com.library.book.util.mapper.CategoryMapper;
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

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<CategoryResponseDTO> getAllCategories(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.toPageable();
        Page<CategoryResponseDTO> page = categoryRepository.findAllByDeleteFlg(Boolean.FALSE, pageable)
                .map(categoryMapper::toDto);
        return new PageResponseDTO<>(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BookResponseDTO> getBooksByCategory(Long categoryId, PageRequestDTO pageRequestDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        Pageable pageable = pageRequestDTO.toPageable();
        Page<BookResponseDTO> page = bookRepository.findAllByCategories(List.of(category), pageable)
                .map(bookMapper::toDto);
        return new PageResponseDTO<>(page);
    }

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CategoryCreateDTO categoryCreateDTO) {
        if (categoryRepository.existsByNameOrSlug(categoryCreateDTO.getName(), categoryCreateDTO.getSlug())) {
            throw new ResourceExistedException("Category", "name/slug", categoryCreateDTO.getName() + "/" + categoryCreateDTO.getSlug());
        }
        Category category = categoryMapper.toEntity(categoryCreateDTO);
        categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }
}
