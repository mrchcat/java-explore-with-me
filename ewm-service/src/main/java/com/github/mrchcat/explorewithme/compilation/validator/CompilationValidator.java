package com.github.mrchcat.explorewithme.compilation.validator;

import com.github.mrchcat.explorewithme.compilation.repository.CompilationRepository;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CompilationValidator {
    private final CompilationRepository compilationRepository;

    public void isCompilationExist(long compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            String message = String.format("Compilation with id=%d was not found", compilationId);
            throw new ObjectNotFoundException(message);
        }
    }
}
