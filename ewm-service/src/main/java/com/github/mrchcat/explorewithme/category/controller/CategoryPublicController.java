package com.github.mrchcat.explorewithme.category.controller;

import com.github.mrchcat.explorewithme.category.dto.CategoryDto;
import com.github.mrchcat.explorewithme.category.service.CategoryService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<CategoryDto> getAllCategories(
            @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Positive Integer size) {

        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        log.info("Public API: received request get all categories with parameters from={}, size={}", from, size);
        return categoryService.getAllDto(pageable);
    }

    @GetMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    CategoryDto getCategoryById(@PathVariable(name = "categoryId") long categoryId) {
        log.info("Public API: received request get category with id={}", categoryId);
        return categoryService.getDtoById(categoryId);
    }

}
