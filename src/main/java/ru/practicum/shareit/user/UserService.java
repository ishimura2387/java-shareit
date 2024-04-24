package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    List<UserDto> getAll();

    UserDto add(UserDto userDto);

    UserDto update(UserDto userDto);

    void delete(long userId);

    UserDto get(long id);
}
