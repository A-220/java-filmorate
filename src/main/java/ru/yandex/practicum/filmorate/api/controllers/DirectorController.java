package ru.yandex.practicum.filmorate.api.controllers;

import lombok.AllArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.api.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.entity.Director;
import ru.yandex.practicum.filmorate.api.errors.ErrorsHandler;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
@AllArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    private final ErrorsHandler errorsHandler;

    @GetMapping
    public List<Director> getAll() {
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    public Director getById(@PathVariable Long id) {
        return directorService.getById(id);
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director, BindingResult bindingResult) {
        errorsHandler.throwValidationExceptionIfErrorsExist(bindingResult);
        return directorService.create(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director, BindingResult bindingResult) {
        errorsHandler.throwValidationExceptionIfErrorsExist(bindingResult);
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        directorService.delete(id);
    }
}
