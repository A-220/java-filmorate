package ru.yandex.practicum.filmorate.utils;

public class IncrementUtil {
    private static Long usersId = 0L;
    private static Long filmsId = 0L;

    public static Long getIncrementUserId() {
        return ++usersId;
    }

    public static Long getIncrementFilmId() {
        return ++filmsId;
    }

}
