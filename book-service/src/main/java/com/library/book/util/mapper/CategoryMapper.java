package com.library.book.util.mapper;

import com.library.book.domain.model.Category;
import com.library.book.presentation.dto.request.CategoryCreateDTO;
import com.library.book.presentation.dto.response.CategoryResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponseDTO toDto(Category category);
    Category toEntity(CategoryCreateDTO categoryCreateDTO);
}