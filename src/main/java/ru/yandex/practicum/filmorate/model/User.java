package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

/**
 * User.
 */

@Data
public class User {
    Integer id;

    @NotBlank
    @Email(message = "Неверный формат почты")
    String email;

    @NotBlank
    String login;
    String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;
}
