package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmTest;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
public class FilmControllerTest extends BaseControllerTest {

    private final static HashMap<Integer, Film> films = new HashMap<>();

    public FilmControllerTest() {
        super("/films");
    }

    void requestFilm(Film film, int status, boolean isPostRequest) throws Exception {
        String jsonOut = objectMapper.writeValueAsString(film);
        String jsonIn = jsonOut;
        if (film.getId() == null) {
            film.setId(expectedId);
            jsonIn = objectMapper.writeValueAsString(film);
        }
        // make request
        ResultActions resultActions;
        if (isPostRequest) {
            resultActions = mockMvc.perform(makePostRequest(jsonOut));
        } else {
            resultActions = mockMvc.perform(makePutRequest(jsonOut));
        }
        resultActions.andExpect(status().is(status));
        if (status == 200) {
            resultActions.andExpect(content().json(jsonIn));
            films.put(film.getId(), film);
            if (film.getId() >= expectedId) {
                expectedId = film.getId() + 1;
            }
        }
    }

    void postFilm(Film film, int status) throws Exception {
        requestFilm(film, status, true);
    }

    void putFilm(Film film, int status) throws Exception {
        requestFilm(film, status, false);
    }

    @Test
    void shouldPostNewFilm() throws Exception {
        postFilm(FilmTest.getNormalFilm(expectedId), 200);
    }

    @Test
    void shouldPostNewFilmWithNulId() throws Exception {
        postFilm(FilmTest.getNormalFilm(null), 200);
    }

    @Test
    void shouldNotPostFilmTwice() throws Exception {
        int id = expectedId;
        postFilm(FilmTest.getNormalFilm(id), 200);
        postFilm(FilmTest.getNormalFilm(id), 400);
    }

    @Test
    void shouldNotPostFilmWithEmptyName() throws Exception {
        Film film = FilmTest.getNormalFilm(expectedId);
        film.setName("");
        postFilm(film, 400);
    }

    @Test
    void shouldPostFilmWithDescriptionOf200Symbols() throws Exception {
        Film film = FilmTest.getNormalFilm(expectedId);
        film.setDescription("!".repeat(200));
        postFilm(film, 200);
    }

    @Test
    void shouldNotPostFilmWithDescriptionOf201Symbols() throws Exception {
        Film film = FilmTest.getNormalFilm(expectedId);
        film.setDescription("!".repeat(201));
        postFilm(film, 400);
    }

    @Test
    void shouldPostFilmWithCinemaBirthReleaseDate() throws Exception {
        Film film = FilmTest.getNormalFilm(expectedId);
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        postFilm(film, 200);
    }

    @Test
    void shouldNotPostFilmWithReleaseDateEarlierThanCinemaBirth() throws Exception {
        Film film = FilmTest.getNormalFilm(expectedId);
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        postFilm(film, 400);
    }

    @Test
    void shouldNotPostFilmWithNonPositiveDuration() throws Exception {
        Film film = FilmTest.getNormalFilm(expectedId);
        film.setDuration(0);
        postFilm(film, 400);
    }

    @Test
    void shouldPutFilm() throws Exception {
        putFilm(FilmTest.getNormalFilm(expectedId - 1), 200);
    }

    @Test
    void shouldNotPutFilmWithExistingName() throws Exception {
        Film film1 = FilmTest.getNormalFilm(expectedId);
        putFilm(film1, 200);
        Film film2 = FilmTest.getNormalFilm(expectedId);
        film2.setName(film1.getName());
        putFilm(film2, 500);
    }

    @Test
    void getFilms() throws Exception {
        List<Film> list = films.values().stream()
                    .sorted(Comparator.comparing(Film::getId))
                    .collect(Collectors.toList());
        mockMvc.perform(makeGetRequest())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(list)));
    }

}