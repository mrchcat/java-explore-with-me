package com.github.mrchcat.explorewithme.category.service;

import com.github.mrchcat.explorewithme.category.dto.CategoryCreateDto;
import com.github.mrchcat.explorewithme.category.dto.CategoryDto;
import com.github.mrchcat.explorewithme.category.mapper.CategoryMapper;
import com.github.mrchcat.explorewithme.category.model.Category;
import com.github.mrchcat.explorewithme.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto createCategory(CategoryCreateDto createDto) {
        Category savedCategory = categoryRepository.save(CategoryMapper.toEntity(createDto));
        return CategoryMapper.toDTO(savedCategory);
    }

    @Override
    public void deleteCategory(long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryDto updateCategory(long catId, CategoryCreateDto createDto) {
        Category updatedCategory = categoryRepository.updateById(CategoryMapper.toEntity(catId, createDto));
        return CategoryMapper.toDTO(updatedCategory);
    }
}
