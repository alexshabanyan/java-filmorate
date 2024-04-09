package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "insert into public.film (name, description, release_date, duration, mpa_id) values (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        Long filmId = (long) (int) keyHolder.getKey();
        setGenres(film, filmId);
        return findFilm(filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public Collection<Film> findAll() {
        return List.of();
    }

    @Override
    public Film findFilm(Long id) {
        String sql = "SELECT * FROM film WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> filmMapper(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotFoundException(id);
        }
    }

    private Set<Genre> getGenres(Long filmId) {
        String sql = "SELECT g.ID, g.NAME FROM FILM_GENRE fg JOIN GENRE g ON fg.GENRE_ID = g.ID WHERE fg.FILM_ID  = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String name = rs.getString("name");
            return new Genre(id, name);
        }, filmId));
    }

    private void setGenres(Film film, Long filmId) {
        if (film.getGenres() == null) return;
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                    filmId, genre.getId());
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
