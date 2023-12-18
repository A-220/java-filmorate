package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    public Optional<User> getUserById(Long id);

    public User addUser(User user);

    public Optional<User> updateUser(User user);

    public List<User> getAllUsers();

}
