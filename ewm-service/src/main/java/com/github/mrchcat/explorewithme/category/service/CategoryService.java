package com.github.mrchcat.explorewithme.category.service;

import com.github.mrchcat.explorewithme.category.dto.CategoryCreateDto;
import com.github.mrchcat.explorewithme.category.dto.CategoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    CategoryDto create(CategoryCreateDto createDto);

    void delete(long categoryId);

    CategoryDto update(long categoryId, CategoryCreateDto createDto);

    List<CategoryDto> getAllDto(Pageable pageable);

    CategoryDto getDtoById(long categoryId);

}
