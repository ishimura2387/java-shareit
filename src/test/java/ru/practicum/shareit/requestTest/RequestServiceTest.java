package ru.practicum.shareit.requestTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestDtoOutput;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTest {
    private final UserService userService;
    private final RequestService requestService;
    private UserDto userDto1 = new UserDto(1, "user1", "user1@mail.ru");
    private UserDto userDto2 = new UserDto(2, "user2", "user2@mail.ru");
    private ItemDto itemDto1 = new ItemDto(1, "item 1", "description item 1",
            true, null, 0);
    private ItemDto itemDto2 = new ItemDto(2, "item 2", "description item 2",
            false, null, 1);
    private User user = new User(1, "user not dto 1", "userNotDto1@mail.ru");

    private User user2 = new User(2, "user not dto 2", "userNotDto2@mail.ru");
    private ItemRequestDto itemRequestDto = new ItemRequestDto(1, "description 1 request", user, LocalDateTime.now());

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void wrongAddRequestTest() {
        NullObjectException exception = Assertions.assertThrows(NullObjectException.class,
                () -> {
                    requestService.addRequest(1, itemRequestDto);
                },
                "Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
        Assertions.assertEquals("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void addAndGetTest() {
        userService.createUser(userDto1);
        requestService.addRequest(1, itemRequestDto);
        ItemRequestDtoOutput itemRequestDtoOutput = requestService.getRequest(1, 1);
        Assertions.assertEquals(itemRequestDtoOutput.getId(), itemRequestDto.getId());
        Assertions.assertEquals(itemRequestDtoOutput.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getRewuestWrongUserTest() {
        NullObjectException exception = Assertions.assertThrows(NullObjectException.class,
                () -> {
                    requestService.getRequest(1, 1);
                },
                "Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
        Assertions.assertEquals("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getRewuestWrongRequestTest() {
        userService.createUser(userDto1);
        NullObjectException exception = Assertions.assertThrows(NullObjectException.class,
                () -> {
                    requestService.getRequest(1, 1);
                },
                "Ошибка проверки запроса на наличие в Storage! Запрос не найден!");
        Assertions.assertEquals("Ошибка проверки запроса на наличие в Storage! Запрос не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getMyRequestWrongUserTest() {
        NullObjectException exception = Assertions.assertThrows(NullObjectException.class,
                () -> {
                    requestService.getMyRequest(1);
                },
                "Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
        Assertions.assertEquals("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getMyRequestTest() {
        userService.createUser(userDto1);
        userService.createUser(userDto2);
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(2, "description 2 request", user2, LocalDateTime.now());
        ItemRequestDto itemRequestDto3 = new ItemRequestDto(3, "description 3 request", user, LocalDateTime.now());
        requestService.addRequest(1, itemRequestDto);
        requestService.addRequest(2, itemRequestDto2);
        requestService.addRequest(1, itemRequestDto3);
        List<ItemRequestDtoOutput> requestDtoOutputs = requestService.getMyRequest(1);
        Assertions.assertEquals(requestDtoOutputs.get(0).getId(), itemRequestDto.getId());
        Assertions.assertEquals(requestDtoOutputs.get(0).getDescription(), itemRequestDto.getDescription());
        Assertions.assertEquals(requestDtoOutputs.get(1).getId(), itemRequestDto3.getId());
        Assertions.assertEquals(requestDtoOutputs.get(1).getDescription(), itemRequestDto3.getDescription());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllRequestWrongUserTest() {
        NullObjectException exception = Assertions.assertThrows(NullObjectException.class,
                () -> {
                    requestService.getAllRequestOtherUsers(1, 1, 2);
                },
                "Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
        Assertions.assertEquals("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllRequestWrongPaginationTest() {
        userService.createUser(userDto1);
        requestService.addRequest(1, itemRequestDto);
        PaginationException exception = Assertions.assertThrows(PaginationException.class,
                () -> {
                    requestService.getAllRequestOtherUsers(1, 1, -1);
                },
                "Ошибка пагинации!");
        Assertions.assertEquals("Ошибка пагинации!", exception.getMessage());
        PaginationException exception2 = Assertions.assertThrows(PaginationException.class,
                () -> {
                    requestService.getAllRequestOtherUsers(1, 1, 0);
                },
                "Ошибка пагинации!");
        Assertions.assertEquals("Ошибка пагинации!", exception2.getMessage());
        PaginationException exception3 = Assertions.assertThrows(PaginationException.class,
                () -> {
                    requestService.getAllRequestOtherUsers(1, -1, 2);
                },
                "Ошибка пагинации!");
        Assertions.assertEquals("Ошибка пагинации!", exception3.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllRequestTest() {
        userService.createUser(userDto1);
        userService.createUser(userDto2);
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(2, "description 2 request", user2, LocalDateTime.now());
        ItemRequestDto itemRequestDto3 = new ItemRequestDto(3, "description 3request", user, LocalDateTime.now());
        requestService.addRequest(1, itemRequestDto);
        requestService.addRequest(2, itemRequestDto2);
        requestService.addRequest(1, itemRequestDto3);
        List<ItemRequestDtoOutput> requestDtoOutputs = requestService.getAllRequestOtherUsers(1, 1, 2);
        Assertions.assertEquals(requestDtoOutputs.get(0).getId(), itemRequestDto2.getId());
        Assertions.assertEquals(requestDtoOutputs.get(0).getDescription(), itemRequestDto2.getDescription());
    }
}
