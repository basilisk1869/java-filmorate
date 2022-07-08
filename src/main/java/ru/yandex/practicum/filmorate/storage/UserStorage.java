package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    void addUser(User film);

    void removeUser(long id);

    boolean hasUser(long id);

    User getUser(long id);

    List<User> getUsers();

}
