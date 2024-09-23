package com.github.mrchcat.explorewithme.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {NotEarlierThanValidation.class})
public @interface NotEarlierThan {
    int value();

    String message() default "date is too early";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
