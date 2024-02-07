package ru.yandex.practicum.filmorate.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.api.errors.ErrorsHandler;
import ru.yandex.practicum.filmorate.api.service.FilmService;
import ru.yandex.practicum.filmorate.api.service.UserService;
import ru.yandex.practicum.filmorate.storage.entity.Event;
import ru.yandex.practicum.filmorate.storage.entity.Film;
import ru.yandex.practicum.filmorate.storage.entity.User;

import javax.validation.Valid;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final FilmService filmService;
    private final ErrorsHandler errorsHandler;

    @Autowired
    public UserController(UserService userService, FilmService filmService, ErrorsHandler errorsHandler) {
        this.userService = userService;
        this.filmService = filmService;
        this.errorsHandler = errorsHandler;
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable(value = "id") Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("{id}/friends/{friendId}")
    public User addNewFriend(@PathVariable(value = "id") Long id,
                             @PathVariable(value = "friendId") Long friendId) {
        return userService.addNewFriend(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable(value = "id") Long id,
                             @PathVariable(value = "friendId") Long friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("{id}/friends")
    public LinkedHashSet<User> getAllFriends(@PathVariable(value = "id") Long id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable(value = "id") Long id,
                                      @PathVariable(value = "otherId") Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        errorsHandler.throwValidationExceptionIfErrorsExist(bindingResult);
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        errorsHandler.throwValidationExceptionIfErrorsExist(bindingResult);
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable(value = "id") Long id) {
        userService.delete(id);
    }

    @GetMapping("{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable(value = "id") Long id) {
        return filmService.getRecommendations(id);
    }

    @GetMapping("{id}/feed")
    public List<Event> getAllFeed(@PathVariable(value = "id") Long id) {
        return userService.getUserFeed(id);
    }
}
