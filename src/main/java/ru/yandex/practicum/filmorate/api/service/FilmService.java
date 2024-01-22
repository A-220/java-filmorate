package ru.yandex.practicum.filmorate.api.service;

import ru.yandex.practicum.filmorate.storage.entity.Film;

import java.util.List;

public interface FilmService {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilmById(Long id);

    Film setLike(Long id, Long userId);

    Film removeLike(Long id, Long userId);

    List<Film> getTopFilms(Integer count);
}