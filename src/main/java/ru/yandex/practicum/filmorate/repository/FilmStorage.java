package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    public Film addFilm(Film film);

    public Optional<Film> updateFilm(Film film);

    public List<Film> getAllFilms();

    public Optional<Film> getFilmById(Long id);
}
