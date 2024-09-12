package com.github.mrchcat.explorewithme.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@Slf4j
@RestControllerAdvice
public class ExceptionsHandler {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.info(ex.getMessage());
        return new ErrorResponse(BAD_REQUEST,
                "Incorrectly made request.",
                ex.getMessage(),
                LocalDateTime.now());
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegrityExceptions(DataIntegrityViolationException ex) {
        log.info(ex.getMessage());
        return new ErrorResponse(CONFLICT,
                "Integrity constraint has been violated.",
                ex.getMessage(),
                LocalDateTime.now());
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(ObjectNotFoundException.class)
    public ErrorResponse handleDataIntegrityExceptions(ObjectNotFoundException ex) {
        log.info(ex.getMessage());
        return new ErrorResponse(NOT_FOUND,
                "The required object was not found.",
                ex.getMessage(),
                LocalDateTime.now());
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleInternalServerException(Exception ex) {
        log.info("{}", ex.getMessage());
        return new ErrorResponse(INTERNAL_SERVER_ERROR,
                "Unrecognized exception",
                Arrays.toString(ex.getStackTrace()),
                LocalDateTime.now());
    }


}

