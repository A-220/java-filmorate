package ru.yandex.practicum.filmorate.api.errors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import ru.yandex.practicum.filmorate.api.errors.exception.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ErrorsHandler {
    public void throwValidationExceptionIfErrorsExist(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            throw new ValidationException(String.join(",", errors));
        }
    }

}
