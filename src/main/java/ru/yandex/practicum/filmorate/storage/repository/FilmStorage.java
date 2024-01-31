package ru.yandex.practicum.filmorate.storage.repository;

import ru.yandex.practicum.filmorate.storage.entity.Film;
import ru.yandex.practicum.filmorate.storage.entity.Genre;
import ru.yandex.practicum.filmorate.storage.entity.Mpa;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film addFilm(Film film);

    Optional<Film> updateFilm(Film film);

    List<Film> getAllFilms();

    Optional<Film> getFilmById(Long id);

    List<Film> getSortedByDirector(Long idDirector, String string);

    List<Film> getTopFilms(Integer count);

    List<Mpa> mpa();

    Mpa mpaById(Long id);

    List<Genre> genre();

    Genre genreById(Long id);

    void delete(Long id);

    List<Film> getCommonFilms(Long userId, Long friendId);
}
