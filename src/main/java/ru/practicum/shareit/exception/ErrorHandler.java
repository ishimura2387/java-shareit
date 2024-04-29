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
        StackTraceElement[] trace = e.getStackTrace();
        log.info("NotFoundException: {}", e.getMessage(), trace);
        return new ErrorResponse(String.format(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        StackTraceElement[] trace = e.getStackTrace();
        log.info("DataIntegrityViolationException: {}", e.getMessage(), trace);
        return new ErrorResponse(String.format(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        StackTraceElement[] trace = e.getStackTrace();
        log.error("ConstraintViolationException: {}", e.getMessage(), trace);
        return new ErrorResponse(String.format(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        StackTraceElement[] trace = e.getStackTrace();
        log.info("IllegalArgumentException: {}", e.getMessage(), trace);
        return new ErrorResponse(String.format(e.getMessage()));

        /*DataIntegrityViolationException которое возникает при валидации входных данных наследуется от Exception.
        Если добавить обработку Exception с кодом ответа, то при возникновении DataIntegrityViolationException будет
        ответ 500! Тогда не проходят постман тесты - нужен код ответа 400.
        И прошу заметить, что errorHandler - это прежние спринты, куда смотрели предыдущие ревьюверы? Почему я
        перелопачиваю все приложение, а не код последнего спринта, который вы проверяете?
        */
    }
}