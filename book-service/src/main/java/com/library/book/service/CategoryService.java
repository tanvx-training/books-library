package com.library.book.service;

import com.library.book.dto.request.CategoryCreateDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.book.dto.response.CategoryResponseDTO;
import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryService {

    PageResponseDTO<CategoryResponseDTO> getAllCategories(PageRequestDTO pageRequestDTO);

    PageResponseDTO<BookResponseDTO> getBooksByCategory(Long categoryId, PageRequestDTO pageRequestDTO);

    CategoryResponseDTO createCategory(CategoryCreateDTO categoryCreateDTO);
}
