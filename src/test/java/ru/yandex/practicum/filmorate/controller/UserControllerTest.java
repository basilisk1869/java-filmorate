package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserTest;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest extends BaseControllerTest {

    private final static HashMap<Integer, User> users = new HashMap<>();

    @Autowired
    public UserControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        super(mockMvc, objectMapper, "/users");
    }

    void requestUser(User user, int status, boolean isPostRequest) throws Exception {
        String jsonOut = objectMapper.writeValueAsString(user);
        String jsonIn = jsonOut;
        if (user.getId() == null) {
            user.setId(expectedId);
            jsonIn = objectMapper.writeValueAsString(user);
        }
        if (user.getName() != null && user.getName().isEmpty() && user.getLogin() != null) {
            user.setName(user.getLogin());
            jsonIn = objectMapper.writeValueAsString(user);
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
            users.put(user.getId(), user);
            if (user.getId() >= expectedId) {
                expectedId = user.getId() + 1;
            }
        }
    }

    void postUser(User user, int status) throws Exception {
        requestUser(user, status, true);
    }

    void putUser(User user, int status) throws Exception {
        requestUser(user, status, false);
    }

    @Test
    void shouldPostNewUser() throws Exception {
        postUser(UserTest.getNormalUser(expectedId), 200);
    }

    @Test
    void shouldPostNewUserWithNulId() throws Exception {
        postUser(UserTest.getNormalUser(null), 200);
    }

    @Test
    void shouldNotPostUserTwice() throws Exception {
        int id = expectedId;
        postUser(UserTest.getNormalUser(id), 200);
        postUser(UserTest.getNormalUser(id), 400);
    }

    @Test
    void shouldNotPostUserWithEmptyEmail() throws Exception {
        User user = UserTest.getNormalUser(expectedId);
        user.setEmail("");
        postUser(user, 400);
    }

    @Test
    void shouldNotPostUserWithInvalidEmail() throws Exception {
        User user = UserTest.getNormalUser(expectedId);
        user.setEmail("qweq.asdasd");
        postUser(user, 400);
    }

    @Test
    void shouldNotPostUserWithEmptyLogin() throws Exception {
        User user = UserTest.getNormalUser(expectedId);
        user.setLogin("");
        postUser(user, 400);
    }

    @Test
    void shouldNotPostUserWithSpacingLogin() throws Exception {
        User user = UserTest.getNormalUser(expectedId);
        user.setLogin("ivan ivanov");
        postUser(user, 400);
    }

    @Test
    void shouldNotPostUserWithNullName() throws Exception {
        User user = UserTest.getNormalUser(expectedId);
        user.setName(null);
        postUser(user, 400);
    }

    @Test
    void shouldPostUserWithEmptyName() throws Exception {
        User user = UserTest.getNormalUser(expectedId);
        user.setName("");
        postUser(user, 200);
    }

    @Test
    void shouldNotPostUserWithNullBirthday() throws Exception {
        User user = UserTest.getNormalUser(expectedId);
        user.setBirthday(null);
        postUser(user, 400);
    }

    @Test
    void shouldNotPostUserWithBirthdayInFuture() throws Exception {
        User user = UserTest.getNormalUser(expectedId);
        user.setBirthday(LocalDate.now().plusYears(1));
        postUser(user, 400);
    }

    @Test
    void shouldPutUser() throws Exception {
        putUser(UserTest.getNormalUser(expectedId - 1), 200);
    }

    @Test
    void shouldNotPutFilmWithExistingLogin() throws Exception {
        User user1 = UserTest.getNormalUser(expectedId);
        putUser(user1, 200);
        User user2 = UserTest.getNormalUser(expectedId);
        user2.setLogin(user1.getLogin());
        putUser(user2, 500);
    }

    @Test
    void getUsers() throws Exception {
        List<User> list = users.values().stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
        mockMvc.perform(makeGetRequest())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(list)));
    }

}
