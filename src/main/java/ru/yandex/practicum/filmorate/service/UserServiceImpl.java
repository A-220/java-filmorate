package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userRepository;
    public static final String NOT_FOUND_USER = "User with id %s doesn't exist.";
    public static final String SUCCESSFUL_ADD_USER = "Successful add user with id: {}";
    public static final String SUCCESSFUL_UPDATE_USER = "Successful update user with id: {}";
    public static final String SUCCESSFUL_ADD_FRIEND = "Successful add new friend with id: {}, for user with id: {}";
    public static final String SUCCESSFUL_DELETE_FRIEND = "Successful delete friend with id: {}, for user with id: {}";
    public static final String USER_ALREADY_FRIEND = "User with id: {}, already friend with user with id: {}";
    public static final String USER_NOT_A_FRIEND = "User with id: {}, not a friend for user with id: {}";
    public static final String EMPTY_LIST_WARN = "The list of users is empty";
    public static final String EMPTY_LIST_OF_FRIENDS = "The list of friends is empty";
    public static final String EMPTY_LIST_OF_COMMON_FRIENDS = "The list of common friends is empty";
    public static final String USER_CANNOT_BE_FRIEND_WITH_SELF = "User cannot be friend with them self";

    @Autowired
    public UserServiceImpl(UserStorage userRepository) {
        this.userRepository = userRepository;
    }


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
    public List<User> getAllUsers() {
        var listOfUsers = userRepository.getAllUsers();
        if (listOfUsers.isEmpty()) {
            log.warn(EMPTY_LIST_WARN);
        }
        return listOfUsers;
    }

    @Override
    public User addNewFriend(Long id, Long friendId) {
        if (id.equals(friendId)) {
            log.warn(USER_CANNOT_BE_FRIEND_WITH_SELF);
            return getUserById(id);
        }

        var user = getUserById(id);
        var friend = getUserById(friendId);

        if (user.getFriends().contains(friendId)) {
            log.warn(USER_ALREADY_FRIEND, friendId, id);
            return user;
        }

        addUserFriendList(user, friendId);
        addUserFriendList(friend, id);

        log.info(SUCCESSFUL_ADD_FRIEND, friendId, id);
        return user;
    }


    @Override
    public User deleteFriend(Long id, Long friendId) {
        if (id.equals(friendId)) {
            log.warn(USER_CANNOT_BE_FRIEND_WITH_SELF);
            return getUserById(id);
        }

        var user = getUserById(id);
        var friend = getUserById(friendId);

        if (!user.getFriends().contains(friendId)) {
            log.warn(USER_NOT_A_FRIEND, friendId, id);
            return user;
        }

        removeUserFriendList(user, friendId);
        removeUserFriendList(user, friendId);


        log.info(SUCCESSFUL_DELETE_FRIEND, friendId, id);
        return user;
    }

    @Override
    public List<User> getAllFriends(Long id) {
        var user = getUserById(id);
        if (user.getFriends().isEmpty()) {
            log.warn(EMPTY_LIST_OF_FRIENDS);
        }
        return user.getFriends().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long id, Long userId) {
        var userFriends = getAllFriends(id);
        var otherUserFriends = getAllFriends(userId);
        var listOfCommonFriends = userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toList());
        if (listOfCommonFriends.isEmpty()) {
            log.warn(EMPTY_LIST_OF_COMMON_FRIENDS);
        }
        return listOfCommonFriends;
    }


    private void ensureNameIsSet(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    private void addUserFriendList(User user, Long friendId) {
        var friends = user.getFriends();
        friends.add(friendId);
        user.setFriends(friends);
    }

    private void removeUserFriendList(User user, Long friendId) {
        var friends = user.getFriends();
        friends.remove(friendId);
        user.setFriends(friends);
    }

}
