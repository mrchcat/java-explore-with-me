package com.github.mrchcat.explorewithme.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.PrintWriter;
import java.io.StringWriter;
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
    public ApiError handleValidationExceptions(MethodArgumentNotValidException ex) {
        String defaultMessage = ex.getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String errors = sw.toString();
        log.info(errors);
        return ApiError.builder()
                .status(BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(defaultMessage)
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
    }


    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ArgumentNotValidException.class)
    public ApiError handleArgumentNotValidException(ArgumentNotValidException ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String errors = sw.toString();
        log.info(errors);
        return ApiError.builder()
                .status(BAD_REQUEST)
                .reason("Arquments are not valid.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
    }


    @ResponseStatus(CONFLICT)
    @ExceptionHandler(DataIntegrityException.class)
    public ApiError handleDataIntegrityExceptions(DataIntegrityException ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String errors = sw.toString();
        log.info(errors);
        return ApiError.builder()
                .status(CONFLICT)
                .reason("Integrity constraint has been violated.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiError handleDataIntegrityExceptions(DataIntegrityViolationException ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String errors = sw.toString();
        log.info(errors);
        return ApiError.builder()
                .status(CONFLICT)
                .reason("Integrity constraint has been violated.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
    }


    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ApiError handleNotFoundException(NotFoundException ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String errors = sw.toString();
        log.info(errors);
        return ApiError.builder()
                .status(NOT_FOUND)
                .reason("The required object was not found.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiError handleMethodArgumentTypeMismatchExceptions(MethodArgumentTypeMismatchException ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String errors = sw.toString();
        log.info(errors);
        return ApiError.builder()
                .status(BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(RulesViolationException.class)
    public ApiError handleRulesViolationExceptions(RulesViolationException ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String errors = sw.toString();
        log.info(errors);
        return ApiError.builder()
                .status(CONFLICT)
                .reason("For the requested operation the conditions are not met")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiError handleMissingServletRequestParameterException(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String errors = sw.toString();
        log.info(errors);
        return ApiError.builder()
                .status(BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiError handleOtherExceptions(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String errors = sw.toString();
        log.error(errors);
        return ApiError.builder()
                .status(INTERNAL_SERVER_ERROR)
                .reason("Internal error")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
    }
}

