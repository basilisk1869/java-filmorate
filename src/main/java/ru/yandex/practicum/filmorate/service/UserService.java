package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        return userStorage.getUsers().stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    public User getUser(long id) {
        return userStorage.getUser(id);
    }

    public User postUser(User user) {
        // check login not exists
        boolean isLoginExists = userStorage.getUsers().stream()
                .anyMatch(item -> Objects.equals(item.getLogin(), user.getLogin()));
        if (isLoginExists) {
            throw new InternalException("Такой логин уже существует \"" + user.getLogin() + "\"");
        }
        // add user and return result
        userStorage.addUser(user);
        return user;
    }

    public User putUser(User user) {
        userStorage.removeUser(user.getId());
        return postUser(user);
    }

    public List<User> getFriends(long id) {
        return userStorage.getUser(id).getFriends().stream()
                .map(userStorage::getUser)
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    public User putFriend(long id, long friendId) {
        // get objects
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);
        // make actions
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        return user;
    }

    public User deleteFriend(long id, long friendId) {
        // get objects
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);
        // make actions
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
        return user;
    }

    public List<User> getCommonFriends(long id, long otherId) {
        User user = userStorage.getUser(id);
        User other = userStorage.getUser(otherId);
        return user.getFriends().stream()
                .filter(friendId -> other.getFriends().contains(friendId))
                .map(userStorage::getUser)
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

}
