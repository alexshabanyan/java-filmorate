package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.dao.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaDbStorage;
    private final GenreStorage genreDbStorage;

    @Override
    public Film create(Film film) {
        String sqlQuery = "insert into public.film (name, description, release_date, duration) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            return ps;
        }, keyHolder);

        Long filmId = (long) (int) keyHolder.getKey();
        setGenres(film, filmId);
        setMpa(film, filmId);
        return read(filmId);
    }

    @Override
    public Film update(Film item) {
        String sqlQuery = "UPDATE film SET name = ?, description = ?, duration = ?, release_date = ?, mpa_id = ?" +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                item.getName(),
                item.getDescription(),
                item.getDuration(),
                item.getReleaseDate(),
                item.getMpa().getId(),
                item.getId()
        );
        setGenres(item, item.getId());
        return read(item.getId());
    }

    @Override
    public Collection<Film> readAll() {
        String sqlQuery = "SELECT * FROM FILM";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> filmMapper(rs));
    }

    @Override
    public Film read(Long id) {
        String sql = "SELECT * FROM film WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> filmMapper(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotFoundException(id);
        }
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM film_genre where film_id = ?", id);
        jdbcTemplate.update("DELETE FROM film where id = ?", id);
    }

    @Override
    public void createLike(Long userId, Long filmId) {
        String sqlQuery = "INSERT INTO FILM_LIKES (user_id, film_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public void deleteLike(Long userId, Long filmId) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE USER_ID = ? AND FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public Collection<Film> readPopular(int count) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, count(fl.film_id)" +
                " from film_likes fl join film f on fl.film_id = f.id group by fl.film_id order by count(fl.film_id) desc limit ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> filmMapper(rs), count);
    }

    @Override
    public boolean isExist(Long id) {
        return false;
    }

    private Set<Genre> getGenres(Long filmId) {
        String sql = "SELECT g.ID, g.NAME FROM FILM_GENRE fg JOIN GENRE g ON fg.GENRE_ID = g.ID WHERE fg.FILM_ID  = ? ORDER BY g.ID";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String name = rs.getString("name");
            return new Genre(id, name);
        }, filmId)
                .stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void setGenres(Film film, Long filmId) {
        if (film.getGenres() == null) return;
        Set<Long> allGenres = genreDbStorage.findAll().stream().map(Genre::getId).collect(Collectors.toSet());
        Set<Long> currentFilmGenres = getGenres(filmId).stream().map(Genre::getId).collect(Collectors.toSet());
        Set<Long> newFilmGenres = film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());

        Optional<Long> messingGenre = newFilmGenres.stream()
                .filter(id -> !allGenres.contains(id)).findFirst();

        if (messingGenre.isPresent()) {
            throw new ValidationException("Такого жанра нет id=" + messingGenre);
        }

        for (Long id : currentFilmGenres) {
            if (!newFilmGenres.contains(id)) {
                jdbcTemplate.update("DELETE FROM film_genre WHERE genre_id = ? AND film_id = ?", id, filmId);
            }
        }

        for (Long id : newFilmGenres) {
            if (!currentFilmGenres.contains(id)) {
                jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)", filmId, id);
            }
        }
    }

    private void setMpa(Film film, Long id) {
        if (film.getMpa() == null) return;
        try {
            Mpa mpa = mpaDbStorage.getMpa(film.getMpa().getId());
            jdbcTemplate.update("UPDATE film SET mpa_id = ? WHERE id = ?",
                    mpa.getId(), id);
        } catch (ItemNotFoundException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    private Film filmMapper(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Mpa mpa = null;
        if (rs.getLong("mpa_id") != 0) {
            mpa = mpaDbStorage.getMpa(rs.getLong("mpa_id"));
        }

        Set<Genre> genres = getGenres(id);

        return Film.builder()
                .id(id)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpa)
                .genres(genres)
                .build();
    }
}
