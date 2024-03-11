package ru.practicum.shareit.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleValidationNullObjectException(final NullObjectException e) {
        return new ErrorResponse(String.format("ошибка поиска объекта: %s ", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleValidationDataIntegrityViolationException(final DataIntegrityViolationException e) {
        return new ErrorResponse(String.format("ошибка валидации email: %s ", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleValidationOwnerException(final OwnerException e) {
        return new ErrorResponse(String.format("Ошибка валидации владельца: %s ", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationTimeBookingValidationException(final TimeBookingValidationException e) {
        return new ErrorResponse(String.format("Ошибка валидации времени бронирования: %s ", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleValidationSetStatusBookingException(final SetStatusBookingException e) {
        return new ErrorResponse(String.format("Ошибка изменения статуса бронирования: %s ", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleStateException(final StateException e) {
        return new ErrorResponse(String.format(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationNoOneApprovedException(final NoOneApprovedException e) {
        return new ErrorResponse(String.format(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationAvailableException(final AvailableException e) {
        return new ErrorResponse(String.format("Ошибка бронирования вещи: %s ", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationCommentException(final CommentException e) {
        return new ErrorResponse(String.format(e.getMessage()));
    }
}