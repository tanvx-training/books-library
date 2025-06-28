package com.library.book.utils.mapper;

import com.library.book.model.Category;
import com.library.book.dto.request.CategoryCreateDTO;
import com.library.book.dto.response.CategoryResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponseDTO toDto(Category category);
    Category toEntity(CategoryCreateDTO categoryCreateDTO);
}