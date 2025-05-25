package com.library.book.domain.service;

import com.library.book.presentation.dto.request.CategoryCreateDTO;
import com.library.book.presentation.dto.response.BookResponseDTO;
import com.library.book.presentation.dto.response.CategoryResponseDTO;
import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryService {

    PageResponseDTO<CategoryResponseDTO> getAllCategories(PageRequestDTO pageRequestDTO);

    PageResponseDTO<BookResponseDTO> getBooksByCategory(Long categoryId, PageRequestDTO pageRequestDTO);

    CategoryResponseDTO createCategory(CategoryCreateDTO categoryCreateDTO);
}
