package com.github.mrchcat.explorewithme.category.service;

import com.github.mrchcat.explorewithme.category.dto.CategoryCreateDto;
import com.github.mrchcat.explorewithme.category.dto.CategoryDto;

public interface CategoryService {

    CategoryDto createCategory(CategoryCreateDto createDto);

    void deleteCategory(long id);

    CategoryDto updateCategory(long categoryId, CategoryCreateDto createDto);
}
