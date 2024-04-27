package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "films")
public class FilmController {
    private final FilmService filmService;

    @PostMapping()
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping()
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping()
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping(path = "{id}")
    public Film getFilm(@PathVariable Long id) {
        return filmService.getFilm(id);
    }

    @DeleteMapping(path = "{id}")
    public void deleteFilm(@PathVariable Long id) {
        filmService.deleteFilm(id);
    }

    @PutMapping(path = "{filmId}/like/{userId}")
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.createLike(userId, filmId);
    }

    @DeleteMapping(path = "{filmId}/like/{userId}")
    public void deleteLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.deleteLike(userId, filmId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        log.info("Получение списка популярных фильмов count={}", count);
        return filmService.getPopular(count);
    }
}
