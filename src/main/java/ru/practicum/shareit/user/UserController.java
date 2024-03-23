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
    public List<UserDto> findAllUsers() {
        return userServiceImpl.findAllUsers();
    }


    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return userServiceImpl.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        userDto.setId(userId);
        return userServiceImpl.updateUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userServiceImpl.deleteUser(userId);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable long id) {
        return userServiceImpl.getUser(id);
    }
}
