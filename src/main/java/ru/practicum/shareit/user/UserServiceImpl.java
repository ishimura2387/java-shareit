package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NullObjectException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        log.debug("Обработка запроса GET /users; Текущее количество пользователей: {}",
                users.size());
        return users.stream().map(user -> userMapper.fromUser(user)).collect(Collectors.toList());
    }

    public UserDto add(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        log.debug("Обработка запроса POST /users. Создан пользователь: {}", user);
        userDto = userMapper.fromUser(userRepository.save(user));
        return userDto;
    }

    public UserDto update(UserDto userDto) {
        long id = userDto.getId();
        User userOld = userRepository.findById(id).orElseThrow(() -> new NullObjectException("Ошибка проверки " +
                "пользователя на наличие в Storage! Пользователь не найден!"));
        User user = userMapper.updateUser(userDto, userOld);
        log.debug("Обработка запроса PUT /users. Пользователь изменен: {}", user);
        return userMapper.fromUser(userRepository.save(user));
    }

    public void delete(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NullObjectException("Ошибка проверки " +
                "пользователя на наличие в Storage! Пользователь не найден!"));
        log.debug("Обработка запроса DELETE /users.Пользователь удален: {}", userId);
        userRepository.deleteById(userId);
    }

    public UserDto get(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NullObjectException("Ошибка проверки " +
                "пользователя на наличие в Storage! Пользователь не найден!"));
        log.debug("Обработка запроса GET /users.Запрошен пользователь c id: {}", id);
        return userMapper.fromUser(userRepository.getById(id));
    }
}