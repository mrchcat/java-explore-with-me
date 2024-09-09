package com.github.mrchcat.explorewithme.category.service;

import com.github.mrchcat.explorewithme.category.dto.CategoryCreateDTO;
import com.github.mrchcat.explorewithme.category.dto.CategoryDTO;

public interface CategoryService {
    CategoryDTO createCategory(CategoryCreateDTO createDTO);
}
