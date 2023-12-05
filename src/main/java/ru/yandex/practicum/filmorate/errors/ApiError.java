package ru.yandex.practicum.filmorate.errors;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiError {
    private int statusCode;
    private String message;
}
