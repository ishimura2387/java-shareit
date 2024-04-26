package ru.practicum.shareit.itemTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingInputDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemWithDateResponseDto;
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
    Sort sort = Sort.by(Sort.Direction.DESC, "id");
    Pageable pageable = PageRequest.of(0, 1, sort);

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void wrongAddCommentTest() {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusHours(1), start.plusHours(2));
        bookingService.add(bookingInputDto, 2);
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    itemService.addComment(commentDto, 2, 1);
                },
                "Чтобы оставлять отзывы нужно иметь завершенное бронирование вещи!!");
        Assertions.assertEquals("Чтобы оставлять отзывы нужно иметь завершенное бронирование вещи!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void wrongUserAddCommentTest() throws InterruptedException {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusSeconds(1), start.plusSeconds(2));
        bookingService.add(bookingInputDto, 2);
        TimeUnit.SECONDS.sleep(3);
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    itemService.addComment(commentDto, 3, 1);
                },
                "Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
        Assertions.assertEquals("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void wrongItemAddCommentTest() throws InterruptedException {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusSeconds(1), start.plusSeconds(2));
        bookingService.add(bookingInputDto, 2);
        TimeUnit.SECONDS.sleep(3);
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    itemService.addComment(commentDto, 1, 2);
                },
                "Ошибка проверки вещи на наличие в Storage! Вещь не найдена!");
        Assertions.assertEquals("Ошибка проверки вещи на наличие в Storage! Вещь не найдена!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void createCommentTest() throws InterruptedException {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusSeconds(1), start.plusSeconds(2));
        bookingService.add(bookingInputDto, 2);
        TimeUnit.SECONDS.sleep(3);
        CommentDto commentDto1 = itemService.addComment(commentDto, 2, 1);
        CommentDto commentDto2 = new CommentDto(1, "text comment 1", "user2", timeStart);
        Assertions.assertEquals(commentDto1.getId(), commentDto2.getId());
        Assertions.assertEquals(commentDto1.getText(), commentDto2.getText());
        Assertions.assertEquals(commentDto1.getAuthorName(), commentDto2.getAuthorName());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void createItemWrongUserTest() {
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    itemService.add(itemDto1, 1);
                },
                "Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
        Assertions.assertEquals("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void createItemWrongRequestTest() {
        userService.add(userDto1);
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    itemService.add(itemDto2, 1);
                },
                "Ошибка проверки запроса на наличие в Storage! Запрос не найден!");
        Assertions.assertEquals("Ошибка проверки запроса на наличие в Storage! Запрос не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void createAndGetItemTest() {
        userService.add(userDto1);
        itemService.add(itemDto1, 1);
        ItemWithDateResponseDto itemDto = itemService.get(1, 1);
        Assertions.assertEquals(itemDto.getId(), itemDto1.getId());
        Assertions.assertEquals(itemDto.getName(), itemDto1.getName());
        Assertions.assertEquals(itemDto.getDescription(), itemDto1.getDescription());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void updateItemTest() {
        userService.add(userDto1);
        ItemDto itemDto = itemService.add(itemDto1, 1);
        ItemDto newItemDto = new ItemDto(1, null, "description new item", null, null, 0);
        ItemDto itemDto2 = itemService.update(newItemDto, 1, 1);
        Assertions.assertEquals(itemDto.getId(), itemDto2.getId());
        Assertions.assertEquals(itemDto.getName(), itemDto2.getName());
        Assertions.assertNotEquals(itemDto.getDescription(), itemDto2.getDescription());
        Assertions.assertEquals(itemDto2.getDescription(), "description new item");
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getItemWrongItemTest() {
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    itemService.get(1, 1);
                },
                "Ошибка проверки вещи на наличие в Storage! Вещь не найдена!");
        Assertions.assertEquals("Ошибка проверки вещи на наличие в Storage! Вещь не найдена!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllItemsWrongUserTest() {
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    itemService.getAllSort(1, sort);
                },
                "Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
        Assertions.assertEquals("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllItemsWithSortTest() {
        userService.add(userDto1);
        itemService.add(itemDto1, 1);
        List<ItemWithDateResponseDto> itemWithDateResponseDtos = itemService.getAllSort(1, sort);
        Assertions.assertEquals(itemWithDateResponseDtos.get(0).getId(), itemDto1.getId());
        Assertions.assertEquals(itemWithDateResponseDtos.get(0).getName(), itemDto1.getName());
        Assertions.assertEquals(itemWithDateResponseDtos.get(0).getDescription(), itemDto1.getDescription());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllItemsWithSortWithBookingTest() throws InterruptedException {
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        LocalDateTime start = LocalDateTime.now();
        BookingInputDto bookingDto1 = new BookingInputDto(1, 1, start.plusSeconds(1),  start.plusSeconds(2));
        BookingInputDto bookingDto2 = new BookingInputDto(2, 1, start.plusSeconds(50),  start.plusSeconds(55));
        bookingService.add(bookingDto1, 2);
        bookingService.add(bookingDto2, 2);
        bookingService.setStatus(1,true, 1);
        bookingService.setStatus(2,true, 1);
        CommentDto commentDto = new CommentDto(1, "text", "name", LocalDateTime.now());
        TimeUnit.SECONDS.sleep(3);
        itemService.addComment(commentDto, 2, 1);
        List<ItemWithDateResponseDto> itemWithDateResponseDtos = itemService.getAllSort(1, sort);
        Assertions.assertEquals(itemWithDateResponseDtos.get(0).getId(), itemDto1.getId());
        Assertions.assertEquals(itemWithDateResponseDtos.get(0).getName(), itemDto1.getName());
        Assertions.assertEquals(itemWithDateResponseDtos.get(0).getDescription(), itemDto1.getDescription());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getItemWithBookingTest() throws InterruptedException {
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        LocalDateTime start = LocalDateTime.now();
        BookingInputDto bookingDto1 = new BookingInputDto(1, 1, start.plusSeconds(1),  start.plusSeconds(2));
        BookingInputDto bookingDto2 = new BookingInputDto(2, 1, start.plusSeconds(50),  start.plusSeconds(55));
        bookingService.add(bookingDto1, 2);
        bookingService.add(bookingDto2, 2);
        bookingService.setStatus(1,true, 1);
        bookingService.setStatus(2,true, 1);
        CommentDto commentDto = new CommentDto(1, "text", "name", LocalDateTime.now());
        TimeUnit.SECONDS.sleep(3);
        itemService.addComment(commentDto, 2, 1);
        ItemWithDateResponseDto itemWithDateResponseDtos = itemService.get(1, 1);
        Assertions.assertEquals(itemWithDateResponseDtos.getId(), itemDto1.getId());
        Assertions.assertEquals(itemWithDateResponseDtos.getName(), itemDto1.getName());
        Assertions.assertEquals(itemWithDateResponseDtos.getDescription(), itemDto1.getDescription());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllItemsWithPaginationTest() {
        userService.add(userDto1);
        itemService.add(itemDto1, 1);
        List<ItemWithDateResponseDto> itemWithDateResponseDtos = itemService.getAllPageable(1, pageable);
        Assertions.assertEquals(itemWithDateResponseDtos.get(0).getId(), itemDto1.getId());
        Assertions.assertEquals(itemWithDateResponseDtos.get(0).getName(), itemDto1.getName());
        Assertions.assertEquals(itemWithDateResponseDtos.get(0).getDescription(), itemDto1.getDescription());
    }


    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getItemsForRentPageableTest() {
        userService.add(userDto1);
        itemService.add(itemDto1, 1);
        List<ItemDto> itemDto = itemService.searchPageable("item", pageable);
        Assertions.assertEquals(itemDto.get(0).getId(), itemDto1.getId());
        Assertions.assertEquals(itemDto.get(0).getName(), itemDto1.getName());
        Assertions.assertEquals(itemDto.get(0).getDescription(), itemDto1.getDescription());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getItemsForRentSortTest() {
        userService.add(userDto1);
        itemService.add(itemDto1, 1);
        List<ItemDto> itemDto = itemService.searchSort("item", sort);
        Assertions.assertEquals(itemDto.get(0).getId(), itemDto1.getId());
        Assertions.assertEquals(itemDto.get(0).getName(), itemDto1.getName());
        Assertions.assertEquals(itemDto.get(0).getDescription(), itemDto1.getDescription());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllItemsPageableWithBookingTest() throws InterruptedException {
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        LocalDateTime start = LocalDateTime.now();
        BookingInputDto bookingDto1 = new BookingInputDto(1, 1, start.plusSeconds(1),  start.plusSeconds(2));
        BookingInputDto bookingDto2 = new BookingInputDto(2, 1, start.plusSeconds(50),  start.plusSeconds(55));
        CommentDto commentDto = new CommentDto(1, "text", "name", LocalDateTime.now());
        bookingService.add(bookingDto1, 2);
        bookingService.add(bookingDto2, 2);
        bookingService.setStatus(1,true, 1);
        bookingService.setStatus(2,true, 1);
        TimeUnit.SECONDS.sleep(3);
        itemService.addComment(commentDto, 2, 1);
        List<ItemWithDateResponseDto> itemWithDateResponseDtos = itemService.getAllPageable(1, pageable);
        Assertions.assertEquals(itemWithDateResponseDtos.get(0).getId(), itemDto1.getId());
        Assertions.assertEquals(itemWithDateResponseDtos.get(0).getName(), itemDto1.getName());
        Assertions.assertEquals(itemWithDateResponseDtos.get(0).getDescription(), itemDto1.getDescription());
    }
}
