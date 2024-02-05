package ru.yandex.practicum.filmorate.api.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.entity.Review;
import ru.yandex.practicum.filmorate.storage.repository.ReviewStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewStorage;

    public static final String LIKE = "LIKE";
    public static final String DISLIKE = "DISLIKE";

    public ReviewServiceImpl(ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    @Override
    public Review addReview(Review review) {
        return reviewStorage.create(review);
    }

    @Override
    public Review editReview(Review review) {
        return reviewStorage.update(review);
    }

    @Override
    public void deleteReviewById(Long id) {
        reviewStorage.delete(id);
    }

    @Override
    public Review getReviewById(Long id) {
        return reviewStorage.findById(id);
    }

    @Override
    public List<Review> getAllReviews(Long filmId, Long count) {
        return reviewStorage.getAllReviews(filmId, count).stream()
                .sorted(Comparator.comparingLong(Review::getUseful)
                        .reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Review addLike(Long id, Long userId) {
        reviewStorage.addRate(id, userId, LIKE);
        return updateReview(id);
    }

    @Override
    public Review addDislike(Long id, Long userId) {
        reviewStorage.addRate(id, userId, DISLIKE);
        return updateReview(id);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        reviewStorage.deleteRate(id, userId, LIKE);
        updateReview(id);
    }

    @Override
    public void deleteDislike(Long id, Long userId) {
        reviewStorage.deleteRate(id, userId, DISLIKE);
        updateReview(id);

    }

    private Review updateReview(Long id) {
        var review = reviewStorage.findById(id);

        long usefulnessScore = reviewStorage.getAllRates(id).stream()
                .mapToInt(rate -> rate.equals(LIKE) ? 1 : -1)
                .sum();

        review.setUseful(usefulnessScore);

        return reviewStorage.update(review);
    }


}
