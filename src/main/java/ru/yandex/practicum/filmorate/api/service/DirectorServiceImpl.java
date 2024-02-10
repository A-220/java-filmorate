package ru.yandex.practicum.filmorate.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.entity.Director;
import ru.yandex.practicum.filmorate.storage.repository.DirectorStorage;

import java.util.List;

@Service
@Slf4j
public class DirectorServiceImpl implements DirectorService {
    public static final String CREATE_DIRECTOR = "successfully create director with id {}";

    public static final String UPDATE_DIRECTOR = "successfully update director with id {}";

    public static final String DELETE_DIRECTOR = "successfully delete director with id {}";

    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorServiceImpl(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    @Override
    public List<Director> getAll() {
        return directorStorage.getAll();
    }

    @Override
    public Director getById(Long id) {
        return directorStorage.getById(id);
    }

    @Override
    public Director create(Director director) {
        var dir = directorStorage.create(director);
        log.info(CREATE_DIRECTOR, director.getId());
        return dir;
    }

    @Override
    public Director update(Director director) {
        var dir = directorStorage.update(director);
        log.info(UPDATE_DIRECTOR, director.getId());
        return dir;
    }

    @Override
    public void delete(Long id) {
        directorStorage.delete(id);
        log.info(DELETE_DIRECTOR, id);
    }
}
