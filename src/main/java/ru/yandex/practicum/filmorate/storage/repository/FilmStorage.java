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
}
