package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private static long lastId = 0;

    private final HashMap<Long, Film> films = new HashMap<>();

    @Override
    public void addFilm(Film film) {
        if (film.getId() == null) {
            film.setId(lastId + 1);
        }

        if (!films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            lastId = Long.max(lastId, film.getId());
            log.info("Добавлен/обновлен фильм \"{}\"", film);
        } else {
            throw new FilmAlreadyExistsException(films.get(film.getId()));
        }
    }

    @Override
    public void removeFilm(long id) {
        if (films.containsKey(id)) {
            films.remove(id);
        } else {
            throw new FilmNotFoundException(id);
        }
    }

    @Override
    public boolean hasFilm(long id) {
        return films.containsKey(id);
    }

    @Override
    public Film getFilm(long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new FilmNotFoundException(id);
        }
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

}
