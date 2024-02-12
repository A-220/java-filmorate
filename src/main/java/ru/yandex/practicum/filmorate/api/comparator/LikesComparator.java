package ru.yandex.practicum.filmorate.api.comparator;

import ru.yandex.practicum.filmorate.storage.entity.Film;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public class LikesComparator implements Comparator<Film> {
    @Override
    public int compare(Film o1, Film o2) {
        Set<Long> setO1 = o1.getLikes().stream().filter(o -> o > 0).collect(Collectors.toSet());
        Set<Long> setO2 = o2.getLikes().stream().filter(o -> o > 0).collect(Collectors.toSet());

        return Integer.compare(setO2.size(), setO1.size());
    }
}
