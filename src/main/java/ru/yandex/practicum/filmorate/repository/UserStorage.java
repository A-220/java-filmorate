package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> getUserById(Long id);

    User addUser(User user);

    Optional<User> updateUser(User user);

    List<User> getAllUsers();

}
