package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import utils.UserTestUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private GenreStorage genreStorage;
    private MpaStorage mpaStorage;
    private UserTestUtils userTestUtils;

    @BeforeEach
    void initialize() {
        genreStorage = new GenreDbStorage(jdbcTemplate);
        mpaStorage = new MpaDbStorage(jdbcTemplate);
        userStorage = new UserDbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, mpaStorage, genreStorage);
        userTestUtils = new UserTestUtils();
    }

    private Film generateNewFilm() {
        return generateNewFilm(null);
    }

    private Film generateNewFilm(Long id) {
        Collection<Mpa> mpaCollection = mpaStorage.findAll();
        Collection<Genre> genreCollection = genreStorage.findAll();

        Mpa mpa = mpaCollection.stream().skip(new Random().nextInt(mpaCollection.size())).findFirst().orElse(null);
        Set<Genre> genres = genreCollection
                .stream()
                .skip(new Random().nextInt(genreCollection.size())).collect(Collectors.toSet());

        Random random = new Random();
        return new Film(
                id,
                String.valueOf("Film " + random.nextInt(10000)),
                "Film description",
                LocalDate.of(2014, 8, random.nextInt(30) + 1),
                random.nextInt(100),
                mpa,
                genres
        );
    }

    @Test
    void createFilmTest() {
        Film film = generateNewFilm();
        Film filmToCreate = filmStorage.create(film);
        Film savedFilm = filmStorage.read(filmToCreate.getId());

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(film);
    }

    @Test
    void updateFilmTest() {
        final Film newFilm = generateNewFilm();
        final Long newFilmId = filmStorage.create(newFilm).getId();
        final Film filmToUpdate = generateNewFilm(newFilmId);

        filmStorage.update(filmToUpdate);
        final Film savedFilm = filmStorage.read(newFilmId);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmToUpdate);
    }

    @Test
    void checkReadAllIsNotEmpty() {
        final Film film = generateNewFilm();
        filmStorage.create(film);
        Collection<Film> films = filmStorage.readAll();
        assertNotEquals(Collections.EMPTY_LIST, films);
    }

    @Test
    void checkReadIsNotEmpty() {
        final Film film = generateNewFilm();
        Film createdFilm = filmStorage.create(film);
        Film filmFromDb = filmStorage.read(createdFilm.getId());
        assertNotNull(filmFromDb);
    }

    @Test
    void deleteFilmTest() {
        Film film = generateNewFilm();
        final Film createdFilm = filmStorage.create(film);
        filmStorage.delete(createdFilm.getId());
        assertThrows(ItemNotFoundException.class, () -> filmStorage.read(createdFilm.getId()));
    }
}
