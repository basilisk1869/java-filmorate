package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private static int lastId = 0;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getFilms() {
        return films.values().stream()
                .sorted(Comparator.comparing(Film::getId))
                .collect(Collectors.toList());
    }

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        // generate id when needed
        if (film.getId() == null) {
            film.setId(lastId + 1);
        }
        // check id not exists
        if (films.containsKey(film.getId())) {
            log.error("Такой идентификатор фильма уже существует \"{}\"", film.getId());
            throw new FilmAlreadyExistsException(films.get(film.getId()));
        }
        // check name not exists
        boolean isLoginExists = films.values().stream()
                .anyMatch(item -> item.getName().equals(film.getName()));
        if (isLoginExists) {
            log.error("Такой фильм уже существует \"{}\"", film.getName());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // put user
        films.put(film.getId(), film);
        log.info("Добавлен/обновлен фильм \"{}\"", film);
        // update last id
        lastId = Integer.max(lastId, film.getId());
        // return added user
        return films.get(film.getId());
    }

    @PutMapping
    public Film putFilm(@Valid @RequestBody Film film) {
        films.remove(film.getId());
        return postFilm(film);
    }

}
