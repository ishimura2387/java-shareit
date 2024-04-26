package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userServiceImpl;

    @GetMapping
    public List<UserDto> getAll() {
        log.debug("Обработка запроса GET/users");
        List<UserDto> users = userServiceImpl.getAll();
        log.debug("Получен список с размером: {}", users.size());
        return users;
    }

    @PostMapping
    public UserDto add(@Valid @RequestBody UserDto userDto) {
        log.debug("Обработка запроса POST/users");
        UserDto user = userServiceImpl.add(userDto);
        log.debug("Создан пользователь: {}", user);
        return user;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @RequestBody UserDto userDto) {
        log.debug("Обработка запроса PATCH/users/" + userId);
        userDto.setId(userId);
        UserDto user = userServiceImpl.update(userDto);
        log.debug("Пользователь изменен: {}", user);
        return user;
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.debug("Обработка запроса DELETE/users/" + userId);
        userServiceImpl.delete(userId);
        log.debug("Пользователь удален: {}", userId);
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable long id) {
        log.debug("Обработка запроса GET/users/" + id);
        UserDto user = userServiceImpl.get(id);
        log.debug("Получен пользователь: {}", user);
        return user;
    }
}
