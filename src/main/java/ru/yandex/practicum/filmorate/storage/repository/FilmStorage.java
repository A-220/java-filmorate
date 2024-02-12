package ru.yandex.practicum.filmorate.storage.repository;

import ru.yandex.practicum.filmorate.storage.entity.Film;
import ru.yandex.practicum.filmorate.storage.entity.Genre;
import ru.yandex.practicum.filmorate.storage.entity.Mpa;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {
    Film addFilm(Film film);

    Optional<Film> updateFilm(Film film);

    List<Film> getAllFilms();

    Optional<Film> getFilmById(Long id);

    List<Film> getSortedByDirector(Long idDirector, String string);

    List<Mpa> mpa();

    Mpa mpaById(Long id);

    List<Genre> genre();

    Genre genreById(Long id);

    void deleteFilm(Long id);

    Map<Long, List<Long>> getAllLikes();

    List<Film> getMostPopularFilms(Integer count, Long genreId, Integer year);

    List<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> search(String by, String title);
}
