package ru.yandex.practicum.filmorate.api.service;

import ru.yandex.practicum.filmorate.storage.entity.Director;

import java.util.List;

public interface DirectorService {
     List<Director> getAll();

     Director getById(Long id);

     Director create(Director director);

     Director update(Director director);

     void delete(Long id);
}
