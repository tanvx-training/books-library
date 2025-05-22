package com.library.book.domain.service;

import com.library.book.presentation.dto.response.CategoryResponseDTO;
import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryService {

    PageResponseDTO<CategoryResponseDTO> getAllCategories(PageRequestDTO pageRequestDTO);
}
