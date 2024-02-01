package ru.yandex.practicum.filmorate.storage.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.api.errors.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.entity.Director;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class DirectorStorageImpl implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

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
    public Director get(Long id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT * FROM directors WHERE director_id = ?", id);
        if (rs.next()) {
            return new Director(
                    rs.getLong("director_id"),
                    rs.getString("director_name")
            );
        }
        throw new NotFoundException("Нет такого директора");
    }

    @Override
    public Director create(Director director) {
        if (director.getName().equals(" ")) {
            throw new RuntimeException("Name cannot be empty");
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
        get(director.getId());
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
