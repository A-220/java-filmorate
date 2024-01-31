package ru.yandex.practicum.filmorate.api.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.entity.Director;
import ru.yandex.practicum.filmorate.storage.repository.DirectorStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class DirectorServiceImpl implements DirectorService {

    private final DirectorStorage directorStorage;

    @Override
    public List<Director> getAll() {
        return directorStorage.getAll();
    }

    @Override
    public Director get(Long id) {
        return directorStorage.get(id);
    }

    @Override
    public Director create(Director director) {
        return directorStorage.create(director);
    }

    @Override
    public Director update(Director director) {
        return directorStorage.update(director);
    }

    @Override
    public void delete(Long id) {
        directorStorage.delete(id);
    }
}
