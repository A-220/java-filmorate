package ru.yandex.practicum.filmorate.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.api.errors.ErrorsHandler;
import ru.yandex.practicum.filmorate.api.service.FilmService;
import ru.yandex.practicum.filmorate.storage.entity.Film;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Validated
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

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable(value = "id") Long id) {
        filmService.delete(id);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(
            @RequestParam(defaultValue = "10") @Positive Integer count,
            @RequestParam(required = false, name = "genreId") Long genreId,
            @RequestParam(required = false, name = "year") @Min(1895) Integer year) {
        return filmService.getMostPopularFilms(count, genreId, year);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam Long userId,
                                     @RequestParam Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getSortedByDirector(
            @PathVariable Long directorId,
            @RequestParam(name = "sortBy") String string) {
        return filmService.getSortedByDirector(directorId, string);
    }

    @GetMapping("/search")
    public List<Film> search(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "by") String title) {
        return filmService.search(query, title);
    }

}
