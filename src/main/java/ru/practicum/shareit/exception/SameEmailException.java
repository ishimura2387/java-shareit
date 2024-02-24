package ru.practicum.shareit.exception;

public class SameEmailException extends RuntimeException {
    public SameEmailException(final String message) {
        super(message);
    }
}
