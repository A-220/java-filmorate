package ru.yandex.practicum.filmorate.storage.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.entity.Film;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;

@Component("FilmStorageJdbc")
@Primary
public class FilmStorageImpl implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private void checkFilmExist(Long id) {
        if (!(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM film WHERE film_id = ?", Integer.class, id) > 0)) {
            throw new IllegalArgumentException("Не верный id");
        }
    }

    public List<Film.Mpa> mpa() {
        List<Film.Mpa> mwList = new ArrayList<>();
        SqlRowSet mpaFromRow = jdbcTemplate.queryForRowSet("select * from motion_picture_association");
        while (mpaFromRow.next()) {
            Film.Mpa mw = new Film.Mpa();
            mw.setId(mpaFromRow.getInt("mpa_id"));
            mw.setName(mpaFromRow.getString("mpa_title"));
            mwList.add(mw);
        }
        return mwList;
    }

    public Film.Mpa mpaById(Long id) {
        if (!(jdbcTemplate.queryForObject("SELECT COUNT(*)" +
                " FROM motion_picture_association " +
                "WHERE mpa_id = ?", Integer.class, id) > 0)) {
            throw new IllegalArgumentException("Не верный id");
        }
        Film.Mpa mw = new Film.Mpa();
        SqlRowSet mpaFromRow = jdbcTemplate.queryForRowSet(
                "select * from motion_picture_association where mpa_id = ?", id);
        while (mpaFromRow.next()) {
            mw.setId(mpaFromRow.getInt("mpa_id"));
            mw.setName(mpaFromRow.getString("mpa_title"));
        }
        return mw;
    }

    public List<Film.Genre> genre() {
        List<Film.Genre> genres = new ArrayList<>();
        SqlRowSet genreFromRow = jdbcTemplate.queryForRowSet("select * from genre");
        while (genreFromRow.next()) {
            Film.Genre gw = new Film.Genre();
            gw.setId(genreFromRow.getInt("genre_id"));
            gw.setName(genreFromRow.getString("genre_name"));
            genres.add(gw);
        }
        return genres;
    }

    public Film.Genre genreById(int id) {
        if (!(jdbcTemplate.queryForObject("SELECT COUNT(*)" +
                " FROM film_genre " +
                "WHERE genre_id = ?", Integer.class, id) > 0)) {
            throw new IllegalArgumentException("Не верный id");
        }
        Film.Genre gw = new Film.Genre();
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

        return film;
    }

    public void delete(Long id) {
        checkFilmExist(id);
        String sql = "delete from film where film_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        checkFilmExist(id);
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film where film_id = ? " +
                "group by film_id", id);
        if (filmRows.next()) {
            var film = Film.builder()
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(filmRows.getDate("release_date").toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .id(filmRows.getLong("film_id"))
                    .build();
            setMpaGenreLikesToOneFilm(film);

            return Optional.of(film);
        }
        return Optional.empty();
    }

    public List<Film> getTopFilms(Integer count) {
        List<Film> topFilms = new LinkedList<>();
        String sql = "select * " +
                "from film " +
                "where film_id in (" +
                "select film_id " +
                "from likes " +
                "group by film_id " +
                "order by count(user_id) desc " +
                "limit ?)";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, count);
        while (filmRows.next()) {
            var film = Film.builder()
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(filmRows.getDate("release_date").toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .id(filmRows.getLong("film_id"))
                    .build();

            topFilms.add(film);
        }

        setMpaGenreLikes(topFilms);

        return new ArrayList<>(topFilms);
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
        for (Film.Genre genre : film.getGenres()) {
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

    private HashMap<Integer, Set<Film.Genre>> selectFilmGenre() {
        SqlRowSet filmGenreRows = jdbcTemplate.queryForRowSet(
                "select * from film_genre" +
                        " LEFT OUTER JOIN genre" +
                        " ON film_genre.genre_id = genre.genre_id");

        HashMap<Integer, Set<Film.Genre>> filmsGenres = new HashMap<>();

        while (filmGenreRows.next()) {
            int filmId = filmGenreRows.getInt("film_id");
            Film.Genre gw = new Film.Genre();
            gw.setId(filmGenreRows.getInt("genre_id"));
            gw.setName(filmGenreRows.getString("genre_name"));

            filmsGenres.computeIfAbsent(filmId,
                    k -> new TreeSet<>(Comparator.comparingInt(Film.Genre::getId))).add(gw);

        }

        return filmsGenres;
    }

    private HashMap<Integer, Film.Mpa> selectFilmMpa() {
        SqlRowSet filmMpaRows = jdbcTemplate.queryForRowSet(
                "select mpa.film_id, mot.mpa_id, mot.mpa_title from mpa" +
                        " LEFT OUTER JOIN motion_picture_association AS mot" +
                        " ON mpa.mpa_id = mot.mpa_id");
        HashMap<Integer, Film.Mpa> filmsMpa = new HashMap<>();
        while (filmMpaRows.next()) {
            Film.Mpa mw = new Film.Mpa();
            int id = filmMpaRows.getInt("film_id");
            mw.setId(filmMpaRows.getInt("mpa_id"));
            mw.setName(filmMpaRows.getString("mpa_title"));
            filmsMpa.put(id, mw);
        }
        return filmsMpa;
    }

    private void setMpaGenreLikes(List<Film> films) {
        for (Film film : films) {
            Long id = film.getId();
            film.setMpa(selectFilmMpa().get(id));
            film.setLikes(selectFilmLikes().get(id));
            if (selectFilmGenre().get(film.getId()) != null) {
                film.setGenres(selectFilmGenre().get(film.getId()));
            }
        }
    }

    private void setMpaGenreLikesToOneFilm(Film film) {
        if (selectFilmGenre().get(film.getId()) != null) {
            film.setGenres(selectFilmGenre().get(film.getId()));
        }
        if (!selectFilmLikes().get(film.getId()).isEmpty()) {
            film.setLikes(selectFilmLikes().get(film.getId()));
        }
        film.setMpa(selectFilmMpa().get(film.getId()));
    }
}
