package ru.yandex.practicum.filmorate.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.api.errors.ErrorsHandler;
import ru.yandex.practicum.filmorate.api.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.entity.Review;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ErrorsHandler errorsHandler;

    @Autowired
    public ReviewController(ReviewService reviewService,
                            ErrorsHandler errorsHandler) {
        this.reviewService = reviewService;
        this.errorsHandler = errorsHandler;
    }

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review,
                            BindingResult bindingResult) {
        errorsHandler.throwValidationExceptionIfErrorsExist(bindingResult);
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review editReview(@Valid @RequestBody Review review,
                             BindingResult bindingResult) {
        errorsHandler.throwValidationExceptionIfErrorsExist(bindingResult);
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
    public List<Review> getAllReviews(@RequestParam(required = false) Long filmId,
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
