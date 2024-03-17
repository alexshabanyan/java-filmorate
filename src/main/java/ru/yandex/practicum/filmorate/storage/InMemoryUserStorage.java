package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long sequenceId = 1L;

    public User addUser(User user) {
        if (user == null) {
            log.error("Произошла ошибка создания пользователя. Отсутствует пользователь для создания");
            throw new ValidationException("Отсутствует пользователь для создания");
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    public User updateUser(User user) {
        User updUser = users.get(user.getId());
        if (updUser == null) {
            log.error("Произошла ошибка обновления пользователя. Отсутствует пользователь для обновления");
            throw new ValidationException("Отсутствует пользователь для обновления");
        }
        users.put(updUser.getId(), user);
        log.info("Обновлен пользователь {}", user);
        return user;
    }

    public Collection<User> findAll() {
        log.info("Получение списка пользователей");
        return users.values();
    }

    public User getUser(Long id) {
        return users.get(id);
    }

    public Boolean isUserContains(Long id) {
        return users.containsKey(id);
    }

    public Long generateId() {
        return sequenceId++;
    }
}
