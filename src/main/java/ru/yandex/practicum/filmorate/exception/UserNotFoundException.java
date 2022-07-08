package ru.yandex.practicum.filmorate.exception;

public class UserNotFoundException extends NotFoundException {

    private final Long id;

    public UserNotFoundException(Long id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Пользователь с идентификатором "+ id + " не найден.";
    }
}
