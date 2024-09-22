package com.github.mrchcat.explorewithme.category.service;

import com.github.mrchcat.explorewithme.category.dto.CategoryCreateDto;
import com.github.mrchcat.explorewithme.category.dto.CategoryDto;
import com.github.mrchcat.explorewithme.category.mapper.CategoryMapper;
import com.github.mrchcat.explorewithme.category.model.Category;
import com.github.mrchcat.explorewithme.category.repository.CategoryRepository;
import com.github.mrchcat.explorewithme.event.repository.EventRepository;
import com.github.mrchcat.explorewithme.exception.DataIntegrityException;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import com.github.mrchcat.explorewithme.exception.RulesViolationException;
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
    private final EventRepository eventRepository;

    @Override
    public CategoryDto create(CategoryCreateDto createDto) {
        isCategoryNameUnique(createDto.getName());
        Category savedCategory = categoryRepository.save(CategoryMapper.toEntity(createDto));
        log.info("{} created", savedCategory);
        return CategoryMapper.toDTO(savedCategory);
    }

    @Override
    public void delete(long categoryId) {
        isAnyLinkedEventsForCategory(categoryId);
        categoryRepository.deleteById(categoryId);
        log.info("Category with id={} deleted", categoryId);
    }

    @Override
    public CategoryDto update(long categoryId, CategoryCreateDto createDto) {
        isCategoryNameUniqueExclId(categoryId, createDto.getName());
        Category oldCategory = getById(categoryId);
        Category mapped = CategoryMapper.toEntity(oldCategory, createDto);
        Category updatedCategory = categoryRepository.save(mapped);
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

    public void isCategoryNameUnique(String categoryName) {
        if (categoryRepository.existsByName(categoryName)) {
            String message = String.format("Name=[%s] is not unique for category", categoryName);
            throw new DataIntegrityException(message);
        }
    }

    private void isCategoryNameUniqueExclId(long categoryId, String categoryName) {
        if (categoryRepository.existsByNameExclId(categoryId, categoryName)) {
            String message = String.format("%s is not unique for category", categoryName);
            throw new RulesViolationException(message);
        }
    }

    public void isAnyLinkedEventsForCategory(long categoryId) {
        if (eventRepository.existsByCategory(categoryId)) {
            String message = String.format("Category id=%d have connected events", categoryId);
            throw new RulesViolationException(message);
        }
    }

}
