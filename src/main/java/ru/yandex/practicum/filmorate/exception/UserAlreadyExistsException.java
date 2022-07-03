package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.model.User;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserAlreadyExistsException extends RuntimeException {

    private final User user;

    public UserAlreadyExistsException(User user) {
        this.user = user;
    }

    @Override
    public String getMessage() {
        return user.toString();
    }
}
