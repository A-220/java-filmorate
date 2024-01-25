package ru.yandex.practicum.filmorate.storage.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.api.errors.annotations.ReleaseDate;
import ru.yandex.practicum.filmorate.api.errors.exception.NotFoundException;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@NotNull
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
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

    private Set<Long> likes = new HashSet<>();

    @JsonProperty("mpa")
    private Mpa mpa = new Mpa();

    @JsonProperty("genres")
    private Set<Genre> genres = new HashSet<>();

    public void setLikeToFilm(Long id) {
        likes.add(id);
    }

    public void deleteLike(Long id) {
        if (!likes.contains(id)) {
            throw new NotFoundException("Not valid id");
        }
        likes.remove(id);
    }
}

