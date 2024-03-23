package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    List<UserDto> findAllUsers();

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    void deleteUser(long userId);

    UserDto getUser(long id);
}
