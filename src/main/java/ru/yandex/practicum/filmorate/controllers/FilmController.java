package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.ErrorsHandlerUtil;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    private final Map<Long, Film> mapOfFilms = new HashMap<>();
    public static final String FILM_NOT_FOUND_WARN = "Film with id: %s doesn't exist.";
    public static final String SUCCESSFUL_ADD_FILM = "Successful add film with id: {}";
    public static final String SUCCESSFUL_UPDATE_FILM = "Successful update film with id: {}";

    private long incrementId = 0;

    //cтоит ли данную реализацию перенести в сервсиный слой а отсюда дергать сами методы
    //как можно еще улучшить это решение что бы была четенькая архитекутра?
    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film, BindingResult bindingResult) {
        ErrorsHandlerUtil.throwValidationExceptionIfErrorsExist(bindingResult);
        film.setId(getIncrementId());
        mapOfFilms.put(film.getId(), film);
        log.info(SUCCESSFUL_ADD_FILM, film.getId());
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film, BindingResult bindingResult) {
        ErrorsHandlerUtil.throwValidationExceptionIfErrorsExist(bindingResult);
        if (mapOfFilms.containsKey(film.getId())) {
            mapOfFilms.put(film.getId(), film);
            log.info(SUCCESSFUL_UPDATE_FILM, film.getId());
            return film;
        } else {
            throw new NotFoundException(String.format(FILM_NOT_FOUND_WARN, film.getId()));
            // а в этом случае нужно создавать not found exception?
        }
    }

    @GetMapping("/films")
    public List<Film> getListOfFilms() {
        List<Film> filmList = new ArrayList<>(mapOfFilms.values());
        if (filmList.isEmpty()) {
            log.warn("List of films is empty");
        }
        return filmList;
    }


    private long getIncrementId() {
        return ++incrementId;
    }

}
