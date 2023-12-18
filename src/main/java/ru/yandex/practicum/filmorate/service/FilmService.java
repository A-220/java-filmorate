package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    public Film addFilm(Film film);

    public Film updateFilm(Film film);

    public List<Film> getAllFilms();

    public Film getFilmById(Long id);

    public Film setLike(Long id, Long userId);

    public Film removeLike(Long id, Long userId);

    public List<Film> getTopFilms(Integer count);
}
