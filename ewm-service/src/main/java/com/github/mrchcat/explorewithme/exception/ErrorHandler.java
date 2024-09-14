package com.github.mrchcat.explorewithme.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        String defaultMessage = ex.getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error(ex.getMessage());
        return ErrorResponse.builder()
                .status(BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(defaultMessage)
                .timestamp(LocalDateTime.now())
                .errors(ex.getStackTrace())
                .build();
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(DataIntegrityException.class)
    public ErrorResponse handleDataIntegrityExceptions(DataIntegrityException ex) {
        log.info(ex.getMessage());
        return ErrorResponse.builder()
                .status(CONFLICT)
                .reason("Integrity constraint has been violated.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ex.getStackTrace())
                .build();
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(ObjectNotFoundException.class)
    public ErrorResponse handleObjectNotFoundException(ObjectNotFoundException ex) {
        log.info(ex.getMessage());
        return ErrorResponse.builder()
                .status(NOT_FOUND)
                .reason("The required object was not found.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ex.getStackTrace())
                .build();
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorResponse handleMethodArgumentTypeMismatchExceptions(MethodArgumentTypeMismatchException ex) {
        log.error(ex.getMessage());
        return ErrorResponse.builder()
                .status(BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ex.getStackTrace())
                .build();
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(RulesViolationException.class)
    public ErrorResponse handleRulesViolationExceptions(RulesViolationException ex) {
        log.error(ex.getMessage());
        return ErrorResponse.builder()
                .status(CONFLICT)
                .reason("For the requested operation the conditions are not met")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ex.getStackTrace())
                .build();
    }


    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleOtherExceptions(Exception ex) {
        log.error(ex.toString());
        return ErrorResponse.builder()
                .status(INTERNAL_SERVER_ERROR)
                .reason("Internal error")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ex.getStackTrace())
                .build();
    }
}

