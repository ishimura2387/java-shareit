package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
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
public class UserController {

    private final UserService userServiceImpl;

    @GetMapping
    public List<UserDto> getAll() {
        return userServiceImpl.getAll();
    }


    @PostMapping
    public UserDto add(@Valid @RequestBody UserDto userDto) {
        return userServiceImpl.add(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @RequestBody UserDto userDto) {
        userDto.setId(userId);
        return userServiceImpl.update(userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        userServiceImpl.delete(userId);
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable long id) {
        return userServiceImpl.get(id);
    }
}
