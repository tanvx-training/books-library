package com.library.book.util;

import com.library.book.domain.model.Category;
import com.library.book.presentation.dto.request.CategoryCreateDTO;
import com.library.book.presentation.dto.request.CategoryUpdateDTO;
import com.library.book.presentation.dto.response.CategoryResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    Category toEntity(CategoryCreateDTO dto);

    CategoryResponseDTO toDto(Category entity);

    void updateEntityFromDto(CategoryUpdateDTO dto, @MappingTarget Category entity);
}
