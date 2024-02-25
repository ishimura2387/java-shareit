package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.SameEmailException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class MemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int counterUserId = 1;

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createNewUser(User user) {
        user.setId(counterUserId);
        users.put(user.getId(), user);
        counterUserId++;
        return user;
    }

    @Override
    public User updateUser(User user) {
        return users.replace(user.getId(), user);
    }

    @Override
    public User getUser(int id) {
        return users.get(id);
    }

    @Override
    public void deleteUser(int id) {
        users.remove(id);
    }
    @Override
    public void checkEmailForService(String email, int id) {
        checkEmail(email, id);
    }

    @Override
    public void checkUserForService(int id) {
        checkUser(id);
    }

    private void checkUser(int id) {
        if (users.get(id) == null) {
            log.debug("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
            throw new NullObjectException("Пользователь не найден!");
        }
    }

    private void checkEmail(String email, int id) { // вытащить в слой storage не получается как не пробовал,
        if (email != null) {
            List<User> usersWithSomeEmail = users.values().stream()
                    .filter(user -> user.getEmail().equals(email)).collect(Collectors.toList());
            if (usersWithSomeEmail.size() > 0 && usersWithSomeEmail.get(0).getId() != id) {
                log.debug("Пользователь с таким email уже существует!");
                throw new SameEmailException("Пользователь с таким email уже существует!");
            }
        }
    }
}
