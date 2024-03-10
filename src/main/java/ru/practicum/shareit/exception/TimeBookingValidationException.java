package ru.practicum.shareit.exception;

public class TimeBookingValidationException extends RuntimeException {
    public TimeBookingValidationException(final String message) {
        super(message);
    }
}
