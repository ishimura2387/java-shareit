package ru.practicum.shareit.requestTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestWithItemsResponseDto;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTest {
    private final UserService userService;
    private final RequestService requestService;
    private final ItemService itemService;
    private UserDto userDto1 = new UserDto(1, "user1", "user1@mail.ru");
    private UserDto userDto2 = new UserDto(2, "user2", "user2@mail.ru");
    private ItemDto itemDto1 = new ItemDto(1, "item 1", "description item 1",
            true, null, 0);
    private ItemDto itemDto2 = new ItemDto(2, "item 2", "description item 2",
            false, null, 1);
    private User user = new User(1, "user not dto 1", "userNotDto1@mail.ru");

    private User user2 = new User(2, "user not dto 2", "userNotDto2@mail.ru");
    private ItemRequestDto itemRequestDto = new ItemRequestDto(1, "description 1 request", user, LocalDateTime.now());

    Sort sort = Sort.by(Sort.Direction.DESC, "created");
    Pageable pageable = PageRequest.of(0, 1, sort);

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void wrongAddRequestTest() {
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    requestService.add(1, itemRequestDto);
                },
                "Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
        Assertions.assertEquals("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void addAndGetTest() {
        userService.add(userDto1);
        requestService.add(1, itemRequestDto);
        ItemRequestWithItemsResponseDto itemRequestWithItemsResponseDto = requestService.get(1, 1);
        Assertions.assertEquals(itemRequestWithItemsResponseDto.getId(), itemRequestDto.getId());
        Assertions.assertEquals(itemRequestWithItemsResponseDto.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getRequestWrongUserTest() {
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    requestService.get(1, 1);
                },
                "Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
        Assertions.assertEquals("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getRequestWrongRequestTest() {
        userService.add(userDto1);
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    requestService.get(1, 1);
                },
                "Ошибка проверки запроса на наличие в Storage! Запрос не найден!");
        Assertions.assertEquals("Ошибка проверки запроса на наличие в Storage! Запрос не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getMyRequestWrongUserTest() {
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    requestService.getAll(1);
                },
                "Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
        Assertions.assertEquals("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getMyRequestTest() {
        userService.add(userDto1);
        userService.add(userDto2);
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(2, "description 2 request", user2, LocalDateTime.now());
        ItemRequestDto itemRequestDto3 = new ItemRequestDto(3, "description 3 request", user, LocalDateTime.now());
        requestService.add(1, itemRequestDto);
        requestService.add(2, itemRequestDto2);
        requestService.add(1, itemRequestDto3);
        List<ItemRequestWithItemsResponseDto> requestDtoOutputs = requestService.getAll(1);
        Assertions.assertEquals(requestDtoOutputs.get(0).getId(), itemRequestDto.getId());
        Assertions.assertEquals(requestDtoOutputs.get(0).getDescription(), itemRequestDto.getDescription());
        Assertions.assertEquals(requestDtoOutputs.get(1).getId(), itemRequestDto3.getId());
        Assertions.assertEquals(requestDtoOutputs.get(1).getDescription(), itemRequestDto3.getDescription());
        ItemDto itemDto3 = new ItemDto(3, "item 3", "description item 2",
                false, null, 3);
        itemService.add(itemDto1, 1);
        itemService.add(itemDto2,2);
        itemService.add(itemDto3,2);
        List<ItemRequestWithItemsResponseDto> requestDtoOutputs2 = requestService.getAll(1);
        Assertions.assertEquals(requestDtoOutputs2.get(0).getId(), itemRequestDto.getId());
        Assertions.assertEquals(requestDtoOutputs2.get(0).getDescription(), itemRequestDto.getDescription());
        Assertions.assertEquals(requestDtoOutputs2.get(1).getId(), itemRequestDto3.getId());
        Assertions.assertEquals(requestDtoOutputs2.get(1).getDescription(), itemRequestDto3.getDescription());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllRequestWrongUserTest() {
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    requestService.getAllOtherPageable(1, pageable);
                },
                "Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
        Assertions.assertEquals("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllRequestTest() {
        userService.add(userDto1);
        userService.add(userDto2);
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(2, "description 2 request", user2, LocalDateTime.now());
        ItemRequestDto itemRequestDto3 = new ItemRequestDto(3, "description 3 request", user, LocalDateTime.now());
        requestService.add(1, itemRequestDto);
        requestService.add(2, itemRequestDto2);
        requestService.add(2, itemRequestDto3);
        List<ItemRequestWithItemsResponseDto> requestDtoOutputs = requestService.getAllOtherPageable(1, pageable);
        Assertions.assertEquals(requestDtoOutputs.get(0).getId(), itemRequestDto3.getId());
        Assertions.assertEquals(requestDtoOutputs.get(0).getDescription(), itemRequestDto3.getDescription());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllRequestSortTest() {
        userService.add(userDto1);
        userService.add(userDto2);
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(2, "description 2 request", user2, LocalDateTime.now());
        ItemRequestDto itemRequestDto3 = new ItemRequestDto(3, "description 3request", user, LocalDateTime.now());
        requestService.add(1, itemRequestDto);
        requestService.add(2, itemRequestDto2);
        requestService.add(1, itemRequestDto3);
        List<ItemRequestWithItemsResponseDto> requestDtoOutputs = requestService.getAllOtherSort(1, sort);
        Assertions.assertEquals(requestDtoOutputs.get(0).getId(), itemRequestDto2.getId());
        Assertions.assertEquals(requestDtoOutputs.get(0).getDescription(), itemRequestDto2.getDescription());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllRequestOtherSortTest() {
        userService.add(userDto1);
        userService.add(userDto2);
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(2, "description 2 request", user2, LocalDateTime.now());
        ItemRequestDto itemRequestDto3 = new ItemRequestDto(3, "description 3 request", user, LocalDateTime.now());
        requestService.add(1, itemRequestDto);
        requestService.add(2, itemRequestDto2);
        requestService.add(1, itemRequestDto3);
        ItemDto itemDto3 = new ItemDto(3, "item 3", "description item 2",
                false, null, 3);
        itemService.add(itemDto1, 1);
        itemService.add(itemDto2,2);
        itemService.add(itemDto3,2);
        List<ItemRequestWithItemsResponseDto> requestDtoOutputs = requestService.getAllOtherSort(2, sort);
        Assertions.assertEquals(requestDtoOutputs.get(0).getId(), itemRequestDto.getId());
        Assertions.assertEquals(requestDtoOutputs.get(0).getDescription(), itemRequestDto.getDescription());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllRequestNullSizeWithPaginationTest() {
        userService.add(userDto1);
        userService.add(userDto2);
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(2, "description 2 request", user2, LocalDateTime.now());
        ItemRequestDto itemRequestDto3 = new ItemRequestDto(3, "description 3 request", user, LocalDateTime.now());
        requestService.add(1, itemRequestDto);
        requestService.add(2, itemRequestDto2);
        requestService.add(1, itemRequestDto3);
        ItemDto itemDto3 = new ItemDto(3, "item 3", "description item 2",
                false, null, 3);
        itemService.add(itemDto1, 1);
        itemService.add(itemDto2,2);
        itemService.add(itemDto3,2);
        List<ItemRequestWithItemsResponseDto> requestDtoOutputs = requestService.getAllOtherPageable(2, pageable);
        Assertions.assertEquals(requestDtoOutputs.get(0).getId(), itemRequestDto3.getId());
        Assertions.assertEquals(requestDtoOutputs.get(0).getDescription(), itemRequestDto3.getDescription());
    }
}
