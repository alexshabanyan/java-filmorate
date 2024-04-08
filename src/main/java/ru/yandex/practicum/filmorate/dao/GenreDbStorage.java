package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> findAll() {
        String sqlQuery = "select * from genre";
        return jdbcTemplate.query(sqlQuery, this::mapToRowGenre);
    }

    @Override
    public Genre getGenre(Long id) {
        String sqlQuery = "select * from genre where id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapToRowGenre, id);
    }

    private Genre mapToRowGenre(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        return new Genre(id, name);
    }
}
