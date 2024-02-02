package ru.yandex.practicum.filmorate.api.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.api.errors.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.entity.Film;
import ru.yandex.practicum.filmorate.storage.entity.Genre;
import ru.yandex.practicum.filmorate.storage.entity.Mpa;
import ru.yandex.practicum.filmorate.storage.repository.FilmStorage;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmRepository;
    public static final String FILM_NOT_FOUND_WARN = "Film with id: %s doesn't exist.";
    public static final String SUCCESSFUL_ADD_FILM = "Successful add film with id: {}";
    public static final String SUCCESSFUL_UPDATE_FILM = "Successful update film with id: {}";
    public static final String EMPTY_LIST_WARN = "The list of films is empty";


    public List<Mpa> getMpa() {
        return filmRepository.mpa();
    }

    public Mpa getMpaById(Long id) {
        return filmRepository.mpaById(id);
    }

    @Override
    public List<Genre> getGenre() {
        return filmRepository.genre();
    }

    @Override
    public Genre getGenreById(Long id) {
        return filmRepository.genreById(id);
    }

    @Override
    public void deleteFilm(Long id) {
        filmRepository.delete(id);
    }

    @Override
    public Film getFilmById(Long id) {
        return filmRepository.getFilmById(id).orElseThrow(() -> new NotFoundException(String.format(FILM_NOT_FOUND_WARN, id)));
    }

    @Override
    public Film addFilm(Film film) {
        var resFilm = filmRepository.addFilm(film);
        log.info(SUCCESSFUL_ADD_FILM, film.getId());
        return resFilm;
    }

    @Override
    public Film updateFilm(Film film) {
        var resFilm = filmRepository.updateFilm(film).orElseThrow(() -> new NotFoundException(String.format(FILM_NOT_FOUND_WARN, film.getId())));
        log.info(SUCCESSFUL_UPDATE_FILM, film.getId());
        return resFilm;
    }

    @Override
    public List<Film> getAllFilms() {
        var listOfFilms = filmRepository.getAllFilms();
        if (listOfFilms.isEmpty()) {
            log.warn(EMPTY_LIST_WARN);
        }
        return listOfFilms;
    }


    @Override
    public Film setLike(Long id, Long userId) {
        var film = filmRepository.getFilmById(id).orElseThrow(() ->
                new NotFoundException(UserServiceImpl.NOT_FOUND_USER));
        film.setLikeToFilm(userId);
        updateFilm(film);
        return film;
    }

    @Override
    public Film removeLike(Long id, Long userId) {
        var film = filmRepository.getFilmById(id).orElseThrow(() ->
                new NotFoundException(UserServiceImpl.NOT_FOUND_USER));
        film.deleteLike(userId);
        updateFilm(film);
        return film;
    }

    @Override
    public List<Film> getSortedByDirector(Long idDirector, String string) {
        return filmRepository.getSortedByDirector(idDirector, string);
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        return filmRepository.getTopFilms(count);
    }

    @Override
    public List<Film> search(String query, String title) {
        return filmRepository.search(query, title);
    }
}
