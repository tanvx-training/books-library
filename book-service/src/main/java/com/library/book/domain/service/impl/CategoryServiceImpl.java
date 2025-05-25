package com.library.book.domain.service.impl;

import com.library.book.domain.service.CategoryService;
import com.library.book.infrastructure.repository.CategoryRepository;
import com.library.book.presentation.dto.response.CategoryResponseDTO;
import com.library.book.util.mapper.CategoryMapper;
import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<CategoryResponseDTO> getAllCategories(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.toPageable();
        Page<CategoryResponseDTO> page = categoryRepository.findAllByDeleteFlg(Boolean.FALSE, pageable)
                .map(categoryMapper::toDto);
        return new PageResponseDTO<>(page);
    }
}
