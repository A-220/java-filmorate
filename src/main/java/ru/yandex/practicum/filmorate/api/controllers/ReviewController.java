package ru.yandex.practicum.filmorate.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.api.errors.ErrorsHandler;
import ru.yandex.practicum.filmorate.api.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.entity.Review;

import java.util.List;


@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review addReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review editReview(@RequestBody Review review) {
        return reviewService.editReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReviewById(@PathVariable Long id) {
        reviewService.deleteReviewById(id);
    }

    @GetMapping("{id}")
    public Review getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getAllReviews(@RequestParam Long filmId,
                                      @RequestParam(defaultValue = "10") Long count) {
        return reviewService.getAllReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public Review addLike(@PathVariable Long id,
                          @PathVariable Long userId) {
        return reviewService.addLike(id, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public Review addDislike(@PathVariable Long id,
                             @PathVariable Long userId) {
        return reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id,
                           @PathVariable Long userId) {
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable Long id,
                              @PathVariable Long userId) {
        reviewService.deleteDislike(id, userId);
    }
}
