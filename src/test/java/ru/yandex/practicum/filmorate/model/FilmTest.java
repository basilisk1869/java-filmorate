package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmTest extends BaseTest {

    Film getNormalFilm() {
        return getNormalFilm(null);
    }

    public static Film getNormalFilm(Integer id) {
       return Film.builder()
                .id(id)
                .name("The Best Film" + id)
                .description("Very best comedy in the world!")
                .releaseDate(LocalDate.now().minusYears(10))
                .duration(180)
                .build();
    }

    List<String> validateFilm(Film film) {
        return validator.validate(film).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Path::toString)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Test
    void testNormalFilm() {
        Film film = getNormalFilm();
        assertEquals(List.of(), validateFilm(film));
    }

    @Test
    void testWithNullName() {
        Film film = getNormalFilm();
        film.setName(null);
        assertEquals(List.of("name"), validateFilm(film));
    }

    @Test
    void testWithEmptyName() {
        Film film = getNormalFilm();
        film.setName("");
        assertEquals(List.of("name"), validateFilm(film));
    }

    @Test
    void testWithNullDescription() {
        Film film = getNormalFilm();
        film.setDescription(null);
        assertEquals(List.of(), validateFilm(film));
    }

    @Test
    void testDescriptionLimit() {
        Film film = getNormalFilm();
        film.setDescription("Best!".repeat(40));
        assertEquals(List.of(), validateFilm(film));
    }

    @Test
    void testTooLongDescription() {
        Film film = getNormalFilm();
        film.setDescription("Best!".repeat(40) + "!");
        assertEquals(List.of("description"), validateFilm(film));
    }

    @Test
    void testWithNullReleaseDate() {
        Film film = getNormalFilm();
        film.setReleaseDate(null);
        assertEquals(List.of("releaseDate"), validateFilm(film));
    }

    @Test
    void testReleaseDateLimit() {
        Film film = getNormalFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertEquals(List.of(), validateFilm(film));
    }

    @Test
    void testWithAncientReleaseDate() {
        Film film = getNormalFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertEquals(List.of("releaseDateNotLaterCinemaBirth"), validateFilm(film));
    }

}
