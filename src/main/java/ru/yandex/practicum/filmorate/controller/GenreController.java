package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "genres")
public class GenreController {
    private final GenreService genreService;

    @GetMapping()
    public Collection<Genre> findAll() {
        return genreService.findAll();
    }

    @GetMapping(path = "{id}")
    public Genre findOne(@PathVariable Long id) {
        return genreService.getGenre(id);
    }
}
