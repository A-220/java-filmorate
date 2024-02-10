package ru.yandex.practicum.filmorate.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Director {

    private Long id;

    @NotEmpty(message = "Name cannot be empty")
    @Size(max = 200)
    private String name;
}
