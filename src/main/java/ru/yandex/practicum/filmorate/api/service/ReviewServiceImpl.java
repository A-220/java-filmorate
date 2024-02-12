package ru.yandex.practicum.filmorate.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.entity.Event;
import ru.yandex.practicum.filmorate.storage.entity.Review;
import ru.yandex.practicum.filmorate.storage.entity.enums.EventType;
import ru.yandex.practicum.filmorate.storage.entity.enums.Operation;
import ru.yandex.practicum.filmorate.storage.repository.FeedStorage;
import ru.yandex.practicum.filmorate.storage.repository.FilmStorage;
import ru.yandex.practicum.filmorate.storage.repository.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.repository.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewRepository;
    private final UserStorage userRepository;
    private final FilmStorage filmRepository;
    private final FeedStorage feedRepository;

    public static final String CREATE_REVIEW = "successfully create review with id {}";

    public static final String UPDATE_REVIEW = "successfully update review with id {}";

    public static final String DELETE_REVIEW = "successfully delete review with id {}";
    public static final String LIKE = "LIKE";
    public static final String DISLIKE = "DISLIKE";

    public ReviewServiceImpl(ReviewStorage reviewRepository, UserStorage userRepository, FilmStorage filmRepository, FeedStorage feedRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.filmRepository = filmRepository;
        this.feedRepository = feedRepository;
    }

    @Override
    public Review addReview(Review review) {
        userRepository.getUserById(review.getUserId());
        filmRepository.getFilmById(review.getFilmId());

        Review addReview = reviewRepository.create(review);

        log.info(CREATE_REVIEW, review.getReviewId());

        feedRepository.saveToFeed(new Event(System.currentTimeMillis(), review.getUserId(),
                EventType.REVIEW, Operation.ADD, review.getReviewId()));
        return addReview;
    }

    @Override
    public Review editReview(Review review) {
        userRepository.getUserById(review.getUserId());
        filmRepository.getFilmById(review.getFilmId());

        Review editReview = reviewRepository.update(review);

        log.info(UPDATE_REVIEW, review.getReviewId());

        feedRepository.saveToFeed(new Event(System.currentTimeMillis(), editReview.getUserId(),
                EventType.REVIEW, Operation.UPDATE, editReview.getReviewId()));
        return editReview;
    }

    @Override
    public void deleteReviewById(Long id) {
        Long userId = getReviewById(id).getUserId();
        reviewRepository.delete(id);

        log.info(DELETE_REVIEW, id);

        feedRepository.saveToFeed(new Event(System.currentTimeMillis(), userId,
                EventType.REVIEW, Operation.REMOVE, id));
    }

    @Override
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public List<Review> getAllReviews(Long filmId, Long count) {
        Stream<Review> sortedReview = reviewRepository.getAllReviews(count).stream()
                .sorted(Comparator.comparingLong(Review::getUseful).reversed());
        if (filmId != null) {
            sortedReview = sortedReview
                    .filter(review -> review.getFilmId().equals(filmId));
        }
        return sortedReview.collect(Collectors.toList());
    }

    @Override
    public Review addLike(Long id, Long userId) {
        reviewRepository.addRate(id, userId, LIKE);
        return updateReview(id);
    }

    @Override
    public Review addDislike(Long id, Long userId) {
        reviewRepository.addRate(id, userId, DISLIKE);
        return updateReview(id);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        reviewRepository.deleteRate(id, userId, LIKE);
        updateReview(id);
    }

    @Override
    public void deleteDislike(Long id, Long userId) {
        reviewRepository.deleteRate(id, userId, DISLIKE);
        updateReview(id);

    }

    private Review updateReview(Long id) {
        reviewRepository.findById(id);

        long usefulnessScore = reviewRepository.getAllRates(id).stream()
                .mapToInt(rate -> rate.equals(LIKE) ? 1 : -1)
                .sum();

        return reviewRepository.updateUseful(id, usefulnessScore);
    }


}
