package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        String sqlQuery = "insert into public.users (email, login, name, birthday) values (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        return user;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public Collection<User> findAll() {
        return List.of();
    }

    @Override
    public User getUser(Long id) {
        return null;
    }

    @Override
    public Boolean isUserContains(Long id) {
        return null;
    }

    @Override
    public Long generateId() {
        return 0L;
    }
}
