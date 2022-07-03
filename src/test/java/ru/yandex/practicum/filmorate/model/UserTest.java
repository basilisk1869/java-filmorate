package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest extends BaseTest {

    User getNormalUser() {
        return getNormalUser(null);
    }

    public static User getNormalUser(Integer id) {
        return User.builder()
                .id(id)
                .email("cyborg@yandex.ru")
                .login("terminator" + id)
                .name("John Dow")
                .birthday(LocalDate.now().minusYears(40))
                .build();
    }

    List<String> validateUser(User user) {
        return validator.validate(user).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Path::toString)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Test
    void testNormalUser() {
        User user = getNormalUser();
        assertEquals(List.of(), validateUser(user));
    }

    @Test
    void testWithNullEmail() {
        User user = getNormalUser();
        user.setEmail(null);
        assertEquals(List.of("email"), validateUser(user));
    }

    @Test
    void testWithEmptyEmail() {
        User user = getNormalUser();
        user.setEmail("");
        assertEquals(List.of("email"), validateUser(user));
    }

    @Test
    void testWithNullLogin() {
        User user = getNormalUser();
        user.setLogin(null);
        assertEquals(List.of("login"), validateUser(user));
    }

    @Test
    void testWithEmptyLogin() {
        User user = getNormalUser();
        user.setLogin("");
        assertEquals(List.of("login"), validateUser(user));
    }

    @Test
    void testWithSpacedLogin() {
        User user = getNormalUser();
        user.setLogin("john dow");
        assertEquals(List.of("login"), validateUser(user));
    }

    @Test
    void testWithNullName() {
        User user = getNormalUser();
        user.setName(null);
        assertEquals(List.of("name"), validateUser(user));
    }

    @Test
    void testWithEmptyName() {
        User user = getNormalUser();
        user.setName("");
        assertEquals(List.of(), validateUser(user));
        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void testWithNullBirthDay() {
        User user = getNormalUser();
        user.setBirthday(null);
        assertEquals(List.of("birthday"), validateUser(user));
    }

    @Test
    void testWithBirthDayInFuture() {
        User user = getNormalUser();
        user.setBirthday(LocalDate.now().plusYears(1));
        assertEquals(List.of("birthday"), validateUser(user));
    }
}
