package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private static long lastId = 0;

    private final HashMap<Long, User> users = new HashMap<>();

    @Override
    public void addUser(User user) {
        if (user.getId() == null) {
            user.setId(lastId + 1);
        }

        if (!users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            lastId = Long.max(lastId, user.getId());
            log.info("Добавлен/обновлен пользователь \"{}\"", user);
        } else {
            log.error("Такой идентификатор полльзователя уже существует \"{}\"", user.getId());
            throw new UserAlreadyExistsException(users.get(user.getId()));
        }
    }

    @Override
    public void removeUser(long id) {
        if (users.containsKey(id)) {
            users.remove(id);
        } else {
            throw new UserNotFoundException(id);
        }
    }

    @Override
    public boolean hasUser(long id) {
        return users.containsKey(id);
    }

    @Override
    public User getUser(long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new UserNotFoundException(id);
        }
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

}
