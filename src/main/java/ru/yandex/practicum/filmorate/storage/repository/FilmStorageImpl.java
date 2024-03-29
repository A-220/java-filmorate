package ru.yandex.practicum.filmorate.storage.repository;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.api.comparator.LikesComparator;
import ru.yandex.practicum.filmorate.api.errors.exception.NotFoundException;
import ru.yandex.practicum.filmorate.api.service.FilmServiceImpl;
import ru.yandex.practicum.filmorate.storage.entity.Director;
import ru.yandex.practicum.filmorate.storage.entity.Film;
import ru.yandex.practicum.filmorate.storage.entity.Genre;
import ru.yandex.practicum.filmorate.storage.entity.Mpa;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.api.service.FilmServiceImpl.FILM_NOT_FOUND_WARN;
import static ru.yandex.practicum.filmorate.api.service.UserServiceImpl.NOT_FOUND_USER;

@Primary
@Repository
public class FilmStorageImpl implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public static final String DIRECTOR_NOT_EXIST = "Such director does not exist";

    public FilmStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private void checkFilmExist(Long id) {
        //noinspection SqlNoDataSourceInspection
        if (!(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM film WHERE film_id = ?", Integer.class, id) > 0)) {
            throw new NotFoundException(FilmServiceImpl.FILM_NOT_FOUND_WARN);
        }
    }

    public List<Mpa> mpa() {
        List<Mpa> mwList = new ArrayList<>();
        SqlRowSet mpaFromRow = jdbcTemplate.queryForRowSet("select * from motion_picture_association");
        while (mpaFromRow.next()) {
            Mpa mw = new Mpa();
            mw.setId(mpaFromRow.getInt("mpa_id"));
            mw.setName(mpaFromRow.getString("mpa_title"));
            mwList.add(mw);
        }
        return mwList;
    }

    public Mpa mpaById(Long id) {
        if (!(jdbcTemplate.queryForObject("SELECT COUNT(*)" +
                " FROM motion_picture_association " +
                "WHERE mpa_id = ?", Integer.class, id) > 0)) {
            throw new NotFoundException(FilmServiceImpl.FILM_NOT_FOUND_WARN);
        }
        Mpa mw = new Mpa();
        SqlRowSet mpaFromRow = jdbcTemplate.queryForRowSet(
                "select * from motion_picture_association where mpa_id = ?", id);
        while (mpaFromRow.next()) {
            mw.setId(mpaFromRow.getInt("mpa_id"));
            mw.setName(mpaFromRow.getString("mpa_title"));
        }
        return mw;
    }

    public List<Genre> genre() {
        List<Genre> genres = new ArrayList<>();
        SqlRowSet genreFromRow = jdbcTemplate.queryForRowSet("select * from genre");
        while (genreFromRow.next()) {
            Genre gw = new Genre();
            gw.setId(genreFromRow.getInt("genre_id"));
            gw.setName(genreFromRow.getString("genre_name"));
            genres.add(gw);
        }
        return genres;
    }

    @Override
    public Genre genreById(Long id) {
        if (!(jdbcTemplate.queryForObject("SELECT COUNT(*)" +
                " FROM film_genre " +
                "WHERE genre_id = ?", Integer.class, id) > 0)) {
            throw new NotFoundException(FilmServiceImpl.FILM_NOT_FOUND_WARN);
        }
        Genre gw = new Genre();
        SqlRowSet genreFromRow = jdbcTemplate.queryForRowSet("select * from genre where genre_id = ?", id);
        while (genreFromRow.next()) {
            gw.setId(genreFromRow.getInt("genre_id"));
            gw.setName(genreFromRow.getString("genre_name"));
        }
        return gw;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        checkFilmExist(film.getId());
        String sql = "update film set name=?, description=?, release_date=?, duration=? where film_id=?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId());

        insertFilmLikes(film, true);
        insertFilmGenre(film, true);
        insertFilmMpa(film, true);
        insertFilmDirector(film, true);

        return Optional.of(film);
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILM");
        while (filmRows.next()) {
            var film = Film.builder()
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(filmRows.getDate("release_date").toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .id(filmRows.getLong("film_id"))
                    .build();

            films.add(film);
        }

        setMpaGenreLikes(films);
        setDirectorForFilm(films);

        return films;
    }

    @Override
    public Film addFilm(Film film) {
        KeyHolder key = new GeneratedKeyHolder();
        String sql = "INSERT INTO film(name, description, release_date, duration) " +
                "VALUES(?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(film.getReleaseDate().atStartOfDay()));
            ps.setInt(4, film.getDuration());
            return ps;
        }, key);

        Long id = key.getKey().longValue();
        film.setId(id);

        insertFilmLikes(film, false);
        insertFilmGenre(film, false);
        insertFilmMpa(film, false);
        insertFilmDirector(film, false);

        return film;
    }

    public void deleteFilm(Long id) {
        checkFilmExist(id);
        Film film = getFilmById(id).orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_USER, id)));

        String deleteFromGenreFilm = "delete from film_genre where film_id = ?";
        jdbcTemplate.update(deleteFromGenreFilm, id);

        String deleteFromMpa = "delete from mpa where film_id = ?";
        jdbcTemplate.update(deleteFromMpa, id);

        String deleteFromFilm = "delete from film where film_id = ?";
        jdbcTemplate.update(deleteFromFilm, film.getId());
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        checkFilmExist(id);
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film where film_id = ? " +
                "group by film_id", id);
        if (filmRows.next()) {
            var film = filmBuilder(filmRows);

            setMpaGenreLikesToOneFilm(film);
            setDirectorForOneFilm(film);

            return Optional.of(film);
        }
        return Optional.empty();
    }

    @SneakyThrows
    @Override
    public List<Film> getSortedByDirector(Long idDirector, String string) {
        List<Film> films = new ArrayList<>();

        for (Film film : getAllFilms()) {
            for (Director director : film.getDirectors()) {
                if (director.getId().equals(idDirector)) {
                    films.add(film);
                }
            }
        }

        if (films.isEmpty()) {
            throw new NotFoundException(DIRECTOR_NOT_EXIST);
        }

        if ("year".equals(string)) {
            films.sort(Comparator.comparing(Film::getReleaseDate));
        } else if ("likes".equals(string)) {
            films.sort(new LikesComparator());
        } else {
            return Collections.emptyList();
        }
        return films;
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        List<Film> userFilms = new LinkedList<>();
        List<Film> friendFilms = new LinkedList<>();
        String sql = "SELECT *, count(user_id) AS rating FROM likes " +
                "LEFT OUTER JOIN film ON likes.film_id = film.film_id " +
                "WHERE user_id = ? " +
                "GROUP BY likes.film_id " +
                "ORDER BY rating";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, userId);
        while (filmRows.next()) {
            userFilms.add(filmBuilder(filmRows));
        }
        setMpaGenreLikes(userFilms);

        filmRows = jdbcTemplate.queryForRowSet(sql, friendId);
        while (filmRows.next()) {
            friendFilms.add(filmBuilder(filmRows));
        }
        setMpaGenreLikes(friendFilms);
        return userFilms.stream()
                .filter(friendFilms::contains)
                .collect(Collectors.toList());
    }

    public List<Film> search(String query, String title) {
        List<Film> allFilms = getAllFilms();
        List<Film> searchResults = new ArrayList<>();

        switch (title) {
            case "director":
                searchResults = allFilms.stream()
                        .filter(film -> film.getDirectors().stream()
                                .anyMatch(director -> director.getName().toLowerCase().contains(query)))
                        .collect(Collectors.toList());
                break;

            case "title":
                searchResults = allFilms.stream()
                        .filter(film -> film.getName().toLowerCase().contains(query))
                        .collect(Collectors.toList());
                break;

            default:
                searchResults.addAll(allFilms.stream()
                        .filter(film -> film.getName().toLowerCase().contains(query))
                        .collect(Collectors.toList()));

                searchResults.addAll(allFilms.stream()
                        .filter(film -> film.getDirectors().stream()
                                .anyMatch(director -> director.getName().toLowerCase().contains(query)))
                        .collect(Collectors.toList()));
                break;
        }

        searchResults.sort(new LikesComparator());

        return searchResults;
    }

    private Film filmBuilder(SqlRowSet filmRows) {
        return Film.builder()
                .name(filmRows.getString("name"))
                .description(filmRows.getString("description"))
                .releaseDate(filmRows.getDate("release_date").toLocalDate())
                .duration(filmRows.getInt("duration"))
                .id(filmRows.getLong("film_id"))
                .build();
    }

    private void insertFilmLikes(Film film, boolean update) {
        if (update) {
            String deleteLikesSql = "delete from likes where film_id=?";
            jdbcTemplate.update(deleteLikesSql, film.getId());
        }

        String insertLikesSql = "insert into likes(film_id, user_id) values (?, ?)";
        if (!film.getLikes().isEmpty()) {
            for (Long userWhoLikeId : film.getLikes()) {
                if (userWhoLikeId > 0) {
                    jdbcTemplate.update(insertLikesSql, film.getId(), userWhoLikeId);
                }
            }
        } else {
            jdbcTemplate.update(insertLikesSql, film.getId(), null);
        }
    }

    private void insertFilmGenre(Film film, boolean update) {
        if (update) {
            String deleteGenresSql = "delete from film_genre where film_id=?";
            jdbcTemplate.update(deleteGenresSql, film.getId());
        }
        String insertGenresSql = "insert into film_genre(film_id, genre_id) values (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(insertGenresSql, film.getId(), genre.getId());
        }
        if (selectFilmGenre().get(film.getId()) != null) {
            film.setGenres(selectFilmGenre().get(film.getId()));
        }
    }

    private void insertFilmMpa(Film film, boolean update) {
        if (update) {
            String deleteMpaSql = "delete from mpa where film_id=?";
            jdbcTemplate.update(deleteMpaSql, film.getId());
        }

        String insertMpaSql = "insert into mpa(film_id, mpa_id) values (?, ?)";

        jdbcTemplate.update(insertMpaSql, film.getId(), film.getMpa().getId());
    }

    private void insertFilmDirector(Film film, boolean update) {
        if (update) {
            String deleteDirectorSql = "delete from film_directors where film_id=?";
            jdbcTemplate.update(deleteDirectorSql, film.getId());
        }
        String insertMpaSql = "insert into film_directors(film_id, director_id) values (?, ?)";
        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update(insertMpaSql, film.getId(), director.getId());
            }
        } else {
            jdbcTemplate.update(insertMpaSql, film.getId(), null);
        }
    }

    private Map<Long, Set<Long>> selectFilmLikes() {
        SqlRowSet filmLikesRows = jdbcTemplate.queryForRowSet(
                "select * from likes");

        Map<Long, Set<Long>> filmsLikes = new HashMap<>();

        while (filmLikesRows.next()) {
            Long filmId = filmLikesRows.getLong("film_id");
            Long userId = filmLikesRows.getLong("user_id");

            filmsLikes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);

        }
        return filmsLikes;
    }

    public Map<Long, List<Long>> getAllLikes() {
        String sql = "SELECT * FROM likes";
        Map<Long, List<Long>> userLikes = new HashMap<>();
        try {
            jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                do {
                    if (userLikes.containsKey(rs.getLong("user_id"))) {
                        userLikes.get(rs.getLong("user_id")).add(rs.getLong("film_id"));
                    } else {
                        List<Long> films = new ArrayList<>();
                        films.add(rs.getLong("film_id"));
                        userLikes.put(rs.getLong("user_id"), films);
                    }
                } while (rs.next());
                return null;
            });
        } catch (EmptyResultDataAccessException e) {
            return Map.of();
        }
        return userLikes;
    }


    private Map<Long, Set<Genre>> selectFilmGenre() {
        SqlRowSet filmGenreRows = jdbcTemplate.queryForRowSet(
                "select * from film_genre" +
                        " LEFT OUTER JOIN genre" +
                        " ON film_genre.genre_id = genre.genre_id");

        Map<Long, Set<Genre>> filmsGenres = new HashMap<>();

        while (filmGenreRows.next()) {
            Long filmId = filmGenreRows.getLong("film_id");
            Genre gw = new Genre();
            gw.setId(filmGenreRows.getInt("genre_id"));
            gw.setName(filmGenreRows.getString("genre_name"));

            filmsGenres.computeIfAbsent(filmId,
                    k -> new TreeSet<>(Comparator.comparingInt(Genre::getId))).add(gw);

        }

        return filmsGenres;
    }

    private Map<Long, Mpa> selectFilmMpa() {
        SqlRowSet filmMpaRows = jdbcTemplate.queryForRowSet(
                "select mpa.film_id, mot.mpa_id, mot.mpa_title from mpa" +
                        " LEFT OUTER JOIN motion_picture_association AS mot" +
                        " ON mpa.mpa_id = mot.mpa_id");
        Map<Long, Mpa> filmsMpa = new HashMap<>();
        while (filmMpaRows.next()) {
            Mpa mw = new Mpa();
            Long id = filmMpaRows.getLong("film_id");
            mw.setId(filmMpaRows.getInt("mpa_id"));
            mw.setName(filmMpaRows.getString("mpa_title"));
            filmsMpa.put(id, mw);
        }
        return filmsMpa;
    }

    private void setMpaGenreLikes(List<Film> films) {
        //избегаем запроса n+1
        Map<Long, Mpa> mpa = selectFilmMpa();
        Map<Long, Set<Long>> longSetMap = selectFilmLikes();
        Map<Long, Set<Genre>> longSetGenre = selectFilmGenre();

        for (Film film : films) {
            Long id = film.getId();
            film.setMpa(mpa.get(id));
            film.setLikes(longSetMap.get(id));
            if (selectFilmGenre().get(film.getId()) != null) {
                film.setGenres(longSetGenre.get(film.getId()));
            } else {
                film.setGenres(new HashSet<>());
            }
        }
    }

    private void setMpaGenreLikesToOneFilm(Film film) {
        if (selectFilmGenre().get(film.getId()) != null) {
            film.setGenres(selectFilmGenre().get(film.getId()));
        } else {
            film.setGenres(new HashSet<>());
        }
        if (!selectFilmLikes().get(film.getId()).isEmpty()) {
            film.setLikes(selectFilmLikes().get(film.getId()));
        }
        film.setMpa(selectFilmMpa().get(film.getId()));
    }

    @Override
    public List<Film> getMostPopularFilms(Integer count, Long genreId, Integer year) {
        List<Film> mostPopularFilms = new ArrayList<>();

        for (Long id : getPopularFilmsIds(count, genreId, year)) {
            Film film = getFilmById(id).orElseThrow(() -> new NotFoundException(String.format(FILM_NOT_FOUND_WARN, id)));
            mostPopularFilms.add(film);
        }

        return new ArrayList<>(mostPopularFilms)
                .stream()
                .sorted(Comparator.comparingLong(Film::getId))
                .collect(Collectors.toList());
    }

    private List<Long> getPopularFilmsIds(Integer count, Long genreId, Integer year) {
        Optional<Long> presentGenreId = Optional.ofNullable(genreId);
        Optional<Integer> presentYear = Optional.ofNullable(year);

        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("SELECT f.film_id, COUNT(l.user_id) AS count_likes FROM film AS f ")
                .append("LEFT JOIN likes AS l ON l.film_id = f.film_id ");
        presentGenreId.map(g ->
                sqlQuery
                        .append("RIGHT JOIN film_genre AS fg ON fg.film_id = f.film_id ")
                        .append("WHERE fg.genre_id = ")
                        .append(presentGenreId.get())
        );
        presentYear.map(y ->
                sqlQuery.append(presentGenreId.isPresent() ? " AND" : " WHERE")
                        .append(" EXTRACT(YEAR FROM f.release_date) = ")
                        .append(presentYear.get())
        );
        sqlQuery.append("GROUP BY f.film_id ORDER BY count_likes DESC LIMIT ")
                .append(count);

        return jdbcTemplate.query(sqlQuery.toString(), this::getId);
    }


    private Long getId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("film_id");
    }

    private Map<Long, Director> getDirectorForFilm() {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT d.director_id, d.director_name, f.film_id " +
                        "FROM film_directors AS f " +
                        "LEFT OUTER JOIN directors AS d " +
                        "ON f.director_id = d.director_id " +
                        "WHERE f.director_id IS NOT NULL"
        );
        Map<Long, Director> filmIdDirectors = new HashMap<>();

        while (rs.next()) {
            Director director = new Director();
            director.setId(rs.getLong("director_id"));
            director.setName(rs.getString("director_name"));
            Long filmId = rs.getLong("film_id");
            filmIdDirectors.put(filmId, director);
        }

        return filmIdDirectors;
    }

    private void setDirectorForFilm(List<Film> films) {
        Map<Long, Director> filmIdDirectors = getDirectorForFilm();
        for (Film film : films) {
            Set<Director> directorSet = new HashSet<>();
            if (filmIdDirectors.containsKey(film.getId())) {
                directorSet.add(filmIdDirectors.get(film.getId()));
            }
            film.setDirectors(directorSet);
        }
    }

    private void setDirectorForOneFilm(Film film) {
        Map<Long, Director> filmIdDirectors = getDirectorForFilm();
        Set<Director> directorSet = new HashSet<>();
        if (filmIdDirectors.containsKey(film.getId())) {
            directorSet.add(filmIdDirectors.get(film.getId()));
            film.setDirectors(directorSet);
        } else {
            film.setDirectors(directorSet);
        }
    }
}
