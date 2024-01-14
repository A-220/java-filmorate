package ru.yandex.practicum.filmorate.storage.entity;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.api.errors.annotations.ReleaseDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;


@Data
@Builder(toBuilder = true)
@NotNull
public class Film {
    private Long id;
    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    private String name;
    @NotNull(message = "Description cannot be null")
    @Size(max = 200, message = "Description cannot be more than 200 characters")
    private String description;
    @NotNull(message = "Name cannot be null")
    @ReleaseDate
    private LocalDate releaseDate;
    @Positive(message = "Duration cannot be zero or less")
    private Integer duration;
    private Set<Long> likes;
}
