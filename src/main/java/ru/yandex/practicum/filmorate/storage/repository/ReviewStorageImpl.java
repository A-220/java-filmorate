package ru.yandex.practicum.filmorate.storage.repository;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.api.errors.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.entity.Review;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReviewStorageImpl implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    public static final String NOT_FOUND = "Review with %d doesn't exist";
    public static final String LIKE_ALREADY_EXIST = "Like already exist";
    public static final String DISLIKE_ALREADY_EXIST = "Dislike already exist";

    @Autowired
    public ReviewStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review findById(Long id) {
        var rs = jdbcTemplate.queryForRowSet(
                "SELECT  * FROM review WHERE review_id = ?", id);

        if (rs.next()) {
            return Review.builder()
                    .id(rs.getLong("review_id"))
                    .content(rs.getString("content"))
                    .isPositive(rs.getBoolean("is_positive"))
                    .userId(rs.getLong("user_id"))
                    .filmId(rs.getLong("film_id"))
                    .useful(rs.getLong("useful"))
                    .build();
        }

        throw new NotFoundException(String.format(NOT_FOUND, id));
    }

    @Override
    public List<Review> getAllReviews(Long filmId, Long count) {
        var rs = jdbcTemplate.queryForRowSet(
                "SELECT * FROM review " +
                        "WHERE film_id = ? " +
                        "LIMIT ?; ", filmId, count);

        List<Review> reviews = new ArrayList<>();

        while (rs.next()) {
            var review = Review.builder()
                    .id(rs.getLong("review_id"))
                    .content(rs.getString("content"))
                    .isPositive(rs.getBoolean("is_positive"))
                    .userId(rs.getLong("user_id"))
                    .filmId(rs.getLong("film_id"))
                    .useful(rs.getLong("useful"))
                    .build();

            reviews.add(review);
        }
        return reviews;
    }

    @Override
    @SneakyThrows
    public Review create(Review review) {
        var keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO review(content, is_positive, user_id, film_id, useful)" +
                "VALUES(?, ?, ?, ?, ?) ";

        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            ps.setLong(5, review.getUseful());

            return ps;
        }, keyHolder);

        review.setId(keyHolder.getKey().longValue());

        return review;
    }

    @Override
    public Review update(Review review) {
        findById(review.getId());
        String sql = "UPDATE review SET " +
                "content = ? " +
                "is_positive = ? " +
                "user_id = ? " +
                "film_id = ? " +
                "useful = ? WHERE review_id = ?";

        jdbcTemplate.update(sql, review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful(),
                review.getId());

        return review;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM review WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void addRate(Long id, Long userId, String type) {

        findById(id);

        String sql = "INSERT INTO review(review_id, user_id, point)" +
                "VALUES(?, ?, ?) ";

        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(sql);

            ps.setLong(1, id);
            ps.setLong(2, userId);
            ps.setString(3, type);

            return ps;
        });
    }


    @Override
    public void deleteRate(Long id, Long userId, String type) {
        String sql = "DELETE FROM review_useful " +
                "WHERE review_id = ? " +
                "AND user_id = ?" +
                "AND point = ? ";

        jdbcTemplate.update(sql, id, userId, type);
    }

    @Override
    public List<String> getAllRates(Long id) {
        var rs = jdbcTemplate.queryForRowSet(
                "SELECT * FROM review_useful WHERE review_id = ? ", id);

        List<String> rate = new ArrayList<>();

        while (rs.next()) {
            rate.add(rs.getString("point"));
        }

        return rate;
    }

}
