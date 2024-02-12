package ru.yandex.practicum.filmorate.api.service;

import ru.yandex.practicum.filmorate.storage.entity.Film;
import ru.yandex.practicum.filmorate.storage.entity.Genre;
import ru.yandex.practicum.filmorate.storage.entity.Mpa;

import java.util.List;

public interface FilmService {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilmById(Long id);

    Film setLike(Long id, Long userId);

    Film removeLike(Long id, Long userId);

    List<Film> getSortedByDirector(Long idDirector, String string);

    List<Mpa> getMpa();

    Mpa getMpaById(Long id);

    List<Genre> getGenre();

    Genre getGenreById(Long id);

    List<Film> getRecommendations(Long id);

    void delete(Long id);

    List<Film> getMostPopularFilms(Integer count, Long genreId, Integer year);

    List<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> search(String s1, String s2);
}
