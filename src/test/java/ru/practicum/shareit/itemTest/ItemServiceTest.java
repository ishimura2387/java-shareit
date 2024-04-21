package ru.practicum.shareit.itemTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingInputDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.CommentException;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemDtoWithDate;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    LocalDateTime timeStart = LocalDateTime.now();
    private UserDto userDto1 = new UserDto(1, "user1", "user1@mail.ru");
    private UserDto userDto2 = new UserDto(2, "user2", "user2@mail.ru");
    private ItemDto itemDto1 = new ItemDto(1, "item 1", "description item 1",
            true, null, 0);
    private ItemDto itemDto2 = new ItemDto(2, "item 2", "description item 2",
            false, null, 1);
    private CommentDto commentDto = new CommentDto(1, "text comment 1", "Garry Potter", timeStart);

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void wrongAddCommentTest() {
        LocalDateTime start = LocalDateTime.now();
        userService.createUser(userDto1);
        userService.createUser(userDto2);
        itemService.createNewItem(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusHours(1), start.plusHours(2));
        bookingService.addBooking(bookingInputDto, 2);
        CommentException exception = Assertions.assertThrows(CommentException.class,
                () -> {
                    itemService.createNewComment(commentDto, 2, 1);
                },
                "Чтобы оставлять отзывы нужно иметь завершенное бронирование вещи!!");
        Assertions.assertEquals("Чтобы оставлять отзывы нужно иметь завершенное бронирование вещи!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void wrongUserAddCommentTest() throws InterruptedException {
        LocalDateTime start = LocalDateTime.now();
        userService.createUser(userDto1);
        userService.createUser(userDto2);
        itemService.createNewItem(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusSeconds(1), start.plusSeconds(2));
        bookingService.addBooking(bookingInputDto, 2);
        TimeUnit.SECONDS.sleep(3);
        NullObjectException exception = Assertions.assertThrows(NullObjectException.class,
                () -> {
                    itemService.createNewComment(commentDto, 3, 1);
                },
                "Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
        Assertions.assertEquals("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void wrongItemAddCommentTest() throws InterruptedException {
        LocalDateTime start = LocalDateTime.now();
        userService.createUser(userDto1);
        userService.createUser(userDto2);
        itemService.createNewItem(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusSeconds(1), start.plusSeconds(2));
        bookingService.addBooking(bookingInputDto, 2);
        TimeUnit.SECONDS.sleep(3);
        NullObjectException exception = Assertions.assertThrows(NullObjectException.class,
                () -> {
                    itemService.createNewComment(commentDto, 1, 2);
                },
                "Ошибка проверки вещи на наличие в Storage! Вещь не найдена!");
        Assertions.assertEquals("Ошибка проверки вещи на наличие в Storage! Вещь не найдена!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void createCommentTest() throws InterruptedException {
        LocalDateTime start = LocalDateTime.now();
        userService.createUser(userDto1);
        userService.createUser(userDto2);
        itemService.createNewItem(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusSeconds(1), start.plusSeconds(2));
        bookingService.addBooking(bookingInputDto, 2);
        TimeUnit.SECONDS.sleep(3);
        CommentDto commentDto1 = itemService.createNewComment(commentDto, 2, 1);
        CommentDto commentDto2 = new CommentDto(1, "text comment 1", "user2", timeStart);
        Assertions.assertEquals(commentDto1.getId(), commentDto2.getId());
        Assertions.assertEquals(commentDto1.getText(), commentDto2.getText());
        Assertions.assertEquals(commentDto1.getAuthorName(), commentDto2.getAuthorName());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void createItemWrongUserTest() {
        NullObjectException exception = Assertions.assertThrows(NullObjectException.class,
                () -> {
                    itemService.createNewItem(itemDto1, 1);
                },
                "Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
        Assertions.assertEquals("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void createItemWrongRequestTest() {
        userService.createUser(userDto1);
        NullObjectException exception = Assertions.assertThrows(NullObjectException.class,
                () -> {
                    itemService.createNewItem(itemDto2, 1);
                },
                "Ошибка проверки запроса на наличие в Storage! Запрос не найден!");
        Assertions.assertEquals("Ошибка проверки запроса на наличие в Storage! Запрос не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void createAndGetItemTest() {
        userService.createUser(userDto1);
        itemService.createNewItem(itemDto1, 1);
        ItemDtoWithDate itemDto = itemService.getItem(1, 1);
        Assertions.assertEquals(itemDto.getId(), itemDto1.getId());
        Assertions.assertEquals(itemDto.getName(), itemDto1.getName());
        Assertions.assertEquals(itemDto.getDescription(), itemDto1.getDescription());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void updateItemTest() {
        userService.createUser(userDto1);
        ItemDto itemDto = itemService.createNewItem(itemDto1, 1);
        ItemDto newItemDto = new ItemDto(1, null, "description new item", null, null, 0);
        ItemDto itemDto2 = itemService.updateItem(newItemDto, 1, 1);
        Assertions.assertEquals(itemDto.getId(), itemDto2.getId());
        Assertions.assertEquals(itemDto.getName(), itemDto2.getName());
        Assertions.assertNotEquals(itemDto.getDescription(), itemDto2.getDescription());
        Assertions.assertEquals(itemDto2.getDescription(), "description new item");
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getItemWrongItemTest() {
        NullObjectException exception = Assertions.assertThrows(NullObjectException.class,
                () -> {
                    itemService.getItem(1, 1);
                },
                "Ошибка проверки вещи на наличие в Storage! Вещь не найдена!");
        Assertions.assertEquals("Ошибка проверки вещи на наличие в Storage! Вещь не найдена!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getMyItemsWrongUserTest() {
        NullObjectException exception = Assertions.assertThrows(NullObjectException.class,
                () -> {
                    itemService.getMyItems(1, 1, 2);
                },
                "Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
        Assertions.assertEquals("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getMyItemsNullSizeTest() {
        userService.createUser(userDto1);
        itemService.createNewItem(itemDto1, 1);
        List<ItemDtoWithDate> itemDtoWithDates = itemService.getMyItems(1, 1, 1);
        Assertions.assertEquals(itemDtoWithDates.get(0).getId(), itemDto1.getId());
        Assertions.assertEquals(itemDtoWithDates.get(0).getName(), itemDto1.getName());
        Assertions.assertEquals(itemDtoWithDates.get(0).getDescription(), itemDto1.getDescription());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getMyItemsWithPaginationTest() {
        userService.createUser(userDto1);
        itemService.createNewItem(itemDto1, 1);
        List<ItemDtoWithDate> itemDtoWithDates = itemService.getMyItems(1, 0, null);
        Assertions.assertEquals(itemDtoWithDates.get(0).getId(), itemDto1.getId());
        Assertions.assertEquals(itemDtoWithDates.get(0).getName(), itemDto1.getName());
        Assertions.assertEquals(itemDtoWithDates.get(0).getDescription(), itemDto1.getDescription());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getMyItemsWrongPaginationTest() {
        userService.createUser(userDto1);
        itemService.createNewItem(itemDto1, 1);
        PaginationException exception = Assertions.assertThrows(PaginationException.class,
                () -> {
                    itemService.getMyItems(1, 1, -1);
                },
                "Ошибка пагинации!");
        Assertions.assertEquals("Ошибка пагинации!", exception.getMessage());
        PaginationException exception2 = Assertions.assertThrows(PaginationException.class,
                () -> {
                    itemService.getMyItems(1, 1, 0);
                },
                "Ошибка пагинации!");
        Assertions.assertEquals("Ошибка пагинации!", exception2.getMessage());
        PaginationException exception3 = Assertions.assertThrows(PaginationException.class,
                () -> {
                    itemService.getMyItems(1, -1, 2);
                },
                "Ошибка пагинации!");
        Assertions.assertEquals("Ошибка пагинации!", exception3.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getItemsForRentNullSizeTest() {
        userService.createUser(userDto1);
        itemService.createNewItem(itemDto1, 1);
        List<ItemDto> itemDto = itemService.getItemsForRent("item", 1, 2);
        Assertions.assertEquals(itemDto.get(0).getId(), itemDto1.getId());
        Assertions.assertEquals(itemDto.get(0).getName(), itemDto1.getName());
        Assertions.assertEquals(itemDto.get(0).getDescription(), itemDto1.getDescription());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getItemsFroRentWrongPaginationTest() {
        userService.createUser(userDto1);
        itemService.createNewItem(itemDto1, 1);
        PaginationException exception = Assertions.assertThrows(PaginationException.class,
                () -> {
                    itemService.getItemsForRent("item", 1, -1);
                },
                "Ошибка пагинации!");
        Assertions.assertEquals("Ошибка пагинации!", exception.getMessage());
        PaginationException exception2 = Assertions.assertThrows(PaginationException.class,
                () -> {
                    itemService.getItemsForRent("item", 1, 0);
                },
                "Ошибка пагинации!");
        Assertions.assertEquals("Ошибка пагинации!", exception2.getMessage());
        PaginationException exception3 = Assertions.assertThrows(PaginationException.class,
                () -> {
                    itemService.getItemsForRent("item", -1, 2);
                },
                "Ошибка пагинации!");
        Assertions.assertEquals("Ошибка пагинации!", exception3.getMessage());
    }
}
