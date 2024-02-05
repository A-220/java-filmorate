package ru.yandex.practicum.filmorate.api.service;

import ru.yandex.practicum.filmorate.storage.entity.Review;

import java.util.List;

public interface ReviewService {
    Review addReview(Review review);

    Review editReview(Review review);

    void deleteReviewById(Long id);

    Review getReviewById(Long id);

    List<Review> getAllReviews(Long filmId, Long count);

    Review addLike(Long id, Long userId);

    Review addDislike(Long id, Long userId);

    void deleteLike(Long id, Long userId);

    void deleteDislike(Long id, Long userId);

}
