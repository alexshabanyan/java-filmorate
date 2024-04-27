package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmStorage extends CrudStorage<Film> {
    void createLike(Long userId, Long filmId);

    void deleteLike(Long userId, Long filmId);

    Collection<Film> readPopular(int count);
}
