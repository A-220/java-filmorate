package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.storage.entity.User;
import ru.yandex.practicum.filmorate.storage.repository.db.UserStorageImpl;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

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
                .isEqualTo(user);
    }
}
