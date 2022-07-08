package ru.yandex.practicum.filmorate.exception;

import ru.yandex.practicum.filmorate.model.Film;

public class FilmAlreadyExistsException extends AlreadyExistsException {

    private final Film film;

    public FilmAlreadyExistsException(Film film) {
        this.film = film;
    }

    @Override
    public String getMessage() {
        return film.toString();
    }
}
