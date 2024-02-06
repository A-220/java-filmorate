package ru.yandex.practicum.filmorate.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NotNull
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    private Long reviewId;

    @NotNull(message = "Field content cannot be null")
    @NotEmpty(message = "Field content cannot be empty")
    private String content;

    @NotNull(message = "Field isPositive cannot be null")
    private Boolean isPositive;

    @NotNull(message = "Field userId cannot be null")
    private Long userId;

    @NotNull(message = "Field filmId cannot be null")
    private Long filmId;

    @Builder.Default
    private Long useful = 0L;
}
