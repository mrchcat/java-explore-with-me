package com.github.mrchcat.explorewithme.category.service;

import com.github.mrchcat.explorewithme.category.dto.CategoryCreateDto;
import com.github.mrchcat.explorewithme.category.dto.CategoryDto;
import com.github.mrchcat.explorewithme.category.mapper.CategoryMapper;
import com.github.mrchcat.explorewithme.category.model.Category;
import com.github.mrchcat.explorewithme.category.repository.CategoryRepository;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import com.github.mrchcat.explorewithme.validator.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final Validator validator;

    @Override
    public CategoryDto createCategory(CategoryCreateDto createDto) {
        validator.isCategoryNameUnique(createDto.getName());
        Category savedCategory = categoryRepository.save(CategoryMapper.toEntity(createDto));
        log.info("{} created", savedCategory);
        return CategoryMapper.toDTO(savedCategory);
    }

    @Override
    public void deleteCategory(long catId) {
        validator.isCategoryIdExists(catId);
//        TODO добавить проверку на отсутствие связанных задач
        categoryRepository.deleteById(catId);
        log.info("Category with id={} deleted", catId);
    }

    @Override
    public CategoryDto updateCategory(long catId, CategoryCreateDto createDto) {
        validator.isCategoryIdExists(catId);
        validator.isCategoryNameUniqueExclId(catId, createDto.getName());
        Category updatedCategory = categoryRepository.save(CategoryMapper.toEntity(catId, createDto));
        log.info("Category updated to {}", updatedCategory);
        return CategoryMapper.toDTO(updatedCategory);
    }

    @Override
    public List<CategoryDto> getAllCategories(long from, long size) {
        List<Category> categories = categoryRepository.getAllCategories(from, size);
        return categories.stream().map(CategoryMapper::toDTO).toList();
    }

    @Override
    public CategoryDto getCategoryById(long categoryId) {
        return CategoryMapper.toDTO(getRawCategoryById(categoryId));
    }

    @Override
    public Category getRawCategoryById(long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        return categoryOptional.orElseThrow(() -> {
            String message = String.format("Category with id=%d was not found", categoryId);
            return new ObjectNotFoundException(message);
        });
    }
}
