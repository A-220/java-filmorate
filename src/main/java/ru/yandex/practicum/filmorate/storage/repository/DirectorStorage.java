package ru.yandex.practicum.filmorate.storage.repository;

import ru.yandex.practicum.filmorate.storage.entity.Director;

import java.util.List;

public interface DirectorStorage {
    List<Director> getAll();

    Director get(Long id);

    Director create(Director director);

    Director update(Director director);

    void delete(Long id);
}
