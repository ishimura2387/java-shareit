package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.SameEmailException;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        log.debug("Обработка запроса GET /users; Текущее количество пользователей: {}",
                users.size());
        return users.stream().map(user -> userMapper.fromUser(user)).collect(Collectors.toList());
    }

    public UserDto createUser(UserDto userDto) {
        try {
            User user = userMapper.toUser(userDto);
            log.debug("Обработка запроса POST /users. Создан пользователь: {}", user);
            userDto = userMapper.fromUser(userRepository.save(user));
            return userDto;
        } catch (DataIntegrityViolationException e) {
            log.debug("Пользователь с таким email уже существует!");
            throw new SameEmailException("Пользователь с таким email уже существует!");
        }

    }

    public UserDto updateUser(UserDto userDto) {
        try {
            long id = userDto.getId();
            checkUser(id);
            User user = userMapper.updateUser(userDto, userRepository.getById(id));
            log.debug("Обработка запроса PUT /users. Пользователь изменен: {}", user);
            return userMapper.fromUser(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            log.debug("Пользователь с таким email уже существует!");
            throw new SameEmailException("Пользователь с таким email уже существует!");
        }
    }

    public void deleteUser(long userId) {
        try {
            log.debug("Обработка запроса DELETE /users.Пользователь удален: {}", userId);
            userRepository.deleteById(userId);
        } catch (EntityNotFoundException e) {
            log.debug("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
            throw new NullObjectException("Пользователь не найден!");
        }
    }

    public UserDto getUser(long id) {
        try {
            log.debug("Обработка запроса GET /users.Запрошен пользователь c id: {}", id);
            return userMapper.fromUser(userRepository.getById(id));
        } catch (EntityNotFoundException e) {
            log.debug("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
            throw new NullObjectException("Пользователь не найден!");
        }
    }

    private void checkUser(long id) {
        if (userRepository.findById(id).isEmpty()) {
            log.debug("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
            throw new NullObjectException("Пользователь не найден!");
        }
    }
}