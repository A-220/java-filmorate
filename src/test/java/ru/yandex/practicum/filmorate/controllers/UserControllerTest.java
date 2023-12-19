package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.repository.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

class UserControllerTest {

    UserController userController;
    private static Validator validator;

    @BeforeAll
    public static void getValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @BeforeEach
    void setUp() {
        userController = new UserController(new UserServiceImpl(new InMemoryUserStorage()));
    }

    @Test
    void addUserTest() {
        var user = getUser();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(violations.size(), 0);
    }

    @Test
    void validateEmailTest() {
        var user = getUser();
        user.setEmail("badEmail");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(violations.size(), 1);
    }

    @Test
    void validateLoginTest() {
        var user = getUser();

        user.setLogin("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(violations.size(), 1);

        user.setLogin("bad login");
        violations = validator.validate(user);
        Assertions.assertEquals(violations.size(), 1);
    }

    @Test
    void validateBirthdayTest() {
        var user = getUser();
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(violations.size(), 1);
    }


    private User getUser() {
        return User.builder()
                .id(0L)
                .email("mail@test")
                .login("test")
                .name("test name")
                .birthday(LocalDate.now().minusDays(2))
                .build();
    }

}