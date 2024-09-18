package com.github.mrchcat.explorewithme.category.service;

import com.github.mrchcat.explorewithme.category.dto.CategoryCreateDto;
import com.github.mrchcat.explorewithme.category.dto.CategoryDto;
import com.github.mrchcat.explorewithme.category.model.Category;

import java.util.List;

public interface CategoryService {

    CategoryDto create(CategoryCreateDto createDto);

    void delete(long categoryId);

    CategoryDto update(long categoryId, CategoryCreateDto createDto);

    List<CategoryDto> getAllDto(long from, long  size);

    CategoryDto getDtoById(long categoryId);

    Category getById(long categoryId);


}
