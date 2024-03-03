package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;

    public List<UserDto> findAllUsers() {
        List<User> users = userStorage.findAllUsers();
        log.debug("Обработка запроса GET /users; Текущее количество пользователей: {}",
                users.size());
        return users.stream().map(user -> userMapper.fromUser(user)).collect(Collectors.toList());
    }

    public UserDto createUser(UserDto userDto) {
        userStorage.checkEmailForService(userDto.getEmail(), userDto.getId());
        User user = userMapper.toUser(userDto);
        log.debug("Обработка запроса POST /users. Создан пользователь: {}", user);
        userDto = userMapper.fromUser(userStorage.createNewUser(user));
        return userDto;
    }

    public UserDto updateUser(UserDto userDto) {
        int id = userDto.getId();
        userStorage.checkUserForService(id);
        userStorage.checkEmailForService(userDto.getEmail(), userDto.getId());
        User user = userMapper.updateUser(userDto, userStorage.getUser(id));
        log.debug("Обработка запроса PUT /users. Пользователь изменен: {}", user);
        return userMapper.fromUser(userStorage.updateUser(user));
    }

    public void deleteUser(Integer userId) {
        userStorage.checkUserForService(userId);
        log.debug("Обработка запроса DELETE /users.Пользователь удален: {}", userId);
        userStorage.deleteUser(userId);
    }

    public UserDto getUser(int id) {
        userStorage.checkUserForService(id);
        log.debug("Обработка запроса GET /users.Запрошен пользователь c id: {}", id);
        return userMapper.fromUser(userStorage.getUser(id));
    }
}