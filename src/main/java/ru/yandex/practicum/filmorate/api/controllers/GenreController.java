package ru.yandex.practicum.filmorate.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.api.service.FilmService;
import ru.yandex.practicum.filmorate.storage.entity.Film;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GenreController {

    private final FilmService filmService;

    @GetMapping("/genres")
    public List<Film.Genre> getGenre() {
        return filmService.getGenre();
    }

    @GetMapping("/genres/{id}")
    public Film.Genre getGenreById(@PathVariable Long id) {
        return filmService.getGenreById(id);
    }

}
