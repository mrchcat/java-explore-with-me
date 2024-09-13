package com.github.mrchcat.explorewithme.category.service;

import com.github.mrchcat.explorewithme.category.dto.CategoryCreateDto;
import com.github.mrchcat.explorewithme.category.dto.CategoryDto;
import com.github.mrchcat.explorewithme.category.mapper.CategoryMapper;
import com.github.mrchcat.explorewithme.category.model.Category;
import com.github.mrchcat.explorewithme.category.repository.CategoryRepository;
import com.github.mrchcat.explorewithme.exception.DataIntegrityException;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
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
        isNameUnique(createDto.getName());
        Category savedCategory = categoryRepository.save(CategoryMapper.toEntity(createDto));
        log.info("{} created", savedCategory);
        return CategoryMapper.toDTO(savedCategory);
    }

    @Override
    public void deleteCategory(long catId) {
        isIdExists(catId);
//        TODO добавить проверку на отсутствие связанных задач
        categoryRepository.deleteById(catId);
        log.info("Category with id={} deleted", catId);
    }

    @Override
    public CategoryDto updateCategory(long catId, CategoryCreateDto createDto) {
        isIdExists(catId);
        isNameUniqueExclId(catId, createDto.getName());
        Category updatedCategory = categoryRepository.save(CategoryMapper.toEntity(catId, createDto));
        log.info("Category updated to {}", updatedCategory);
        return CategoryMapper.toDTO(updatedCategory);
    }

    private void isNameUnique(String name) {
        if (categoryRepository.existsByName(name)) {
            String message = String.format("Name=[%s] is not unique for category", name);
            throw new DataIntegrityException(message);
        }
    }

    private void isIdExists(long id) {
        if (!categoryRepository.existsById(id)) {
            String message = String.format("Category with id=%d was not found", id);
            throw new ObjectNotFoundException(message);
        }
    }

    private void isNameUniqueExclId(long id, String name) {
        if (categoryRepository.existsByNameExclId(id, name)) {
            String message = String.format("%s is not unique for category", name);
            throw new ObjectNotFoundException(message);
        }
    }

}
