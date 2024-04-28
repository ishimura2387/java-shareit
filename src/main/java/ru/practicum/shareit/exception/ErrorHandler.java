package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.info("NotFoundException: {}");
        StackTraceElement[] trace = e.getStackTrace();
        System.out.println(trace[0].getClass());
        System.out.println(trace[0].getMethodName());
        System.out.println(trace[0].getFileName());
        System.out.println(trace[0].getLineNumber());
        return new ErrorResponse(String.format(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.info("DataIntegrityViolationException: {}");
        StackTraceElement[] trace = e.getStackTrace();
        System.out.println(trace[0].getClass());
        System.out.println(trace[0].getMethodName());
        System.out.println(trace[0].getFileName());
        System.out.println(trace[0].getLineNumber());
        return new ErrorResponse(String.format(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        log.error("ConstraintViolationException: {}");
        StackTraceElement[] trace = e.getStackTrace();
        System.out.println(trace[0].getClass());
        System.out.println(trace[0].getMethodName());
        System.out.println(trace[0].getFileName());
        System.out.println(trace[0].getLineNumber());
        return new ErrorResponse(String.format(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        log.info("IllegalArgumentException: {}");
        StackTraceElement[] trace = e.getStackTrace();
        System.out.println(trace[0].getClass());
        System.out.println(trace[0].getMethodName());
        System.out.println(trace[0].getFileName());
        System.out.println(trace[0].getLineNumber());
        return new ErrorResponse(String.format(e.getMessage()));
    }
}