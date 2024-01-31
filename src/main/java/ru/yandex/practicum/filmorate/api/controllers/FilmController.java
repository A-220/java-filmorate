package ru.yandex.practicum.filmorate.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.api.errors.ErrorsHandler;
import ru.yandex.practicum.filmorate.storage.entity.Film;
import ru.yandex.practicum.filmorate.api.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final ErrorsHandler errorsHandler;

    @Autowired
    public FilmController(FilmService filmService, ErrorsHandler errorsHandler) {
        this.filmService = filmService;
        this.errorsHandler = errorsHandler;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable(value = "id") Long id) {
        return filmService.getFilmById(id);
    }


    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film, BindingResult bindingResult) {
        errorsHandler.throwValidationExceptionIfErrorsExist(bindingResult);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film, BindingResult bindingResult) {
        errorsHandler.throwValidationExceptionIfErrorsExist(bindingResult);
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film setLike(@PathVariable(value = "id") Long id,
                        @PathVariable(value = "userId") Long userId) {
        return filmService.setLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable(value = "id") Long id,
                           @PathVariable(value = "userId") Long userId) {
        return filmService.removeLike(id, userId);
    }

//    @GetMapping("/popular")
//    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") Integer count) {
//        return filmService.getTopFilms(count);
//    }


    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable Long id) {
        filmService.deleteFilm(id);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(
            @RequestParam(required = false, defaultValue = "10") Integer count,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Integer year) {
        return filmService.getMostPopularFilms(count, genreId, year);
    }
    @GetMapping("/director/{directorId}")
    public List<Film> getSortedByDirector(
            @PathVariable Long directorId,
            @RequestParam(name = "sortBy") String string) {
        return filmService.getSortedByDirector(directorId, string);
    }

}
