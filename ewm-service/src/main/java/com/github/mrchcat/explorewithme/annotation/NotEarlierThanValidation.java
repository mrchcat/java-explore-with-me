package com.github.mrchcat.explorewithme.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j

public class NotEarlierThanValidation implements ConstraintValidator<NotEarlierThan, LocalDateTime> {
    private Duration gap;

    @Override
    public void initialize(NotEarlierThan constraintAnnotation) {
        this.gap = Duration.ofHours(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDateTime date, ConstraintValidatorContext constraintValidatorContext) {
        if (date == null) {
            return true;
        }
        return date.isAfter(LocalDateTime.now().plus(gap));
    }
}
