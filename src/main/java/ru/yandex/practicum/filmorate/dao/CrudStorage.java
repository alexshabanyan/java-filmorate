package ru.yandex.practicum.filmorate.dao;

import java.util.Collection;

public interface CrudStorage<T> {
    T create(T item);

    T update(T item);

    Collection<T> readAll();

    T read(Long id);

    void delete(Long id);

    boolean isExist(Long id);
}
