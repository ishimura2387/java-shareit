package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.debug("Обработка запроса GET/users");
        ResponseEntity<Object> users = userClient.getAll();
        log.debug("Получен список: {}", users.getBody());
        return users;
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Valid UserDto userDto) {
        log.debug("Обработка запроса POST/users");
        ResponseEntity<Object> user = userClient.addUser(userDto);
        log.debug("Создан пользователь: {}", user.getBody());
        return user;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable long userId, @RequestBody UserDto userDto) {
        log.debug("Обработка запроса PATCH/users/" + userId);
        ResponseEntity<Object> user = userClient.update(userId, userDto);
        log.debug("Пользователь изменен: {}", user.getBody());
        return user;
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable long userId) {
        log.debug("Обработка запроса DELETE/users/" + userId);
        ResponseEntity<Object> response = userClient.delete(userId);
        log.debug("Пользователь удален: {}", userId);
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable long id) {
        log.debug("Обработка запроса GET/users/" + id);
        ResponseEntity<Object> user = userClient.get(id);
        log.debug("Получен пользователь: {}", user.getBody());
        return user;
    }
}
