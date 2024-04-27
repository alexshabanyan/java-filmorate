package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserStorage;

import java.util.Collection;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    public Collection<User> findAll() {
        return userStorage.readAll();
    }

    public User getUser(Long id) {
        User user = userStorage.read(id);
        if (user == null) {
            throw new ItemNotFoundException(id);
        }
        return user;
    }

    public void deleteUser(Long id) {
        userStorage.delete(id);
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public Collection<User> getFriends(Long id) {
        return userStorage.getFriends(id);
    }

    public void deleteFriend(Long id, Long friendId) {
        userStorage.deleteFriend(id, friendId);
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        Collection<User> userFriends = userStorage.getFriends(id);
        Collection<User> friendFriends = userStorage.getFriends(otherId);
        userFriends.retainAll(friendFriends);
        return userFriends;
    }
//
//    public Collection<User> getFriends(Long id) {
//        User user = getUser(id);
//        if (user == null) {
//            throw new ItemNotFoundException(id);
//        }
//        return getUser(id).getFriends().stream()
//                .map(this::getUser)
//                .collect(Collectors.toList());
//    }
//
//    public Collection<User> getCommonFriends(Long id, Long friendId) {
//        User user = getUser(id);
//        if (user == null) {
//            throw new ItemNotFoundException(id);
//        }
//        return getUser(id).getFriends().stream()
//                .filter(i -> containsFriend(friendId, i)).collect(Collectors.toSet()).stream()
//                .map(this::getUser).collect(Collectors.toList());
//    }
//
//    public Boolean containsFriend(Long id, Long friendId) {
//        return getUser(id).getFriends().contains(friendId);
//    }
}
