package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer sequenceId = 1;

    @PostMapping()
    public User addUser(@Valid @RequestBody User user) {
        if (user == null) {
            log.error("Произошла ошибка создания пользователя. Отсутствует пользователь для создания");
            throw new ValidationException("Отсутствует пользователь для создания");
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    @PutMapping()
    public User updateUser(@RequestBody User user) {
        User updUser = users.get(user.getId());
        if (updUser == null) {
            log.error("Произошла ошибка обновления пользователя. Отсутствует пользователь для обновления");
            throw new ValidationException("Отсутствует пользователь для обновления");
        }
        users.put(updUser.getId(), user);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        log.info("Обновлен пользователь {}", user);
        return user;
    }

    @GetMapping()
    public Collection<User> findAll() {
        log.info("Получение списка пользователей");
        return users.values();
    }

    public Integer generateId() {
        return sequenceId++;
    }
}
