package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparing(Film::getId))
                .collect(Collectors.toList());
    }

    public Film getFilm(long id) {
        return filmStorage.getFilm(id);
    }

    public Film postFilm(Film film) {
        // check name not exists
        boolean isNameExists = filmStorage.getFilms().stream()
                .anyMatch(item -> item.getName().equals(film.getName()));
        if (isNameExists) {
            throw new InternalException("Такой фильм уже существует \"" + film.getName() + "\"");
        }
        // add film and return result
        filmStorage.addFilm(film);
        return film;
    }

    public Film putFilm(Film film) {
        filmStorage.removeFilm(film.getId());
        return postFilm(film);
    }

    public Film putLike(long id, long userId) {
        // get objects
        Film film = filmStorage.getFilm(id);
        userStorage.getUser(userId);
        // make actions
        film.getLikes().add(userId);
        return film;
    }

    public Film deleteLike(long id, long userId) {
        // get objects
        Film film = filmStorage.getFilm(id);
        userStorage.getUser(userId);
        // make actions
        film.getLikes().remove(userId);
        return film;
    }

    public List<Film> getPopularFilms(long count) {
        return filmStorage.getFilms().stream()
                .sorted((film1, film2) -> (film2.getLikes().size() - film1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
