package ru.yandex.practicum.filmorate.storage.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.entity.Film;
import ru.yandex.practicum.filmorate.utils.IncrementUtil;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> mapOfFilms = new HashMap<>();

    @Override
    public Optional<Film> getFilmById(Long id) {
        if (mapOfFilms.containsKey(id)) {
            return Optional.of(mapOfFilms.get(id));
        }
        return Optional.empty();
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        return null;
    }

    @Override
    public List<Film.Mpa> mpa() {
        return null;
    }

    @Override
    public Film.Mpa mpaById(Long id) {
        return null;
    }

    @Override
    public List<Film.Genre> genre() {
        return null;
    }

    @Override
    public Film.Genre genreById(Long id) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Film addFilm(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        film.setId(IncrementUtil.getIncrementFilmId());
        mapOfFilms.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        if (mapOfFilms.containsKey(film.getId())) {
            mapOfFilms.put(film.getId(), film);
            return Optional.of(film);
        }
        return Optional.empty();
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(mapOfFilms.values());
    }
}
