package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer sequenceId = 1;

    @PostMapping()
    public Film addFilm(@Valid @RequestBody Film film) {
        if (film == null) {
            throw new ValidationException("Отсутствует фильм для создания");
        }
        film.setId(sequenceId);
        films.put(film.getId(), film);
        sequenceId++;
        log.info("Добавлен фильм {}", film);
        return film;
    }

    @PutMapping()
    public Film updateFilm(@RequestBody Film film) {
        Film updFilm = films.get(film.getId());
        if (updFilm == null) {
            throw new ValidationException("Отсутствует фильм для обновления");
        }
        films.put(film.getId(), film);
        log.info("Обновлен фильм {}", film);
        return film;
    }

    @GetMapping()
    public Collection<Film> findAll() {
        log.info("Получен список фильмов");
        return films.values();
    }
}
