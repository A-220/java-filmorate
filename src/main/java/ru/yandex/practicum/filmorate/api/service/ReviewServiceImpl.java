package ru.yandex.practicum.filmorate.api.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.entity.Event;
import ru.yandex.practicum.filmorate.storage.entity.Review;
import ru.yandex.practicum.filmorate.storage.repository.FeedStorage;
import ru.yandex.practicum.filmorate.storage.repository.ReviewStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewStorage;
    private final FeedStorage feedRepository;

    public static final String LIKE = "LIKE";
    public static final String DISLIKE = "DISLIKE";

    public ReviewServiceImpl(ReviewStorage reviewStorage, FeedStorage feedRepository) {
        this.reviewStorage = reviewStorage;
        this.feedRepository = feedRepository;
    }

    @Override
    public Review addReview(Review review) {
        Review addReview = reviewStorage.create(review);
        feedRepository.saveToFeed(new Event(System.currentTimeMillis(), review.getUserId(),
                "REVIEW", "ADD", review.getReviewId()));
        return addReview;
    }

    @Override
    public Review editReview(Review review) {
        Review editReview =  reviewStorage.update(review);
        feedRepository.saveToFeed(new Event(System.currentTimeMillis(), editReview.getUserId(),
                "REVIEW", "UPDATE", editReview.getReviewId()));
        return editReview;
    }

    @Override
    public void deleteReviewById(Long id) {
        Long userId = getReviewById(id).getUserId();
        reviewStorage.delete(id);
        feedRepository.saveToFeed(new Event(System.currentTimeMillis(), userId,
                "REVIEW", "REMOVE", id));
    }

    @Override
    public Review getReviewById(Long id) {
        return reviewStorage.findById(id);
    }

    @Override
    public List<Review> getAllReviews(Long filmId, Long count) {
        Stream<Review> sortedReview = reviewStorage.getAllReviews(count).stream()
                .sorted(Comparator.comparingLong(Review::getUseful).reversed());
        if (filmId != null) {
            sortedReview = sortedReview
                    .filter(review -> review.getFilmId().equals(filmId));
        }
        return sortedReview.collect(Collectors.toList());
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

        System.out.println(review);
        System.out.println(reviewStorage.getAllRates(id));
        long usefulnessScore = reviewStorage.getAllRates(id).stream()
                .mapToInt(rate -> rate.equals(LIKE) ? 1 : -1)
                .sum();

        return reviewStorage.updateUseful(id, usefulnessScore);
    }


}
