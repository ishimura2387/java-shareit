package ru.practicum.shareit.exception;

public class NullObjectException extends RuntimeException {
    public NullObjectException(final String message) {
        super(message);
    }
}