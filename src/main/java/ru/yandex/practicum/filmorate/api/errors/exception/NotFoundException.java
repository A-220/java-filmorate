package ru.yandex.practicum.filmorate.api.errors.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
