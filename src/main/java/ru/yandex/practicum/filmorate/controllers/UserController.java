package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.ErrorsHandlerUtil;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
public class UserController {
    private final Map<Long, User> mapOfUsers = new HashMap<>();
    public static final String NOT_FOUND_USER = "User with id %s doesn't exist.";
    public static final String SUCCESSFUL_ADD_USER = "Successful add user with id: {}";
    public static final String SUCCESSFUL_UPDATE_USER = "Successful update user with id: {}";
    private long incrementId = 0;

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        ErrorsHandlerUtil.throwValidationExceptionIfErrorsExist(bindingResult);
        ensureNameIsSet(user);
        user.setId(getIncrementId());
        mapOfUsers.put(user.getId(), user);
        log.info(SUCCESSFUL_ADD_USER, user.getId());
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        ErrorsHandlerUtil.throwValidationExceptionIfErrorsExist(bindingResult);
        ensureNameIsSet(user);
        if (mapOfUsers.containsKey(user.getId())) {
            mapOfUsers.put(user.getId(), user);
            log.info(SUCCESSFUL_UPDATE_USER, user.getId());
            return user;
        } else {
            throw new NotFoundException(String.format(NOT_FOUND_USER, user.getId()));
        }
    }

    @GetMapping("/users")
    public List<User> getListOfUsers() {
        List<User> userList = new ArrayList<>(mapOfUsers.values());
        if (userList.isEmpty()) {
            log.warn("List of users is empty");
        }
        return userList;
    }

    private void ensureNameIsSet(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    private long getIncrementId() {
        return ++incrementId;
    }
}
