package utils;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Random;

public class UserTestUtils {
    public User generateUser() {
        return generateUser(null);
    }

    public User generateUser(Long id) {
        Random random = new Random();
        return new User(id,
                random.nextInt(10000) + "@email.ru",
                "Alexander" + random.nextInt(10000),
                "Name " + random.nextInt(10000),
                LocalDate.of(1994, 8, random.nextInt(30) + 1));
    }
}
