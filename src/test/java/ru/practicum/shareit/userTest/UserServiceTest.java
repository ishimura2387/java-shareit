package ru.practicum.shareit.userTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserService userService;
    private UserDto userDto1 = new UserDto(1, "user1", "user1@mail.ru");
    private UserDto userDto2 = new UserDto(2, "user2", "user2@mail.ru");
    private UserDto userDto3 = new UserDto(3, "user3", "user3@mail.ru");

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void userFindAllTest() {
        userService.createUser(userDto1);
        Assertions.assertEquals(userService.findAllUsers().size(), 1);
        userService.createUser(userDto2);
        Assertions.assertEquals(userService.findAllUsers().size(), 2);
        userService.createUser(userDto3);
        Assertions.assertEquals(userService.findAllUsers().size(), 3);
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void userAddAndGetTest() {
        userService.createUser(userDto1);
        UserDto userDto = userService.getUser(1);
        Assertions.assertEquals(userDto.getId(), userDto.getId());
        Assertions.assertEquals(userDto.getName(), userDto.getName());
        Assertions.assertEquals(userDto.getEmail(), userDto.getEmail());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getWrongUsertTest() {
        NullObjectException exception = Assertions.assertThrows(NullObjectException.class,
                () -> {
                    userService.getUser(1);
                },
                "Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
        Assertions.assertEquals("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void updateWrongUsertTest() {
        NullObjectException exception = Assertions.assertThrows(NullObjectException.class,
                () -> {
                    userService.updateUser(new UserDto(1, "nameUpdate", "emailUpdate@mail.ru"));
                },
                "Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
        Assertions.assertEquals("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void updateUserTest() {
        userService.createUser(userDto1);
        userService.updateUser(new UserDto(1, "nameUpdate", "emailUpdate@mail.ru"));
        UserDto userDto = userService.getUser(1);
        Assertions.assertEquals(userDto.getId(), 1);
        Assertions.assertEquals(userDto.getName(), "nameUpdate");
        Assertions.assertEquals(userDto.getEmail(), "emailUpdate@mail.ru");
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void deleteFindAllTest() {
        userService.createUser(userDto1);
        Assertions.assertEquals(userService.findAllUsers().size(), 1);
        userService.createUser(userDto2);
        Assertions.assertEquals(userService.findAllUsers().size(), 2);
        userService.deleteUser(1);
        Assertions.assertEquals(userService.findAllUsers().size(), 1);
    }
}
