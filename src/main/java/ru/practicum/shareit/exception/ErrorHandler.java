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
    public ErrorResponse handleValidationNullObjectException(final NullObjectException e) {
        log.debug("NullObjectException: %s ", e.getMessage());
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
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleValidationOwnerException(final OwnerException e) {
        log.debug("OwnerException: %s ", e.getMessage());
        StackTraceElement[] trace = e.getStackTrace();
        System.err.println(trace[0].toString());
        System.out.println(trace[0].getClass());
        System.out.println(trace[0].getMethodName());
        System.out.println(trace[0].getFileName());
        System.out.println(trace[0].getLineNumber());
        return new ErrorResponse(String.format("Ошибка валидации владельца: %s ", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationTimeBookingValidationException(final TimeBookingValidationException e) {
        log.debug("TimeBookingValidationException: %s ", e.getMessage());
        StackTraceElement[] trace = e.getStackTrace();
        System.err.println(trace[0].toString());
        System.out.println(trace[0].getClass());
        System.out.println(trace[0].getMethodName());
        System.out.println(trace[0].getFileName());
        System.out.println(trace[0].getLineNumber());
        return new ErrorResponse(String.format("Ошибка валидации времени бронирования: %s ", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleValidationSetStatusBookingException(final SetStatusBookingException e) {
        log.debug("SetStatusBookingException: %s ", e.getMessage());
        StackTraceElement[] trace = e.getStackTrace();
        System.err.println(trace[0].toString());
        System.out.println(trace[0].getClass());
        System.out.println(trace[0].getMethodName());
        System.out.println(trace[0].getFileName());
        System.out.println(trace[0].getLineNumber());
        return new ErrorResponse(String.format("Ошибка изменения статуса бронирования: %s ", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleStateException(final StateException e) {
        log.debug("StateException: %s ", e.getMessage());
        StackTraceElement[] trace = e.getStackTrace();
        System.err.println(trace[0].toString());
        System.out.println(trace[0].getClass());
        System.out.println(trace[0].getMethodName());
        System.out.println(trace[0].getFileName());
        System.out.println(trace[0].getLineNumber());
        return new ErrorResponse(String.format(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationNoOneApprovedException(final NoOneApprovedException e) {
        log.debug("NoOneApprovedException: %s ", e.getMessage());
        StackTraceElement[] trace = e.getStackTrace();
        System.err.println(trace[0].toString());
        System.out.println(trace[0].getClass());
        System.out.println(trace[0].getMethodName());
        System.out.println(trace[0].getFileName());
        System.out.println(trace[0].getLineNumber());
        return new ErrorResponse(String.format(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationAvailableException(final AvailableException e) {
        log.debug("AvailableException: %s ", e.getMessage());
        StackTraceElement[] trace = e.getStackTrace();
        System.err.println(trace[0].toString());
        System.out.println(trace[0].getClass());
        System.out.println(trace[0].getMethodName());
        System.out.println(trace[0].getFileName());
        System.out.println(trace[0].getLineNumber());
        return new ErrorResponse(String.format("Ошибка бронирования вещи: %s ", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationCommentException(final CommentException e) {
        log.debug("CommentException: %s ", e.getMessage());
        StackTraceElement[] trace = e.getStackTrace();
        System.err.println(trace[0].toString());
        System.out.println(trace[0].getClass());
        System.out.println(trace[0].getMethodName());
        System.out.println(trace[0].getFileName());
        System.out.println(trace[0].getLineNumber());
        return new ErrorResponse(String.format(e.getMessage()));
    }
}