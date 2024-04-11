package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.dao.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Mpa> findAll() {
        String sqlQuery = "select * from mpa";
        return jdbcTemplate.query(sqlQuery, this::mapToRowMpa);
    }

    @Override
    public Mpa getMpa(Long id) {
        String sqlQuery = "select * from mpa where id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapToRowMpa, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotFoundException(id, "Рейтинг не найден id=" + id);
        }
    }

    private Mpa mapToRowMpa(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        return new Mpa(id, name);
    }
}
