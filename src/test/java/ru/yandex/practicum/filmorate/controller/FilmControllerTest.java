package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserTest;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {FilmController.class, FilmService.class, FilmStorage.class,
        UserController.class, UserService.class, UserStorage.class})
public class FilmControllerTest extends BaseControllerTest {

    private final static HashMap<Long, Film> films = new HashMap<>();

    private final UserControllerTest userControllerTest;

    @Autowired
    public FilmControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        super(mockMvc, objectMapper, "/films");
        this.userControllerTest = new UserControllerTest(mockMvc, objectMapper);
        MvcResult result = mockMvc.perform(makeGetRequest(""))
                .andExpect(status().isOk())
                .andReturn();
        films.clear();
        objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Film>>(){})
                .forEach(film -> films.put(film.getId(), film));
    }

    void setFilm(Film film, int status, String method, Class<?> exception) throws Exception {
        String jsonOut = objectMapper.writeValueAsString(film);
        String jsonIn = jsonOut;
        if (film.getId() == null) {
            film.setId(expectedId);
            jsonIn = objectMapper.writeValueAsString(film);
        }
        // make request
        ResultActions resultActions = mockMvc.perform(makeRequest(method, path, jsonOut));
        resultActions.andExpect(status().is(status));
        if (status == 200) {
            resultActions.andExpect(content().json(jsonIn));
            films.put(film.getId(), film);
            if (film.getId() >= expectedId) {
                expectedId = film.getId() + 1;
            }
        }
        MvcResult mvcResult = resultActions.andReturn();
        if (exception != null) {
            assertNotNull(mvcResult.getResolvedException());
            assertEquals(exception, mvcResult.getResolvedException().getClass());
        }
    }

    void postFilm(Film film, int status, Class<?> exception) throws Exception {
        setFilm(film, status, "POST", exception);
    }

    void putFilm(Film film, int status, Class<?> exception) throws Exception {
        setFilm(film, status, "PUT", exception);
    }

    @Test
    void shouldPostNewFilm() throws Exception {
        postFilm(FilmTest.getNormalFilm(expectedId), 200, null);
    }

    @Test
    void shouldPostNewFilmWithNulId() throws Exception {
        postFilm(FilmTest.getNormalFilm(null), 200, null);
    }

    @Test
    void shouldNotPostFilmTwice() throws Exception {
        Film film1 = FilmTest.getNormalFilm(expectedId);
        postFilm(film1, 200, null);
        Film film2 = FilmTest.getNormalFilm(expectedId);
        film2.setId(film1.getId());
        postFilm(film2, 400, FilmAlreadyExistsException.class);
    }

    @Test
    void shouldNotPostFilmWithEmptyName() throws Exception {
        Film film = FilmTest.getNormalFilm(expectedId);
        film.setName("");
        postFilm(film, 400, MethodArgumentNotValidException.class);
    }

    @Test
    void shouldPostFilmWithDescriptionOf200Symbols() throws Exception {
        Film film = FilmTest.getNormalFilm(expectedId);
        film.setDescription("!".repeat(200));
        postFilm(film, 200, null);
    }

    @Test
    void shouldNotPostFilmWithDescriptionOf201Symbols() throws Exception {
        Film film = FilmTest.getNormalFilm(expectedId);
        film.setDescription("!".repeat(201));
        postFilm(film, 400, MethodArgumentNotValidException.class);
    }

    @Test
    void shouldPostFilmWithCinemaBirthReleaseDate() throws Exception {
        Film film = FilmTest.getNormalFilm(expectedId);
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        postFilm(film, 200, null);
    }

    @Test
    void shouldNotPostFilmWithReleaseDateEarlierThanCinemaBirth() throws Exception {
        Film film = FilmTest.getNormalFilm(expectedId);
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        postFilm(film, 400, MethodArgumentNotValidException.class);
    }

    @Test
    void shouldNotPostFilmWithNonPositiveDuration() throws Exception {
        Film film = FilmTest.getNormalFilm(expectedId);
        film.setDuration(0);
        postFilm(film, 400, MethodArgumentNotValidException.class);
    }

    @Test
    void shouldPutFilm() throws Exception {
        putFilm(FilmTest.getNormalFilm(expectedId - 1), 200, null);
    }

    @Test
    void shouldNotPutFilmWithExistingName() throws Exception {
        Film film1 = FilmTest.getNormalFilm(expectedId);
        postFilm(film1, 200, null);
        Film film2 = FilmTest.getNormalFilm(expectedId);
        film2.setName(film1.getName());
        putFilm(film2, 404, FilmNotFoundException.class);
    }

    @Test
    void shouldGetFilms() throws Exception {
        List<Film> list = films.values().stream()
                .sorted(Comparator.comparing(Film::getId))
                .collect(Collectors.toList());
        mockMvc.perform(makeGetRequest(""))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(list)));
    }

    @Test
    void shouldPutLike() throws Exception {
        // post user
        User user = UserTest.getNormalUser(expectedId);
        userControllerTest.postUser(user, 200, null);
        // post film
        Film film = FilmTest.getNormalFilm(expectedId);
        postFilm(film, 200, null);
        // put like with wrong user id
        MvcResult mvcResult = mockMvc.perform(makePutRequest("/" + film.getId() + "/like/" + (user.getId() + 1), null))
                .andExpect(status().is(404))
                .andReturn();
        assertNotNull(mvcResult.getResolvedException());
        assertEquals(UserNotFoundException.class, mvcResult.getResolvedException().getClass());
        // put like with wrong film id
        mvcResult = mockMvc.perform(makePutRequest("/" + (film.getId() + 1) + "/like/" + user.getId(), null))
                .andExpect(status().is(404))
                .andReturn();
        assertNotNull(mvcResult.getResolvedException());
        assertEquals(FilmNotFoundException.class, mvcResult.getResolvedException().getClass());
        // put good like
        film.getLikes().add(user.getId());
        mockMvc.perform(makePutRequest("/" + film.getId() + "/like/" + user.getId(), null))
                .andExpect(status().isOk());
        shouldGetFilms();
    }

    @Test
    void shouldDeleteLike() throws Exception {
        // post user
        User user = UserTest.getNormalUser(expectedId);
        userControllerTest.postUser(user, 200, null);
        // post film
        Film film = FilmTest.getNormalFilm(expectedId);
        postFilm(film, 200, null);
        // put good like
        film.getLikes().add(user.getId());
        mockMvc.perform(makePutRequest("/" + film.getId() + "/like/" + user.getId(), null))
                .andExpect(status().isOk());
        // delete like with wrong user id
        MvcResult mvcResult = mockMvc.perform(makeDeleteRequest("/" + film.getId() + "/like/" + (user.getId() + 1)))
                .andExpect(status().is(404))
                .andReturn();
        assertNotNull(mvcResult.getResolvedException());
        assertEquals(UserNotFoundException.class, mvcResult.getResolvedException().getClass());
        // delete like with wrong film id
        mvcResult = mockMvc.perform(makeDeleteRequest("/" + (film.getId() + 1) + "/like/" + user.getId()))
                .andExpect(status().is(404))
                .andReturn();
        assertNotNull(mvcResult.getResolvedException());
        assertEquals(FilmNotFoundException.class, mvcResult.getResolvedException().getClass());
        // delete right like
        film.getLikes().remove(user.getId());
        mockMvc.perform(makeDeleteRequest("/" + film.getId() + "/like/" + user.getId()))
                .andExpect(status().isOk());
        shouldGetFilms();
    }

    @Test
    void shouldGetPopularFilms() throws Exception {
        for (int i = 0; i < 20; i++) {
            shouldPostNewFilm();
        }
        MvcResult result = mockMvc.perform(makeGetRequest("/popular"))
                .andExpect(status().isOk())
                .andReturn();
        List<Film> popularFilms = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>(){});
        assertEquals(Integer.min(10, films.size()), popularFilms.size());
        if (popularFilms.size() > 1) {
            result = mockMvc.perform(makeGetRequest("/popular?count=2"))
                    .andExpect(status().isOk())
                    .andReturn();
            popularFilms = objectMapper.readValue(result.getResponse().getContentAsString(),
                    new TypeReference<>(){});
            assertEquals(2, popularFilms.size());
        }
    }
}
