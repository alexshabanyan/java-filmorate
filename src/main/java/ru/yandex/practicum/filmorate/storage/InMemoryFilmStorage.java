package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private Long sequenceId = 1L;

    public Film addFilm(Film film) {
        if (film == null) {
            log.error("Произошла ошибка создания фильма. Отсутствует фильм для создания");
            throw new ValidationException("Отсутствует фильм для создания");
        }
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм {}", film);
        return film;
    }

    public Film updateFilm(Film film) {
        Film updFilm = films.get(film.getId());
        if (updFilm == null) {
            log.error("Произошла ошибка обновления фильма. Отсутствует фильм для обновления");
            throw new ItemNotFoundException(film.getId());
        }
        films.put(film.getId(), film);
        log.info("Обновлен фильм {}", film);
        return film;
    }

    public Collection<Film> findAll() {
        log.info("Получение списка всех фильмов");
        return films.values();
    }

    @Override
    public Film findFilm(Long id) {
        return films.get(id);
    }

    public Long generateId() {
        return sequenceId++;
    }
}
