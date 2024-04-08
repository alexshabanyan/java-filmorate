package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Slf4j
@Service
public class FilmService {
//    private static final Comparator<Film> FILM_COMPARATOR_SORT_BY_LIKES = Comparator
//            .comparing(film -> film.getLikes().size(), Comparator.reverseOrder());

    private final FilmStorage filmStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Отсутствует идентификатор фильма");
        }
        return filmStorage.updateFilm(film);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getFilm(Long id) {
        Film film = filmStorage.findFilm(id);
        if (film == null) {
            throw new ItemNotFoundException(id);
        }
        return filmStorage.findFilm(id);
    }

//    public Film setLike(Long filmId, Long userId) {
//        Film film = getFilm(filmId);
//        if (film == null) {
//            throw new ItemNotFoundException(filmId);
//        }
//        film.getLikes().add(userId);
//        return film;
//    }

//    public Film removeLike(Long filmId, Long userId) {
//        Film film = getFilm(filmId);
//        if (film == null) {
//            throw new ItemNotFoundException(filmId);
//        }
//        Set<Long> filmLikes = film.getLikes();
//        filmLikes.remove(userId);
//        return film;
//    }

//    public Collection<Film> getPopular(int count) {
//        log.info("Получение списка популярных фильмов count={}", count);
//        return filmStorage.findAll().stream()
//                .sorted(FILM_COMPARATOR_SORT_BY_LIKES)
//                .limit(count)
//                .collect(Collectors.toList());
//    }
}
