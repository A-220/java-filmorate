package ru.yandex.practicum.filmorate.storage.repository;

import ru.yandex.practicum.filmorate.storage.entity.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film addFilm(Film film);

    Optional<Film> updateFilm(Film film);

    List<Film> getAllFilms();

    Optional<Film> getFilmById(Long id);

    List<Film> getTopFilms(Integer count);

    List<Film.Mpa> mpa();

    Film.Mpa mpaById(Long id);

    List<Film.Genre> genre();

    Film.Genre genreById(Long id);

    void delete(Long id);
}
