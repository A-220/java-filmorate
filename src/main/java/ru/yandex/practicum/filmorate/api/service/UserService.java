package ru.yandex.practicum.filmorate.api.service;

import ru.yandex.practicum.filmorate.storage.entity.User;

import java.util.List;

public interface UserService {
    User getUserById(Long id);

    User addUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User addNewFriend(Long id, Long friendId);

    User deleteFriend(Long id, Long friendId);

    List<User> getAllFriends(Long id);

    List<User> getCommonFriends(Long id, Long userId);
}
