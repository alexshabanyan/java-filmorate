package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Mpa {
    Long id;
    String name;

    public Mpa(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
