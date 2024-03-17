package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService {
    UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        Long id = user.getId();
        if (id != null & userStorage.isUserContains(id)) {
            throw new ItemAlreadyExistException(id);
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new ValidationException("Отсутствует идендификатор пользователя");
        } else if (getUser(user.getId()) == null) {
            throw new ItemNotFoundException(user.getId());
        }
        return userStorage.updateUser(user);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User getUser(Long id) {
        User user = userStorage.getUser(id);
        if (user == null) {
            throw new ItemNotFoundException(id);
        }
        return user;
    }

    public User addFriend(Long id, Long friendId) {
        User user = getUser(id);
        User friend = getUser(friendId);

        if (user == null) {
            throw new ItemNotFoundException(id);
        } else if (friend == null) {
            throw new ItemNotFoundException(friendId);
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(id);

        updateUser(user);
        updateUser(friend);

        return user;
    }

    public User deleteFriend(Long id, Long friendId) {
        User user = getUser(id);
        User friend = getUser(friendId);

        if (user == null) {
            throw new ItemNotFoundException(id);
        } else if (friend == null) {
            throw new ItemNotFoundException(friendId);
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);

        updateUser(user);
        updateUser(friend);

        return user;
    }

    public Collection<User> getFriends(Long id) {
        User user = getUser(id);
        if (user == null) {
            throw new ItemNotFoundException(id);
        }
        return getUser(id).getFriends().stream()
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long id, Long friendId) {
        User user = getUser(id);
        if (user == null) {
            throw new ItemNotFoundException(id);
        }
        return getUser(id).getFriends().stream()
                .filter(i -> containsFriend(friendId, i)).collect(Collectors.toSet()).stream()
                .map(this::getUser).collect(Collectors.toList());
    }

    public Boolean containsFriend(Long id, Long friendId) {
        return getUser(id).getFriends().contains(friendId);
    }
}
