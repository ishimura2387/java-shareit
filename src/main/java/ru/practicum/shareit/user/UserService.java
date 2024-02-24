package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.SameEmailException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;

    public List<UserDto> findAllUsers() {
        List<User> users = userStorage.findAllUsers();
        log.debug("Обработка запроса GET /users; Текущее количество пользователей: {}",
                users.size());
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(userMapper.fromUser(user));
        }
        return usersDto;
    }

    public UserDto createUser(UserDto userDto) {
        checkEmail(userDto.getEmail(), userDto.getId());
        User user = userMapper.toUser(userDto);
        log.debug("Обработка запроса POST /users. Создан пользователь: {}", user);
        userDto = userMapper.fromUser(userStorage.createNewUser(user));
        return userDto;
    }

    public UserDto updateUser(UserDto userDto) {
        checkUser(userDto.getId());
        checkEmail(userDto.getEmail(), userDto.getId());
        int id = userDto.getId();
        User user = userMapper.updateUser(userDto, userStorage.getUser(id));
        log.debug("Обработка запроса PUT /users. Пользователь изменен: {}", user);
        return userMapper.fromUser(userStorage.updateUser(user));
    }

    public void deleteUser(Integer userId) {
        checkUser(userId);
        log.debug("Обработка запроса DELETE /users.Пользователь удален: {}", userId);
        userStorage.deleteUser(userId);
    }

    public UserDto getUser(int id) {
        checkUser(id);
        log.debug("Обработка запроса GET /users.Запрошен пользователь c id: {}", id);
        return userMapper.fromUser(userStorage.getUser(id));
    }

    private void checkUser(Integer id) {
        if (!userStorage.findAllUsersId().contains(id)) {
            log.debug("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
            throw new NullObjectException("Пользователь не найден!");
        }
    }

    private void checkEmail(String email, int id) {
        if (email != null) {
            List<User> users = userStorage.findAllUsers();
            for (User user2 : users) {
                if (email.equals(user2.getEmail()) && id != user2.getId()) {
                    log.debug("Пользователь с таким email уже существует!");
                    throw new SameEmailException("Пользователь с таким email уже существует!");
                }
            }
        }
    }
}