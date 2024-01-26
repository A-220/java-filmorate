package ru.yandex.practicum.filmorate.storage.repository;

import ru.yandex.practicum.filmorate.storage.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    void delete(Long id);

    Optional<User> getUserById(Long id);

    User addUser(User user);

    Optional<User> updateUser(User user);

    List<User> getAllUsers();
}
