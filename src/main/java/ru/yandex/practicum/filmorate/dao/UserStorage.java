package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

public interface UserStorage extends CrudStorage<User> {
    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    Collection<User> getFriends(Long id);
}
