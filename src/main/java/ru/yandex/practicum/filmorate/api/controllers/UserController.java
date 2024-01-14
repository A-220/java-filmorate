package ru.yandex.practicum.filmorate.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.api.service.UserService;
import ru.yandex.practicum.filmorate.storage.entity.User;
import ru.yandex.practicum.filmorate.api.errors.ErrorsHandlerUtil;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
    public List<User> getAllFriends(@PathVariable(value = "id") Long id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable(value = "id") Long id,
                                       @PathVariable(value = "otherId") Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }


    @PostMapping
    public User addUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        ErrorsHandlerUtil.throwValidationExceptionIfErrorsExist(bindingResult);
        return userService.addUser(user);
    }


    @PutMapping
    public User updateUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        ErrorsHandlerUtil.throwValidationExceptionIfErrorsExist(bindingResult);
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }


}
