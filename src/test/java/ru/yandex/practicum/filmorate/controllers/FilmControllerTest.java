package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;


class FilmControllerTest {

    FilmController filmController;
    private static Validator validator;

    @BeforeAll
    public static void getValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void addFilmSuccess() {
        var film = getFilm();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println(violations);
        Assertions.assertEquals(violations.size(), 0);
    }

    @Test
    void validateNameTest() {
        var film = getFilm();
        film.setName("");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(violations.size(), 1);
    }

    @Test
    void validateDescriptionTest() {
        var film = getFilm();
        film.setDescription("a".repeat(201));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(violations.size(), 1);
    }

    @Test
    void validateDateOfReleaseDateTest() {
        var film = getFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 28).minusYears(22));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(violations.size(), 1);
    }

    @Test
    void validateDurationTest() {
        var film = getFilm();
        film.setDuration(-1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(violations.size(), 1);
    }

    private Film getFilm() {
        return Film.builder()
                .name("test")
                .description("test test")
                .duration(120)
                .releaseDate(LocalDate.now().minusDays(5))
                .build();
    }


}