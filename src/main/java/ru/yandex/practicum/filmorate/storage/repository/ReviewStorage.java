package ru.yandex.practicum.filmorate.storage.repository;

import ru.yandex.practicum.filmorate.storage.entity.Review;

import java.util.List;

public interface ReviewStorage {
    Review findById(Long id);

    List<Review> getAllReviews(Long count);

    Review create(Review review);

    Review update(Review review);

    Review updateUseful(Long reviewId, Long useful);

    void delete(Long id);

    void addRate(Long id, Long userId, String type);

    void deleteRate(Long id, Long userId, String type);

    List<String> getAllRates(Long id);
}
