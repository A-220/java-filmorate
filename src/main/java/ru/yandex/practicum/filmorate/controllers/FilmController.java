package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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
    public static final String VALIDATION_ERROR = "Validation error: {}";
    public static final String SUCCESSFUL_ADD_FILM = "Successful add film with id: {}";
    public static final String SUCCESSFUL_UPDATE_FILM = "Successful update film with id: {}";


    private long incrementId = 0;

    @PostMapping("/films")
    public ResponseEntity<?> addFilm(@Valid @RequestBody Film film, BindingResult bindingResult) {
        List<String> errors = ErrorsHandlerUtil.getListOfErrorsMessagesFromBindingResult(bindingResult);
        if (!errors.isEmpty()) {
            log.error(VALIDATION_ERROR, errors);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        } else {
            film.setId(getIncrementId());
            mapOfFilms.put(film.getId(), film);
            log.info(SUCCESSFUL_ADD_FILM, film.getId());
            return ResponseEntity.ok(film);
        }
    }

    @PutMapping("/films")
    public ResponseEntity<?> updateFilm(@Valid @RequestBody Film film, BindingResult bindingResult) {
        List<String> errors = ErrorsHandlerUtil.getListOfErrorsMessagesFromBindingResult(bindingResult);
        if (!errors.isEmpty()) {
            log.error(VALIDATION_ERROR, errors);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        } else {
            if (mapOfFilms.containsKey(film.getId())) {
                mapOfFilms.put(film.getId(), film);
                log.info(SUCCESSFUL_UPDATE_FILM, film.getId());
                return ResponseEntity.ok(film);
            } else {
                log.warn(String.format(FILM_NOT_FOUND_WARN, film.getId()));
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(FILM_NOT_FOUND_WARN, film.getId()));
            }
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
