package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.annotations.BirthdayDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class BirthdayDateValidator implements ConstraintValidator<BirthdayDate, LocalDate> {
    private static final LocalDate NOW_DATE = LocalDate.now();

    @Override
    public boolean isValid(LocalDate birthday, ConstraintValidatorContext context) {
        return !birthday.isAfter(NOW_DATE);
    }
}
