package com.github.mrchcat.explorewithme.category.service;

import com.github.mrchcat.explorewithme.category.dto.CategoryCreateDto;
import com.github.mrchcat.explorewithme.category.dto.CategoryDto;
import com.github.mrchcat.explorewithme.category.mapper.CategoryMapper;
import com.github.mrchcat.explorewithme.category.model.Category;
import com.github.mrchcat.explorewithme.category.repository.CategoryRepository;
import com.github.mrchcat.explorewithme.event.repository.EventRepository;
import com.github.mrchcat.explorewithme.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Category savedCategory = categoryRepository.save(CategoryMapper.toEntity(createDto));
        log.info("{} created", savedCategory);
        return CategoryMapper.toDTO(savedCategory);
    }

    @Override
    public void delete(long categoryId) {
        categoryRepository.deleteById(categoryId);
        log.info("Category with id={} deleted", categoryId);
    }

    @Transactional
    @Override
    public CategoryDto update(long categoryId, CategoryCreateDto createDto) {
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
            String message = "Category with id=" + categoryId + " was not found";
            return new NotFoundException(message);
        });
    }
}
