package ru.yandex.practicum.filmorate.api.errors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.api.errors.exception.NotFoundException;
import ru.yandex.practicum.filmorate.api.errors.exception.ValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleThrowable(final ValidationException e) {
        log.error("400 bad request {}", e.getMessage(), e);
        return new ResponseEntity<>(new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handleThrowable(final NotFoundException e) {
        log.error("404 not found {}", e.getMessage(), e);
        return new ResponseEntity<>(new ApiError(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleThrowable(final ConstraintViolationException e) {
        log.error("400 bad request {}", e.getMessage(), e);
        return new ResponseEntity<>(new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

}