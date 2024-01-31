DROP TABLE IF EXISTS likes, friends, users, mpa, film_genre,
    motion_picture_association, film, genre, film_directors, directors;

CREATE TABLE IF NOT EXISTS motion_picture_association
(
    mpa_id    integer GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY,
    mpa_title varchar(50)
);

CREATE TABLE IF NOT EXISTS genre
(
    genre_id   integer GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY,
    genre_name varchar(50)
);

CREATE TABLE IF NOT EXISTS film
(
    film_id      INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY,
    name         VARCHAR(200),
    description  VARCHAR(400),
    release_date TIMESTAMP,
    duration     INTEGER,
    mpa          INTEGER,
    genre_id     INTEGER,
    director_id  INTEGER
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  INTEGER,
    genre_id INTEGER,
    foreign key (genre_id) references genre (genre_id),
    foreign key (film_id) references film (film_id)
);

CREATE TABLE IF NOT EXISTS mpa
(
    film_id integer,
    mpa_id  integer,
    FOREIGN KEY (film_id) REFERENCES film (film_id),
    FOREIGN KEY (mpa_id) REFERENCES motion_picture_association (mpa_id)
);

CREATE TABLE IF NOT EXISTS users
(
    user_id  integer GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY,
    login    varchar(200),
    name     varchar(200),
    email    varchar(200),
    birthday timestamp
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id  integer,
    users_id integer,
    status   varchar(50),
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    UNIQUE (user_id, users_id, status)
);

CREATE TABLE IF NOT EXISTS likes
(
    film_id integer,
    user_id integer,
    FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    UNIQUE (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS directors
(
    director_id integer GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY,
    director_name varchar(200)
);

CREATE TABLE IF NOT EXISTS film_directors
(
    director_id integer REFERENCES directors(director_id) ON DELETE CASCADE,
    film_id integer
);

INSERT INTO motion_picture_association (mpa_title)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');

INSERT INTO genre (genre_name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');