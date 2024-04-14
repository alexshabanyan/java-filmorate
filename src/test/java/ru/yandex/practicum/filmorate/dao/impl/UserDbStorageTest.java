package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import utils.UserTestUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserStorage userStorage;
    private UserTestUtils userTestUtils;

    @BeforeEach
    void initialize() {
        userStorage = new UserDbStorage(jdbcTemplate);
        userTestUtils = new UserTestUtils();
    }

    @Test
    void createUser() {
        User user = userTestUtils.generateUser();

        User userToCreate = userStorage.create(user);
        User savedUser = userStorage.read(userToCreate.getId());

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(user);
    }

    @Test
    void updateUserTest() {
        final User newUser = userTestUtils.generateUser();
        final Long newUserId = userStorage.create(newUser).getId();
        final User userToUpdate = userTestUtils.generateUser(newUserId);

        userStorage.update(userToUpdate);
        final User savedUser = userStorage.read(newUserId);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(userToUpdate);
    }

    @Test
    void checkReadAllIsNotEmpty() {
        final User user = userTestUtils.generateUser();
        userStorage.create(user);
        Collection<User> users = userStorage.readAll();
        assertNotEquals(Collections.EMPTY_LIST, users);
    }

    @Test
    void checkReadIsNotEmpty() {
        final User user = userTestUtils.generateUser();
        User createdUser = userStorage.create(user);
        User userFromDb = userStorage.read(createdUser.getId());
        assertNotNull(userFromDb);
    }

    @Test
    void deleteFilmTest() {
        User user = userTestUtils.generateUser();
        final User createdUser = userStorage.create(user);
        userStorage.delete(createdUser.getId());
        assertThrows(ItemNotFoundException.class, () -> userStorage.read(createdUser.getId()));
    }
}
