package com.library.book.service;

import com.library.book.dto.request.CategoryCreateDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.book.dto.response.CategoryResponseDTO;
import com.library.common.dto.PaginatedRequest;
import com.library.common.dto.PaginatedResponse;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryService {

    PaginatedResponse<CategoryResponseDTO> getAllCategories(PaginatedRequest paginatedRequest);

    PaginatedResponse<BookResponseDTO> getBooksByCategory(Long categoryId, PaginatedRequest paginatedRequest);

    CategoryResponseDTO createCategory(CategoryCreateDTO categoryCreateDTO);
}
