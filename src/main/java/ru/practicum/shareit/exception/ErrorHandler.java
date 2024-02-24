package ru.practicum.shareit.exception;

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
    public ErrorResponse handleValidationSameEmailException(final SameEmailException e) {
        return new ErrorResponse(String.format("ошибка валидации email: %s ", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleValidationOwnerException(final OwnerException e) {
        return new ErrorResponse(String.format("Ошибка валидации владельца: %s ", e.getMessage()));
    }

}