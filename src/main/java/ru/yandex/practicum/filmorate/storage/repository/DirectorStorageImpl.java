package ru.yandex.practicum.filmorate.storage.repository;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.api.errors.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.entity.Director;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class DirectorStorageImpl implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    public static final String NAME_CANNOT_BE_EMPTY = "Name cannot be empty";

    @Override
    public List<Director> getAll() {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT * FROM directors");
        List<Director> directors = new ArrayList<>();

        while (rs.next()) {
            Director director = new Director(
                    rs.getLong("director_id"),
                    rs.getString("director_name")
            );
            directors.add(director);
        }
        return directors;
    }

    @Override
    public Director getById(Long id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT * FROM directors WHERE director_id = ?", id);
        if (rs.next()) {
            return new Director(
                    rs.getLong("director_id"),
                    rs.getString("director_name")
            );
        }
        throw new NotFoundException("Such director does not exist");
    }

    @SneakyThrows
    @Override
    public Director create(Director director) {
        if (director.getName().equals(" ")) {
            throw new IllegalAccessException(NAME_CANNOT_BE_EMPTY);
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO directors(director_name) VALUES(?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        director.setId(keyHolder.getKey().longValue());

        return director;
    }

    @Override
    public Director update(Director director) {
        getById(director.getId());
        String sql = "update directors set director_name =? where director_id =?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public void delete(Long id) {
        String delete = "delete from directors where director_id=?";
        jdbcTemplate.update(delete, id);
    }
}
