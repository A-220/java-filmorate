package ru.yandex.practicum.filmorate.api.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.api.errors.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.entity.Film;
import ru.yandex.practicum.filmorate.storage.repository.FilmStorage;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    @Autowired
    private final FilmStorage filmRepository;
    public static final String FILM_NOT_FOUND_WARN = "Film with id: %s doesn't exist.";
    public static final String SUCCESSFUL_ADD_FILM = "Successful add film with id: {}";
    public static final String SUCCESSFUL_UPDATE_FILM = "Successful update film with id: {}";
    public static final String SUCCESSFUL_ADD_LIKE = "Successful add like for film with id: {}, of user with id: {}";
    public static final String SUCCESSFUL_REMOVE_LIKE = "Successful remove like for film with id: {}, of user with id: {}";
    public static final String EMPTY_LIST_WARN = "The list of films is empty";

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
        var film = getFilmById(id);
        if (userId <= 0) throw new NotFoundException(UserServiceImpl.NOT_FOUND_USER);
        updateLikes(film, userId);
        log.info(SUCCESSFUL_ADD_LIKE, id, userId);
        return film;
    }

    @Override
    public Film removeLike(Long id, Long userId) {
        var film = getFilmById(id);
        if (userId <= 0) throw new NotFoundException(UserServiceImpl.NOT_FOUND_USER);
        removeLikes(film, userId);
        log.info(SUCCESSFUL_REMOVE_LIKE, id, userId);
        return null;
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        return getAllFilms().stream().sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed()).limit(count).collect(toList());
    }

    private void updateLikes(Film film, Long userId) {
        var likes = film.getLikes();
        likes.add(userId);
        film.setLikes(likes);
    }

    private void removeLikes(Film film, long userId) {
        var likes = film.getLikes();
        likes.remove(userId);
        film.setLikes(likes);

    }
}
