package ru.yandex.practicum.filmorate.api.errors;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiError {
    private Integer statusCode;
    private String message;
}
