package com.github.mrchcat.explorewithme.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;


@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiError handleValidationExceptions(MethodArgumentNotValidException ex) {
        String defaultMessage = ex.getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error(ex.getMessage());
        return ApiError.builder()
                .status(BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(defaultMessage)
                .timestamp(LocalDateTime.now())
                .errors(ex.getStackTrace())
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiError handleMissingServletRequestParameterException(Exception ex) {
        log.error(ex.toString());
        return ApiError.builder()
                .status(BAD_REQUEST)
                .reason("Missing arguments")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ex.getStackTrace())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class, ArgumentNotValidException.class})
    public ApiError handleArgumentException(Exception ex) {
        log.error(ex.getMessage());
        return ApiError.builder()
                .status(BAD_REQUEST)
                .reason("Incorrect arguments")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ex.getStackTrace())
                .build();
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiError handleOtherExceptions(Exception ex) {
        log.error(ex.toString());
        return ApiError.builder()
                .status(INTERNAL_SERVER_ERROR)
                .reason("Internal error")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ex.getStackTrace())
                .build();
    }
}

