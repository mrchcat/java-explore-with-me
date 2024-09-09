package com.github.mrchcat.explorewithme.category.controller;

import com.github.mrchcat.explorewithme.category.dto.CategoryCreateDTO;
import com.github.mrchcat.explorewithme.category.dto.CategoryDTO;
import com.github.mrchcat.explorewithme.category.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO createCategory(HttpServletRequest request,
                                      @RequestBody CategoryCreateDTO createDTO) {
        log.info("Admin API: received request to ;Ltybc category {}", createDTO);
        return categoryService.createCategory(createDTO);
    }

}
