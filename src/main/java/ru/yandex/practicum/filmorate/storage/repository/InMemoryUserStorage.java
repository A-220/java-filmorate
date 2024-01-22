package ru.yandex.practicum.filmorate.storage.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.entity.User;
import ru.yandex.practicum.filmorate.utils.IncrementUtil;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> mapOfUsers = new HashMap<>();


    @Override
    public Optional<User> getUserById(Long id) {
        if (mapOfUsers.containsKey(id)) {
            return Optional.of(mapOfUsers.get(id));
        }
        return Optional.empty();
    }

    @Override
    public User addUser(User user) {
        user.setId(IncrementUtil.getIncrementUserId());
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        mapOfUsers.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> updateUser(User user) {
        if (mapOfUsers.containsKey(user.getId())) {
            if (user.getFriends() == null) {
                user.setFriends(new HashSet<>());
            }
            mapOfUsers.put(user.getId(), user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(mapOfUsers.values());
    }

}
