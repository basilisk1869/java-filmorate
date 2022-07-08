package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends NotFoundException {

    private final Long id;

    public FilmNotFoundException(Long id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Фильм с идентификатором " + id + " не найден.";
    }
}
