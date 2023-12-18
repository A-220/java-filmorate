package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService  {
    public User getUserById(Long id);
    public User addUser(User user);
    public User updateUser(User user);
    public List<User> getAllUsers();
    public User addNewFriend(Long id, Long friendId);
    public User deleteFriend(Long id, Long friendId);
    public List<User> getAllFriends(Long id);
    public List<User> getCommonFriends(Long id, Long userId);
}
