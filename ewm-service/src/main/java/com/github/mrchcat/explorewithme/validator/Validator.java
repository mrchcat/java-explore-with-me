package com.github.mrchcat.explorewithme.validator;

import com.github.mrchcat.explorewithme.category.repository.CategoryRepository;
import com.github.mrchcat.explorewithme.exception.DataIntegrityException;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Validator {
    private final CategoryRepository categoryRepository;


}
