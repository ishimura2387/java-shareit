package ru.practicum.shareit.exception;

public class NoOneApprovedException extends RuntimeException {
    public NoOneApprovedException(final String message) {
        super(message);
    }
}
