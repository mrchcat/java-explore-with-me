package com.github.mrchcat.explorewithme.category.service;

import com.github.mrchcat.explorewithme.category.dto.CategoryCreateDto;
import com.github.mrchcat.explorewithme.category.dto.CategoryDto;
import com.github.mrchcat.explorewithme.category.mapper.CategoryMapper;
import com.github.mrchcat.explorewithme.category.model.Category;
import com.github.mrchcat.explorewithme.category.repository.CategoryRepository;
import com.github.mrchcat.explorewithme.category.validator.CategoryValidator;
import com.github.mrchcat.explorewithme.event.validator.EventValidator;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryValidator categoryValidator;
    private final EventValidator eventValidator;

    @Override
    public CategoryDto create(CategoryCreateDto createDto) {
        categoryValidator.isCategoryNameUnique(createDto.getName());
        Category savedCategory = categoryRepository.save(CategoryMapper.toEntity(createDto));
        log.info("{} created", savedCategory);
        return CategoryMapper.toDTO(savedCategory);
    }

    @Override
    public void delete(long catId) {
        categoryValidator.isCategoryIdExists(catId);
        eventValidator.isAnyLinkedEventsForCategory(catId);
        categoryRepository.deleteById(catId);
        log.info("Category with id={} deleted", catId);
    }

    @Override
    public CategoryDto update(long catId, CategoryCreateDto createDto) {
        categoryValidator.isCategoryIdExists(catId);
        categoryValidator.isCategoryNameUniqueExclId(catId, createDto.getName());
        Category updatedCategory = categoryRepository.save(CategoryMapper.toEntity(catId, createDto));
        log.info("Category updated to {}", updatedCategory);
        return CategoryMapper.toDTO(updatedCategory);
    }

    @Override
    public List<CategoryDto> getAllDto(Pageable pageable) {
        List<Category> categories = categoryRepository.findAll(pageable).getContent();
        return CategoryMapper.toDTO(categories);
    }

    @Override
    public CategoryDto getDtoById(long categoryId) {
        return CategoryMapper.toDTO(getById(categoryId));
    }

    @Override
    public Category getById(long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        return categoryOptional.orElseThrow(() -> {
            String message = String.format("Category with id=%d was not found", categoryId);
            return new ObjectNotFoundException(message);
        });
    }
}
