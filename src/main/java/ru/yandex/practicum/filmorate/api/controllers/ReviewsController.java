package ru.yandex.practicum.filmorate.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.api.errors.ErrorsHandler;
import ru.yandex.practicum.filmorate.api.service.ReviewsService;
import ru.yandex.practicum.filmorate.storage.entity.Review;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewsController {

    private final ReviewsService reviewsService;
    private final ErrorsHandler errorsHandler;

    @Autowired
    public ReviewsController(ReviewsService reviewsService, ErrorsHandler errorsHandler) {
        this.reviewsService = reviewsService;
        this.errorsHandler = errorsHandler;
    }

    @PostMapping
    public void addReview(@RequestBody Review review) {

    }

    @PutMapping
    public void editReview(@RequestBody Review review) {

    }

    @DeleteMapping("/{id}")
    public void deleteReviewById(@PathVariable Long id) {

    }

    @GetMapping("{id}")
    public Review getReviewById(@PathVariable Long id) {

    }

    @GetMapping
    public List<Review> getAllReviews(@RequestParam Long filmId,
                                      @RequestParam(defaultValue = "10") Long count) {

    }

}
