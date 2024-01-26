package ru.yandex.practicum.filmorate.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.api.errors.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.entity.User;
import ru.yandex.practicum.filmorate.storage.repository.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userRepository;
    public static final String NOT_FOUND_USER = "User with id %s doesn't exist.";
    public static final String SUCCESSFUL_ADD_USER = "Successful add user with id: {}";
    public static final String SUCCESSFUL_UPDATE_USER = "Successful update user with id: {}";
    public static final String SUCCESSFUL_DELETE_FRIEND = "Successful delete friend with id: {}, for user with id: {}";
    public static final String EMPTY_LIST_WARN = "The list of users is empty";
    public static final String USER_CANNOT_BE_FRIEND_WITH_SELF = "User cannot be friend with them self";

    @Override
    public User getUserById(Long id) {
        return userRepository.getUserById(id).orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_USER, id)));
    }

    @Override
    public User addUser(User user) {
        ensureNameIsSet(user);
        var resUser = userRepository.addUser(user);
        log.info(SUCCESSFUL_ADD_USER, user.getId());
        return resUser;
    }

    @Override
    public User updateUser(User user) {
        ensureNameIsSet(user);
        var resUser = userRepository.updateUser(user).orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_USER, user.getId())));
        log.info(SUCCESSFUL_UPDATE_USER, user.getId());
        return resUser;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.delete(id);
    }

    @Override
    public List<User> getAllUsers() {
        var listOfUsers = userRepository.getAllUsers();
        if (listOfUsers.isEmpty()) {
            log.warn(EMPTY_LIST_WARN);
        }
        return listOfUsers;
    }

    @Override
    public User addNewFriend(Long id, Long friendId) {
        User user = getUserById(id);
        getUserById(friendId);

        user.setFriendStatus(friendId, "Запрос отправлен");

        updateUser(user);
        return user;
    }


    @Override
    public User deleteFriend(Long id, Long friendId) {
        if (id.equals(friendId)) {
            log.warn(USER_CANNOT_BE_FRIEND_WITH_SELF);
            return getUserById(id);
        }

        User user = getUserById(id);

        user.deleteFriend(friendId);
        updateUser(user);

        log.info(SUCCESSFUL_DELETE_FRIEND, friendId, id);
        return user;
    }

    @Override
    public LinkedHashSet<User> getAllFriends(Long id) {
        User user = getUserById(id);
        return user.getFriendStatus().keySet().stream()
                .map(this::getUserById)
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<User> getCommonFriends(Long id, Long userId) {
        Set<Long> i = getUserById(id).getFriendStatus().keySet();
        Set<Long> j = getUserById(userId).getFriendStatus().keySet();
        return i.stream()
                .filter(j::contains)
                .map(this::getUserById)
                .collect(Collectors.toSet());
    }


    private void ensureNameIsSet(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

}
