package ru.yandex.practicum.filmorate.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.storage.entity.User;
import ru.yandex.practicum.filmorate.storage.repository.db.UserStorageImpl;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
class UserDbStorageTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testFindUserById() {
        var user = User.builder()
                .email("test@email.com")
                .login("123")
                .name("test name")
                .birthday(LocalDate.of(2008, 1, 1))
                .build();

        var userStorage = new UserStorageImpl(jdbcTemplate);
        userStorage.addUser(user);
        var savedUser = userStorage.getUserById(1L);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(Optional.of(user));
    }
}
