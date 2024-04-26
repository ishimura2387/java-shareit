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
    public ErrorResponse handleValidationNotFoundException(final NotFoundException e) {
        log.debug("NotFoundException: %s ", e.getMessage());
        StackTraceElement[] trace = e.getStackTrace();
        System.err.println(trace[0].toString());
        System.out.println(trace[0].getClass());
        System.out.println(trace[0].getMethodName());
        System.out.println(trace[0].getFileName());
        System.out.println(trace[0].getLineNumber());
        return new ErrorResponse(String.format("ошибка поиска объекта: %s ", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleValidationDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.debug("DataIntegrityViolationException: %s ", e.getMessage());
        StackTraceElement[] trace = e.getStackTrace();
        System.err.println(trace[0].toString());
        System.out.println(trace[0].getClass());
        System.out.println(trace[0].getMethodName());
        System.out.println(trace[0].getFileName());
        System.out.println(trace[0].getLineNumber());
        return new ErrorResponse(String.format("ошибка валидации email: %s ", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleValidationConstraintViolationException(final ConstraintViolationException e) {
        log.debug("ConstraintViolationException: %s ", e.getMessage());
        StackTraceElement[] trace = e.getStackTrace();
        System.err.println(trace[0].toString());
        System.out.println(trace[0].getClass());
        System.out.println(trace[0].getMethodName());
        System.out.println(trace[0].getFileName());
        System.out.println(trace[0].getLineNumber());
        return new ErrorResponse(String.format("ошибка валидации пагинации: %s ", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        log.debug("IllegalArgumentException: %s ", e.getMessage());
        StackTraceElement[] trace = e.getStackTrace();
        System.err.println(trace[0].toString());
        System.out.println(trace[0].getClass());
        System.out.println(trace[0].getMethodName());
        System.out.println(trace[0].getFileName());
        System.out.println(trace[0].getLineNumber());
        return new ErrorResponse(String.format(e.getMessage()));
    }
}