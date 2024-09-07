package com.github.mrchcat.explorewithme.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class ExceptionHandlerClass {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.info(ex.getMessage());
        return new ErrorResponse("Request is not valid", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        log.info("{}", ex.getMessage());
        return new ErrorResponse(ex.getMessage(), null);
    }
}

