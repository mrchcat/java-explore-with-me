package com.github.mrchcat.explorewithme.category.mapper;

import com.github.mrchcat.explorewithme.category.dto.CategoryDTO;
import com.github.mrchcat.explorewithme.category.model.Category;

public class CategoryMapper {
    public static CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
