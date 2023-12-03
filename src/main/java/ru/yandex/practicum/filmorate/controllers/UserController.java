package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.ErrorsHandlerUtil;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
public class UserController {
    private final Map<Long, User> mapOfUsers = new HashMap<>();
    public static final String VALIDATION_ERROR = "Validation error: {}";
    public static final String NOT_FOUND_USER = "User with id %s doesn't exist.";
    public static final String SUCCESSFUL_ADD_USER = "Successful add user with id: {}";
    public static final String SUCCESSFUL_UPDATE_USER = "Successful update user with id: {}";
    private long incrementId = 0;

    @PostMapping("/users")
    public ResponseEntity<?> addUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        List<String> errors = ErrorsHandlerUtil.getListOfErrorsMessagesFromBindingResult(bindingResult);
        if (!errors.isEmpty()) {
            log.error(VALIDATION_ERROR, errors);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.toString());
        } else {
            ensureNameIsSet(user);
            user.setId(getIncrementId());
            mapOfUsers.put(user.getId(), user);
            log.info(SUCCESSFUL_ADD_USER, user.getId());
            return ResponseEntity.ok(user);
        }
    }

    @PutMapping("/users")
    public ResponseEntity<?> updateUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        List<String> errors = ErrorsHandlerUtil.getListOfErrorsMessagesFromBindingResult(bindingResult);
        if (!errors.isEmpty()) {
            log.error(VALIDATION_ERROR, errors);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        } else {
            ensureNameIsSet(user);
            if (mapOfUsers.containsKey(user.getId())) {
                mapOfUsers.put(user.getId(), user);
                log.info(SUCCESSFUL_UPDATE_USER, user.getId());
                return ResponseEntity.ok(user);
            } else {
                log.warn(String.format(NOT_FOUND_USER, user.getId()));
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(NOT_FOUND_USER, user.getId()));
            }
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
