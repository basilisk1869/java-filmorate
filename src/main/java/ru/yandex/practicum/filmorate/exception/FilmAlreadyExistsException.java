package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.model.Film;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FilmAlreadyExistsException extends RuntimeException {

    private final Film film;

    public FilmAlreadyExistsException(Film film) {
        this.film = film;
    }

    @Override
    public String getMessage() {
        return film.toString();
    }
}
