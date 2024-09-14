package com.github.mrchcat.explorewithme.category.service;

import com.github.mrchcat.explorewithme.category.dto.CategoryCreateDto;
import com.github.mrchcat.explorewithme.category.dto.CategoryDto;
import com.github.mrchcat.explorewithme.category.model.Category;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CategoryCreateDto createDto);

    void deleteCategory(long categoryId);

    CategoryDto updateCategory(long categoryId, CategoryCreateDto createDto);

    List<CategoryDto> getAllCategories(long from, long  size);

    CategoryDto getCategoryById(long categoryId);

    Category getRawCategoryById(long categoryId);


}
