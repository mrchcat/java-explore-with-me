package com.github.mrchcat.explorewithme.category.mapper;

import com.github.mrchcat.explorewithme.category.dto.CategoryCreateDto;
import com.github.mrchcat.explorewithme.category.dto.CategoryDto;
import com.github.mrchcat.explorewithme.category.model.Category;

public class CategoryMapper {

    public static CategoryDto toDTO(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toEntity(CategoryCreateDto createDto) {
        return Category.builder()
                .name(createDto.getName())
                .build();
    }

    public static Category toEntity(long categoryId, CategoryCreateDto createDto) {
        return Category.builder()
                .id(categoryId)
                .name(createDto.getName())
                .build();
    }
}
