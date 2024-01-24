package ru.yandex.practicum.filmorate.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.storage.entity.Film;
import ru.yandex.practicum.filmorate.storage.repository.db.FilmStorageImpl;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
class FilmDbStorageTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testFindFilmById() {
        var film = Film.builder()
                .name("test name")
                .description("test desc")
                .releaseDate(LocalDate.of(2000, 2, 22))
                .duration(22)
                .build();

        var filmStorage = new FilmStorageImpl(jdbcTemplate);
        filmStorage.addFilm(film);
        film.setLikes(new HashSet<>());
        film.setLikeToFilm(0L);

        film.setGenres(new HashSet<>());
        var savedFilm = filmStorage.getFilmById(1L);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(Optional.of(film));
    }
}
