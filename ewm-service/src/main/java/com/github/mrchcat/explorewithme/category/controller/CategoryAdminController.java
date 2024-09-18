package com.github.mrchcat.explorewithme.category.controller;

import com.github.mrchcat.explorewithme.category.dto.CategoryCreateDto;
import com.github.mrchcat.explorewithme.category.dto.CategoryDto;
import com.github.mrchcat.explorewithme.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid CategoryCreateDto createDto) {
        log.info("Admin API: received request to create category {}", createDto);
        return categoryService.create(createDto);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(value = "categoryId") long categoryId) {
        log.info("Admin API: received request to delete category id={}", categoryId);
        categoryService.delete(categoryId);
    }

    @PatchMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable(value = "categoryId") long categoryId,
                                      @RequestBody @Valid CategoryCreateDto updateDto) {
        log.info("Admin API: received request to update category id={} by {}", categoryId, updateDto);
        return categoryService.update(categoryId, updateDto);
    }
}
