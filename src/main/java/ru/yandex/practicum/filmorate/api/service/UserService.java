package ru.yandex.practicum.filmorate.api.service;

import ru.yandex.practicum.filmorate.storage.entity.User;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public interface UserService {
    User getUserById(Long id);

    User addUser(User user);

    User updateUser(User user);

    void delete(Long id);

    List<User> getAllUsers();

    User addNewFriend(Long id, Long friendId);

    User deleteFriend(Long id, Long friendId);

    LinkedHashSet<User> getAllFriends(Long id);

    Set<User> getCommonFriends(Long id, Long userId);
}
