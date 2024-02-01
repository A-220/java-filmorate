package ru.yandex.practicum.filmorate.storage.entity;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Validated
@Data
public class Director {
    private Long id;
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    public Director(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Director() {
    }
}
