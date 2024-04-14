package ru.practicum.shareit.exception;

public class PaginationException extends RuntimeException {
    public PaginationException(final String message) {
        super(message);
    }
}
