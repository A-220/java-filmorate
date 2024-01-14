package ru.yandex.practicum.filmorate.api.errors.exception;


public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }

}
