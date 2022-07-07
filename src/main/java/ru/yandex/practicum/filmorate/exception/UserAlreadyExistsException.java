package ru.yandex.practicum.filmorate.exception;

import ru.yandex.practicum.filmorate.model.User;

public class UserAlreadyExistsException extends AlreadyExistsException {

    private final User user;

    public UserAlreadyExistsException(User user) {
        this.user = user;
    }

    @Override
    public String getMessage() {
        return user.toString();
    }
}
