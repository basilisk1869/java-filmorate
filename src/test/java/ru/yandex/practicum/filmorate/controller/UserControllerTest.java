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
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserTest;
import ru.yandex.practicum.filmorate.service.UserService;
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

@WebMvcTest(value = {UserController.class, UserService.class, UserStorage.class})
public class UserControllerTest extends BaseControllerTest {

    private final static HashMap<Long, User> users = new HashMap<>();

    @Autowired
    public UserControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        super(mockMvc, objectMapper, "/users");
        MvcResult result = mockMvc.perform(makeGetRequest(""))
                .andExpect(status().isOk())
                .andReturn();
        users.clear();
        objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<User>>(){})
               .forEach(user -> users.put(user.getId(), user));
    }

    void postUser(User user, int status, Class<?> exception) throws Exception {
        setUser(user, status, "POST", exception);
    }

    void putUser(User user, int status, Class<?> exception) throws Exception {
        setUser(user, status, "PUT", exception);
    }

    void setUser(User user, int status, String method, Class<?> exception) throws Exception {
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
        ResultActions resultActions = mockMvc.perform(makeRequest(method, path, jsonOut));
        resultActions.andExpect(status().is(status));
        if (status == 200) {
            resultActions.andExpect(content().json(jsonIn));
            users.put(user.getId(), user);
            if (user.getId() >= expectedId) {
                expectedId = user.getId() + 1;
            }
        }
        MvcResult mvcResult = resultActions.andReturn();
        if (exception != null) {
            assertNotNull(mvcResult.getResolvedException());
            assertEquals(exception, mvcResult.getResolvedException().getClass());
        }
    }

    User getUser(long id, int status, Class<?> exception) throws Exception {
        MvcResult mvcResult = mockMvc.perform(makeGetRequest("/" + id))
                .andExpect(status().is(status))
                .andReturn();
        if (exception != null) {
            assertNotNull(mvcResult.getResolvedException());
            assertEquals(exception, mvcResult.getResolvedException().getClass());
        }

        if (status == 200) {
            return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);
        } else {
            return null;
        }
    }

    @Test
    void shouldPostNewUser() throws Exception {
        postUser(UserTest.getNormalUser(expectedId), 200, null);
    }

    @Test
    void shouldPostNewUserWithNulId() throws Exception {
        postUser(UserTest.getNormalUser(null), 200, null);
    }

    @Test
    void shouldNotPostUserTwice() throws Exception {
        User user1 = UserTest.getNormalUser(expectedId);
        postUser(user1, 200, null);
        User user2 = UserTest.getNormalUser(expectedId);
        user2.setId(user1.getId());
        postUser(user2, 400, UserAlreadyExistsException.class);
    }

    @Test
    void shouldNotPostUserWithEmptyEmail() throws Exception {
        User user = UserTest.getNormalUser(expectedId);
        user.setEmail("");
        postUser(user, 400, MethodArgumentNotValidException.class);
    }

    @Test
    void shouldNotPostUserWithInvalidEmail() throws Exception {
        User user = UserTest.getNormalUser(expectedId);
        user.setEmail("qweq.asdasd");
        postUser(user, 400, MethodArgumentNotValidException.class);
    }

    @Test
    void shouldNotPostUserWithEmptyLogin() throws Exception {
        User user = UserTest.getNormalUser(expectedId);
        user.setLogin("");
        postUser(user, 400, MethodArgumentNotValidException.class);
    }

    @Test
    void shouldNotPostUserWithSpacingLogin() throws Exception {
        User user = UserTest.getNormalUser(expectedId);
        user.setLogin("ivan ivanov");
        postUser(user, 400, MethodArgumentNotValidException.class);
    }

    @Test
    void shouldNotPostUserWithNullName() throws Exception {
        User user = UserTest.getNormalUser(expectedId);
        user.setName(null);
        postUser(user, 400, MethodArgumentNotValidException.class);
    }

    @Test
    void shouldPostUserWithEmptyName() throws Exception {
        User user = UserTest.getNormalUser(expectedId);
        user.setName("");
        postUser(user, 200, null);
    }

    @Test
    void shouldNotPostUserWithNullBirthday() throws Exception {
        User user = UserTest.getNormalUser(expectedId);
        user.setBirthday(null);
        postUser(user, 400, MethodArgumentNotValidException.class);
    }

    @Test
    void shouldNotPostUserWithBirthdayInFuture() throws Exception {
        User user = UserTest.getNormalUser(expectedId);
        user.setBirthday(LocalDate.now().plusYears(1));
        postUser(user, 400, MethodArgumentNotValidException.class);
    }

    @Test
    void shouldPutUser() throws Exception {
        putUser(UserTest.getNormalUser(expectedId - 1), 200, null);
    }

    @Test
    void shouldNotPutUserWithExistingLogin() throws Exception {
        User user1 = UserTest.getNormalUser(expectedId);
        postUser(user1, 200, null);
        User user2 = UserTest.getNormalUser(expectedId);
        user2.setLogin(user1.getLogin());
        putUser(user2, 404, UserNotFoundException.class);
    }

    @Test
    void shouldGetUsers() throws Exception {
        List<User> list = users.values().stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
        mockMvc.perform(makeGetRequest(""))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(list)));
    }

    @Test
    void shouldGetUser() throws Exception {
        User user1 = UserTest.getNormalUser(expectedId);
        postUser(user1, 200, null);
        User user2 = getUser(user1.getId(), 200, null);
        assertEquals(user1, user2);
    }

    @Test
    void shouldNotGetUser() throws Exception {
        getUser(expectedId, 404, UserNotFoundException.class);
    }

    @Test
    void shouldAddToFriends() throws Exception {
        User user1 = UserTest.getNormalUser(expectedId);
        postUser(user1, 200, null);
        User user2 = UserTest.getNormalUser(expectedId);
        postUser(user2, 200, null);
        user1.getFriends().add(user2.getId());
        user2.getFriends().add(user1.getId());
        mockMvc.perform(makePutRequest("/" + user1.getId() + "/friends/" + user2.getId(), null))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAddToFriendsBecauseWrongId() throws Exception {
        User user = UserTest.getNormalUser(expectedId);
        postUser(user, 200, null);
        MvcResult mvcResult = mockMvc.perform(makePutRequest("/" + expectedId + "/friends/" + user.getId(), null))
                .andExpect(status().is(404))
                .andReturn();
        assertNotNull(mvcResult.getResolvedException());
        assertEquals(UserNotFoundException.class, mvcResult.getResolvedException().getClass());
    }

    @Test
    void shouldNotAddToFriendsBecauseWrongFriendId() throws Exception {
        User user = UserTest.getNormalUser(expectedId);
        postUser(user, 200, null);
        MvcResult mvcResult = mockMvc.perform(makePutRequest("/" + user.getId() + "/friends/" + expectedId, null))
                .andExpect(status().is(404))
                .andReturn();
        assertNotNull(mvcResult.getResolvedException());
        assertEquals(UserNotFoundException.class, mvcResult.getResolvedException().getClass());
    }

    @Test
    void shouldDeleteFromFriends() throws Exception {
        shouldAddToFriends();
        long id1 = expectedId - 2;
        long id2 = expectedId - 1;
        User user1 = users.get(id1);
        User user2 = users.get(id2);
        user1.getFriends().remove(user2.getId());
        user2.getFriends().remove(user1.getId());
        mockMvc.perform(makeDeleteRequest("/" + user1.getId() + "/friends/" + user2.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotDeleteFromFriendsBecauseWrongId() throws Exception {
        shouldAddToFriends();
        MvcResult mvcResult = mockMvc.perform(makeDeleteRequest("/" + expectedId + "/friends/" + (expectedId - 1)))
                .andExpect(status().is(404))
                .andReturn();
        assertNotNull(mvcResult.getResolvedException());
        assertEquals(UserNotFoundException.class, mvcResult.getResolvedException().getClass());
    }

    @Test
    void shouldNotDeleteFromFriendsBecauseWrongFriendId() throws Exception {
        shouldAddToFriends();
        MvcResult mvcResult = mockMvc.perform(makeDeleteRequest("/" + (expectedId - 2) + "/friends/" + expectedId))
                .andExpect(status().is(404))
                .andReturn();
        assertNotNull(mvcResult.getResolvedException());
        assertEquals(UserNotFoundException.class, mvcResult.getResolvedException().getClass());
    }

    @Test
    void shouldGetFriends() throws Exception {
        shouldAddToFriends();
        long id1 = expectedId - 2;
        long id2 = expectedId - 1;
        User user2 = users.get(id2);
        mockMvc.perform(makeGetRequest("/" + id1 + "/friends"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(user2))));
    }

    @Test
    void shouldGetNoFriends() throws Exception {
        shouldPostNewUser();
        mockMvc.perform(makeGetRequest("/" + (expectedId - 1) + "/friends"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of())));
    }

    @Test
    void shouldNotGetFriends() throws Exception {
        shouldPostNewUser();
        MvcResult mvcResult = mockMvc.perform(makeGetRequest("/" + expectedId + "/friends"))
                .andExpect(status().is(404))
                .andReturn();
        assertNotNull(mvcResult.getResolvedException());
        assertEquals(UserNotFoundException.class, mvcResult.getResolvedException().getClass());
    }

    @Test
    void shouldGetCommonFriends() throws Exception {
        // create users
        User user1 = UserTest.getNormalUser(expectedId);
        postUser(user1, 200, null);
        User user2 = UserTest.getNormalUser(expectedId);
        postUser(user2, 200, null);
        User user3 = UserTest.getNormalUser(expectedId);
        postUser(user3, 200, null);
        // link friends internally
        user1.getFriends().add(user3.getId());
        user3.getFriends().add(user1.getId());
        user2.getFriends().add(user3.getId());
        user3.getFriends().add(user2.getId());
        // make wrong request
        MvcResult mvcResult = mockMvc.perform(makeGetRequest("/" + expectedId + "/friends/common/" + user2.getId()))
                .andExpect(status().is(404))
                .andReturn();
        assertNotNull(mvcResult.getResolvedException());
        assertEquals(UserNotFoundException.class, mvcResult.getResolvedException().getClass());
        mvcResult = mockMvc.perform(makeGetRequest("/" + user1.getId() + "/friends/common/" + expectedId))
                .andExpect(status().is(404))
                .andReturn();
        assertNotNull(mvcResult.getResolvedException());
        assertEquals(UserNotFoundException.class, mvcResult.getResolvedException().getClass());
        // make request with empty response
        mockMvc.perform(makeGetRequest("/" + user1.getId() + "/friends/common/" + user2.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of())));
        // add user3 to user1 and 2
        mockMvc.perform(makePutRequest("/" + user1.getId() + "/friends/" + user3.getId(), null))
                .andExpect(status().isOk());
        mockMvc.perform(makePutRequest("/" + user2.getId() + "/friends/" + user3.getId(), null))
                .andExpect(status().isOk());
        // make request with non-empty response
        mockMvc.perform(makeGetRequest("/" + user1.getId() + "/friends/common/" + user2.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(user3))));
    }
}
