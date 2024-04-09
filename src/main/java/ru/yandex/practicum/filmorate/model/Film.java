package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.yandex.practicum.filmorate.validation.ValidDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Value
@Jacksonized
@SuperBuilder(toBuilder = true)
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

    @NonFinal
    @Builder.Default
    Set<Genre> genres = new HashSet<>();

    Mpa mpa;

    public Film(Long id,
                String name,
                String description,
                LocalDate releaseDate,
                Integer duration,
                Mpa mpa,
                Set<Genre> genres
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = Objects.requireNonNullElseGet(genres, HashSet::new);
    }
}
