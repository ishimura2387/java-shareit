package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    List<User> findAllUsers();

    User createNewUser(User user);

    User updateUser(User user);

    User getUser(int id);

    void deleteUser(int id);

    void checkEmailForService(String email, int id);

    void checkUserForService(int id);
}
