package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    Collection<User> findAll();

    User getUser(Long id);

    Boolean isUserContains(Long id);

    Long generateId();
}
