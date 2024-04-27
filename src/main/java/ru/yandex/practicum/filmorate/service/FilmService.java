package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmStorage;

import java.util.Collection;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Отсутствует идентификатор фильма");
        }
        return filmStorage.update(film);
    }

    public Collection<Film> findAll() {
        return filmStorage.readAll();
    }

    public Film getFilm(Long id) {
        Film film = filmStorage.read(id);
        if (film == null) {
            throw new ItemNotFoundException(id);
        }
        return film;
    }

    public void deleteFilm(Long id) {
        filmStorage.delete(id);
    }

    public void createLike(Long userId, Long filmId) {
        filmStorage.createLike(userId, filmId);
    }

    public void deleteLike(Long userId, Long filmId) {
        filmStorage.deleteLike(userId, filmId);
    }

    public Collection<Film> getPopular(int count) {
        log.info("Получение списка популярных фильмов count={}", count);
        return filmStorage.readPopular(count);
    }
}
