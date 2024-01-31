package ru.yandex.practicum.filmorate.api.service;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.storage.entity.Director;

import java.util.List;

public interface DirectorService {
     List<Director> getAll();

     Director get(Long id);

     Director create(Director director);

     Director update(Director director);

     void delete(Long id);
}
