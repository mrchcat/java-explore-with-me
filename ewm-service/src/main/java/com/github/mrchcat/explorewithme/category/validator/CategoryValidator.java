package com.github.mrchcat.explorewithme.category.validator;

import com.github.mrchcat.explorewithme.category.repository.CategoryRepository;
import com.github.mrchcat.explorewithme.exception.DataIntegrityException;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import com.github.mrchcat.explorewithme.exception.RulesViolationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CategoryValidator {
    private final CategoryRepository categoryRepository;

    public void isCategoryNameUnique(String categoryName) {
        if (categoryRepository.existsByName(categoryName)) {
            String message = String.format("Name=[%s] is not unique for category", categoryName);
            throw new DataIntegrityException(message);
        }
    }

    public void isCategoryIdExists(long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            String message = String.format("Category with id=%d was not found", categoryId);
            throw new ObjectNotFoundException(message);
        }
    }

    public void isCategoryNameUniqueExclId(long categoryId, String categoryName) {
        if (categoryRepository.existsByNameExclId(categoryId, categoryName)) {
            String message = String.format("%s is not unique for category", categoryName);
            throw new RulesViolationException(message);
        }
    }
}
