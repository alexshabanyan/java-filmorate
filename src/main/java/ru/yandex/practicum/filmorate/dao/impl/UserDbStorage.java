package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserStorage;

import java.sql.*;
import java.util.Collection;

@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        String sqlQuery = "insert into public.users (email, login, name, birthday) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        Long id = (long) (int) keyHolder.getKey();
        return read(id);
    }

    @Override
    public User update(User item) {
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? where id = ?";
        jdbcTemplate.update(sqlQuery,
                item.getEmail(),
                item.getLogin(),
                item.getName(),
                item.getBirthday(),
                item.getId()
        );
        return read(item.getId());
    }

    @Override
    public Collection<User> readAll() {
        String sqlQuery = "SELECT * FROM USERS";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> userMapper(rs));
    }

    @Override
    public User read(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> userMapper(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotFoundException(id);
        }
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        read(userId);
        read(friendId);
        String userSqlQuery = "SELECT * FROM friendship where user_id = ? and send_to = ?";
        SqlRowSet userFriendship = jdbcTemplate.queryForRowSet(userSqlQuery, userId, friendId);
        SqlRowSet friendFriendship = jdbcTemplate.queryForRowSet(userSqlQuery, friendId, userId);

        if (!userFriendship.next()) {
            jdbcTemplate.update("INSERT INTO friendship (user_id, send_to) VALUES (?, ?)",
                    userId, friendId);
        }
        if (friendFriendship.next()) {
            jdbcTemplate.update("UPDATE friendship SET status = ? WHERE user_id IN (?, ?) AND send_to IN (?, ?)",
                   true, userId, friendId, userId, friendId);
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        read(userId);
        read(friendId);
        int updated = jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ? AND send_to = ?",
                userId, friendId);
        if (updated > 0) {
            jdbcTemplate.update("UPDATE friendship SET approved = ? WHERE user_id = ? AND send_to = ?",
                    false, friendId, userId);
        }
    }

    @Override
    public Collection<User> getFriends(Long id) {
        read(id);
        return jdbcTemplate.query("SELECT u.* FROM friendship AS fr " +
                        "LEFT JOIN users AS u ON fr.send_to = u.id WHERE fr.user_id = ?",
                (rs, rowNum) -> userMapper(rs), id);
    }

    private User userMapper(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");

        return User.builder()
                .id(id)
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }
}
