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
        userService.add(userDto1);
        Assertions.assertEquals(userService.getAll().size(), 1);
        userService.add(userDto2);
        Assertions.assertEquals(userService.getAll().size(), 2);
        userService.add(userDto3);
        Assertions.assertEquals(userService.getAll().size(), 3);
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void userAddAndGetTest() {
        userService.add(userDto1);
        UserDto userDto = userService.get(1);
        Assertions.assertEquals(userDto.getId(), userDto.getId());
        Assertions.assertEquals(userDto.getName(), userDto.getName());
        Assertions.assertEquals(userDto.getEmail(), userDto.getEmail());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getWrongUsertTest() {
        NullObjectException exception = Assertions.assertThrows(NullObjectException.class,
                () -> {
                    userService.get(1);
                },
                "Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
        Assertions.assertEquals("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void updateWrongUsertTest() {
        NullObjectException exception = Assertions.assertThrows(NullObjectException.class,
                () -> {
                    userService.update(new UserDto(1, "nameUpdate", "emailUpdate@mail.ru"));
                },
                "Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
        Assertions.assertEquals("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void updateUserTest() {
        userService.add(userDto1);
        userService.update(new UserDto(1, "nameUpdate", "emailUpdate@mail.ru"));
        UserDto userDto = userService.get(1);
        Assertions.assertEquals(userDto.getId(), 1);
        Assertions.assertEquals(userDto.getName(), "nameUpdate");
        Assertions.assertEquals(userDto.getEmail(), "emailUpdate@mail.ru");
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void deleteFindAllTest() {
        userService.add(userDto1);
        Assertions.assertEquals(userService.getAll().size(), 1);
        userService.add(userDto2);
        Assertions.assertEquals(userService.getAll().size(), 2);
        userService.delete(1);
        Assertions.assertEquals(userService.getAll().size(), 1);
    }
}
