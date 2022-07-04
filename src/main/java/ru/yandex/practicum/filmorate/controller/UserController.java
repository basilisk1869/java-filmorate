package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private static int lastId = 0;
    private final HashMap<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getUsers() {
        return users.values().stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        // generate id when needed
        if (user.getId() == null) {
            user.setId(lastId + 1);
        }
        // check id not exists
        if (users.containsKey(user.getId())) {
            log.error("Такой идентификатор пользователя уже существует \"{}\"", user.getId());
            throw new UserAlreadyExistsException(users.get(user.getId()));
        }
        // check login not exists
        boolean isLoginExists = users.values().stream()
                .anyMatch(item -> item.getLogin().equals(user.getLogin()));
        if (isLoginExists) {
            log.error("Такой логин уже существует \"{}\"", user.getLogin());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // put user
        users.put(user.getId(), user);
        log.info("Добавлен/обновлен пользователь \"{}\"", user);
        // update last id
        lastId = Integer.max(lastId, user.getId());
        // return added user
        return users.get(user.getId());
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) {
        users.remove(user.getId());
        return postUser(user);
    }

}
