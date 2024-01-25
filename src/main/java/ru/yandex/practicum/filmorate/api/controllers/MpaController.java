package ru.yandex.practicum.filmorate.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.api.service.FilmService;
import ru.yandex.practicum.filmorate.storage.entity.Mpa;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MpaController {

    private final FilmService filmService;

    @GetMapping("/mpa")
    public List<Mpa> getMpa() {
        return filmService.getMpa();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpaById(@PathVariable Long id) {
        return filmService.getMpaById(id);
    }
}
