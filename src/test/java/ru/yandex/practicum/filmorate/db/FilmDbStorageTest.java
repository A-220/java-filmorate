package ru.yandex.practicum.filmorate.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.storage.entity.Film;
import ru.yandex.practicum.filmorate.storage.entity.Mpa;
import ru.yandex.practicum.filmorate.storage.repository.FilmStorageImpl;

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
        var mpa = new Mpa();
        mpa.setId(1);
        var film = Film.builder()
                .name("test name")
                .description("test desc")
                .releaseDate(LocalDate.of(2000, 2, 22))
                .duration(22)
                .mpa(mpa)
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .directors(new HashSet<>())
                .build();

        var filmStorage = new FilmStorageImpl(jdbcTemplate);

        filmStorage.addFilm(film);
        film.setLikeToFilm(0L);
        film.getMpa().setName("G");


        var savedFilm = filmStorage.getFilmById(1L);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(Optional.of(film));
    }
}
