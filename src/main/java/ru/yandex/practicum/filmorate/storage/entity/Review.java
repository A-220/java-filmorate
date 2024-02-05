package ru.yandex.practicum.filmorate.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NotNull
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    private Long id;

    private String content;

    private Boolean isPositive;

    private Long userId;

    private Long filmId;

    private Long useful;
}
