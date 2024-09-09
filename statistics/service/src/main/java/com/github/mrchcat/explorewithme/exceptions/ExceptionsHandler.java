package com.github.mrchcat.explorewithme.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;


@Slf4j
@RestControllerAdvice
public class ExceptionsHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());
        log.error(Arrays.toString(ex.getStackTrace()));
        return new ErrorResponse("Request is not valid", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("{}", ex.getMessage());
        log.error(Arrays.toString(ex.getStackTrace()));
        return new ErrorResponse(ex.getMessage(), null);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherException(Exception ex) {
        log.error(ex.getMessage());
        log.error(Arrays.toString(ex.getStackTrace()));
        return new ErrorResponse("Internal server error", ex.getMessage());
    }
}

