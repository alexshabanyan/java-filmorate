package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ValidDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film {
    Long id;

    @NotBlank
    String name;

    @Size(max = 200, message = "Длина описания не может превышать 200 символов.")
    String description;

    @ValidDate(message = "дата релиза — не раньше 28 декабря 1895 года", targetDate = "1895-12-28")
    LocalDate releaseDate;

    @NotNull
    @Positive
    Integer duration;

    Set<Long> likes;

    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration, Set<Long> likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = Objects.requireNonNullElseGet(likes, HashSet::new);
    }
}
