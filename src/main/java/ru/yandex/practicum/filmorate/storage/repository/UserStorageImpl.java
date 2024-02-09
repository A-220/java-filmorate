package ru.yandex.practicum.filmorate.storage.repository;

import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.api.errors.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.entity.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;

import static ru.yandex.practicum.filmorate.api.service.UserServiceImpl.NOT_FOUND_USER;


@Repository
public class UserStorageImpl implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private void checkUserExist(Long id) {
        if (!(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE user_id = ?", Long.class, id) > 0)) {
            throw new NotFoundException(NOT_FOUND_USER);
        }
    }

    @Override
    public Optional<User> updateUser(User user) {
        checkUserExist(user.getId());
        jdbcTemplate.update("update users set email=?, login=?, name=?, birthday=? where user_id = ?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        insertFriends(user, true);
        return Optional.of(user);
    }

    private void insertFriends(User user, boolean update) {
        if (update) {
            String deleteFriends = "delete from friends where user_id = ?";
            jdbcTemplate.update(deleteFriends, user.getId());
        }

        if (user.getFriendStatus() != null && !user.getFriendStatus().isEmpty()) {
            String addFriends = "insert into friends(user_id, users_id, status) values (?, ?, ?)";
            List<Object[]> batchArgs = new ArrayList<>();

            for (Map.Entry<Long, String> entry : user.getFriendStatus().entrySet()) {
                batchArgs.add(new Object[]{user.getId(), entry.getKey(), entry.getValue()});
            }

            if (!batchArgs.isEmpty()) {
                jdbcTemplate.batchUpdate(addFriends, batchArgs);
            }
        }
    }


    @Override
    public User addUser(User user) {
        KeyHolder key = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT into users(login, name, email, birthday) VALUES(?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setTimestamp(4, Timestamp.valueOf(user.getBirthday().atStartOfDay()));
            return ps;
        }, key);

        Long id = key.getKey().longValue();
        user.setId(id);

        insertFriends(user, false);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id).orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_USER, id)));

        String deleteFriends = "delete from friends where users_id = ?";
        jdbcTemplate.update(deleteFriends, user.getId());

        String deleteFromLikes = "delete from likes where user_id = ?";
        jdbcTemplate.update(deleteFromLikes, id);

        String deleteFromUsers = "delete from users where user_id = ?";
        jdbcTemplate.update(deleteFromUsers, id);
    }

    @SneakyThrows
    @Override
    public Optional<User> getUserById(Long id) {
        checkUserExist(id);
        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT * FROM users WHERE user_id = ?", id);

        if (rs.next()) {
            User user = User.builder()
                    .email(rs.getString("email"))
                    .login(rs.getString("login"))
                    .name(rs.getString("name"))
                    .birthday(Objects.requireNonNull(rs.getDate("birthday")).toLocalDate())
                    .id(rs.getLong("user_id"))
                    .build();

            Map<Long, List<Map<Long, String>>> friend = makeFriend();
            friend.getOrDefault(user.getId(), Collections.emptyList())
                    .forEach(userFriendMap ->
                            userFriendMap.forEach(user::setFriendStatus)
                    );

            return Optional.of(user);
        }
        return Optional.empty();
    }

    @SneakyThrows
    @Override
    public List<User> getAllUsers() {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT * FROM users");

        List<User> users = new ArrayList<>();
        while (rs.next()) {
            User user = User.builder()
                    .email(rs.getString("email"))
                    .login(rs.getString("login"))
                    .name(rs.getString("name"))
                    .birthday(Objects.requireNonNull(rs.getDate("birthday")).toLocalDate())
                    .id(rs.getLong("user_id"))
                    .build();

            users.add(user);
        }

        Map<Long, List<Map<Long, String>>> friend = makeFriend();

        if (!friend.isEmpty()) {
            users.forEach(user -> {
                friend.getOrDefault(user.getId(), Collections.emptyList())
                        .forEach(userFriendMap -> userFriendMap.forEach(user::setFriendStatus));
            });
        }
        return users;
    }

    private Map<Long, List<Map<Long, String>>> makeFriend() {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT * FROM friends");
        Map<Long, List<Map<Long, String>>> userFriends = new HashMap<>();

        while (rs.next()) {
            Long userId = rs.getLong("user_id");
            Long userFriendsId = rs.getLong("users_id");
            String status = rs.getString("status");

            Map<Long, String> friendsWithStatus = new HashMap<>();
            friendsWithStatus.put(userFriendsId, status);

            if (userFriends.containsKey(userId)) {
                userFriends.get(userId).add(friendsWithStatus);
            } else {
                List<Map<Long, String>> listOfFriends = new ArrayList<>();
                listOfFriends.add(friendsWithStatus);
                userFriends.put(userId, listOfFriends);
            }
        }
        return userFriends;
    }
}
