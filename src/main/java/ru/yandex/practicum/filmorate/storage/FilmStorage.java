package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    void addFilm(Film film);

    void removeFilm(long id);

    boolean hasFilm(long id);

    Film getFilm(long id);

    List<Film> getFilms();

}
