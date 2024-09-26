package com.github.mrchcat.explorewithme.category.mapper;

import com.github.mrchcat.explorewithme.category.dto.CategoryCreateDto;
import com.github.mrchcat.explorewithme.category.dto.CategoryDto;
import com.github.mrchcat.explorewithme.category.model.Category;

import java.util.List;

public class CategoryMapper {

    public static CategoryDto toDTO(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static List<CategoryDto> toDTO(List<Category> categories) {
        return categories.stream().map(CategoryMapper::toDTO).toList();
    }


    public static Category toEntity(CategoryCreateDto createDto) {
        return Category.builder()
                .name(createDto.getName())
                .build();
    }

    public static Category toEntity(Category category, CategoryCreateDto createDto) {
        return Category.builder()
                .id(category.getId())
                .name(createDto.getName())
                .build();
    }
}
