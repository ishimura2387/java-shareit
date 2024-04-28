package ru.practicum.shareit.bookingTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingInputDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemDto;
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
public class BookingServiceTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    private UserDto userDto1 = new UserDto(1, "user1", "user1@mail.ru");
    private UserDto userDto2 = new UserDto(2, "user2", "user2@mail.ru");
    private ItemDto itemDto1 = new ItemDto(1, "item 1", "description item 1",
            true, null, 0);
    private ItemDto itemDto2 = new ItemDto(2, "item 2", "description item 2",
            false, null, 0);
    Sort sort = Sort.by(Sort.Direction.DESC, "start");
    Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start"));

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void wrongItemAddBookingTest() {
        LocalDateTime start = LocalDateTime.now();
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusHours(1), start.plusHours(2));
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    bookingService.add(bookingInputDto, 1);
                },
                "Ошибка проверки вещи на наличие в Storage! Вещь не найдена!");
        Assertions.assertEquals("Ошибка проверки вещи на наличие в Storage! Вещь не найдена!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void wrongBookerAddBookingTest() {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusHours(1), start.plusHours(2));
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    bookingService.add(bookingInputDto, 2);
                },
                "Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
        Assertions.assertEquals("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void wrongAvailableAddBookingTest() {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        itemService.add(itemDto2, 2);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 2, start.plusHours(1), start.plusHours(2));
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    bookingService.add(bookingInputDto, 1);
                },
                "Вещь недоступна для бронирования!");
        Assertions.assertEquals("Вещь недоступна для бронирования!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void ownerBookItemAddBookingTest() {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusHours(1), start.plusHours(2));
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    bookingService.add(bookingInputDto, 1);
                },
                "Пользователь не может забронировать свою вещь!");
        Assertions.assertEquals("Пользователь не может забронировать свою вещь!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void validTimeBookingTestTest() {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusHours(2), start.plusHours(1));
        BookingInputDto bookingInputDto2 = new BookingInputDto(2, 1, start.minusHours(1), start.plusHours(2));
        BookingInputDto bookingInputDto3 = new BookingInputDto(3, 1, start.plusHours(1), start.plusHours(1));
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    bookingService.add(bookingInputDto, 2);
                },
                "Время бронирования не корректно!");
        Assertions.assertEquals("Время бронирования не корректно!", exception.getMessage());
        IllegalArgumentException exception2 = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    bookingService.add(bookingInputDto2, 2);
                },
                "Время бронирования не корректно!");
        Assertions.assertEquals("Время бронирования не корректно!", exception2.getMessage());
        IllegalArgumentException exception3 = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    bookingService.add(bookingInputDto3, 2);
                },
                "Время бронирования не корректно!");
        Assertions.assertEquals("Время бронирования не корректно!", exception3.getMessage());
        BookingInputDto bookingInputDto4 = new BookingInputDto(4, 1, start.plusHours(1), start.plusHours(3));
        BookingInputDto bookingInputDto5 = new BookingInputDto(5, 1, start.plusHours(2), start.plusHours(4));
        bookingService.add(bookingInputDto4, 2);
        IllegalArgumentException exception4 = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    bookingService.add(bookingInputDto5, 2);
                },
                "На данное время уже есть бронирование!");
        Assertions.assertEquals("На данное время уже есть бронирование!", exception4.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void addAndGetOwnerTest() {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto1 = new BookingInputDto(1, 1, start.plusHours(1), start.plusHours(3));
        bookingService.add(bookingInputDto1, 2);
        BookingDto bookingDto1 = bookingService.get(1, 1);
        BookingDto bookingDto2 = new BookingDto(1, start.plusHours(1), start.plusHours(3), itemDto1, userDto2, Status.WAITING);
        Assertions.assertEquals(bookingDto1.getId(), bookingDto2.getId());
        Assertions.assertEquals(bookingDto1.getStart(), bookingDto2.getStart());
        Assertions.assertEquals(bookingDto1.getEnd(), bookingDto2.getEnd());
        Assertions.assertEquals(bookingDto1.getItem().getId(), bookingDto2.getItem().getId());
        Assertions.assertEquals(bookingDto1.getBooker().getId(), bookingDto2.getBooker().getId());
        Assertions.assertEquals(bookingDto1.getStatus(), bookingDto2.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getErrorBookingTest() {
        userService.add(userDto1);
        userService.add(userDto2);
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    bookingService.get(2, 2);
                },
                "Ошибка проверки бронирования на наличие в Storage! Бронирование не найдено!");
        Assertions.assertEquals("Ошибка проверки бронирования на наличие в Storage! Бронирование не найдено!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void setStatusErrorBookingTest() {
        userService.add(userDto1);
        userService.add(userDto2);
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    bookingService.setStatus(2, true, 2);
                },
                "Ошибка проверки бронирования на наличие в Storage! Бронирование не найдено!");
        Assertions.assertEquals("Ошибка проверки бронирования на наличие в Storage! Бронирование не найдено!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void setStatusNoApprovedExc() {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusHours(1), start.plusHours(3));
        bookingService.add(bookingInputDto, 2);
        bookingService.setStatus(1, true, 1);
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    bookingService.setStatus(1, true, 1);
                },
                "Статус уже подтвержден!");
        Assertions.assertEquals("Статус уже подтвержден!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void setStatusNoOwnerTest() {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusHours(1), start.plusHours(3));
        bookingService.add(bookingInputDto, 2);
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    bookingService.setStatus(1, true, 2);
                },
                "Подтверждение бронирования доступно только владельцу вещи!");
        Assertions.assertEquals("Подтверждение бронирования доступно только владельцу вещи!", exception.getMessage());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void setStatusTest() {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusHours(1), start.plusHours(3));
        bookingService.add(bookingInputDto, 2);
        bookingService.setStatus(1, true, 1);
        BookingDto bookingDto1 = bookingService.get(1, 1);
        BookingDto bookingDto2 = new BookingDto(1, start.plusHours(1), start.plusHours(3), itemDto1, userDto2, Status.APPROVED);
        Assertions.assertEquals(bookingDto1.getId(), bookingDto2.getId());
        Assertions.assertEquals(bookingDto1.getStart(), bookingDto2.getStart());
        Assertions.assertEquals(bookingDto1.getEnd(), bookingDto2.getEnd());
        Assertions.assertEquals(bookingDto1.getItem().getId(), bookingDto2.getItem().getId());
        Assertions.assertEquals(bookingDto1.getBooker().getId(), bookingDto2.getBooker().getId());
        Assertions.assertEquals(bookingDto1.getStatus(), bookingDto2.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getBookingByUserCurrentTest() throws InterruptedException {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusSeconds(1), start.plusSeconds(20));
        bookingService.add(bookingInputDto, 2);
        TimeUnit.SECONDS.sleep(2);
        List<BookingDto> bookingDtos = bookingService.getAllByUser(2, "CURRENT", pageable);
        BookingDto bookingDto1 = bookingDtos.get(0);
        BookingDto bookingDto2 = new BookingDto(1, start.plusSeconds(1), start.plusSeconds(20), itemDto1, userDto2, Status.WAITING);
        Assertions.assertEquals(bookingDto1.getId(), bookingDto2.getId());
        Assertions.assertEquals(bookingDto1.getStart(), bookingDto2.getStart());
        Assertions.assertEquals(bookingDto1.getEnd(), bookingDto2.getEnd());
        Assertions.assertEquals(bookingDto1.getItem().getId(), bookingDto2.getItem().getId());
        Assertions.assertEquals(bookingDto1.getBooker().getId(), bookingDto2.getBooker().getId());
        Assertions.assertEquals(bookingDto1.getStatus(), bookingDto2.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getBookingByUserPastTest() throws InterruptedException {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusSeconds(1), start.plusSeconds(2));
        bookingService.add(bookingInputDto, 2);
        TimeUnit.SECONDS.sleep(3);
        List<BookingDto> bookingDtos = bookingService.getAllByUser(2, "PAST", pageable);
        BookingDto bookingDto1 = bookingDtos.get(0);
        BookingDto bookingDto2 = new BookingDto(1, start.plusSeconds(1), start.plusSeconds(2), itemDto1, userDto2, Status.WAITING);
        Assertions.assertEquals(bookingDto1.getId(), bookingDto2.getId());
        Assertions.assertEquals(bookingDto1.getStart(), bookingDto2.getStart());
        Assertions.assertEquals(bookingDto1.getEnd(), bookingDto2.getEnd());
        Assertions.assertEquals(bookingDto1.getItem().getId(), bookingDto2.getItem().getId());
        Assertions.assertEquals(bookingDto1.getBooker().getId(), bookingDto2.getBooker().getId());
        Assertions.assertEquals(bookingDto1.getStatus(), bookingDto2.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getBookingByUserFutureTest() {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusHours(1), start.plusHours(3));
        bookingService.add(bookingInputDto, 2);
        List<BookingDto> bookingDtos = bookingService.getAllByUser(2, "FUTURE", pageable);
        BookingDto bookingDto1 = bookingDtos.get(0);
        BookingDto bookingDto2 = new BookingDto(1, start.plusHours(1), start.plusHours(3), itemDto1, userDto2, Status.WAITING);
        Assertions.assertEquals(bookingDto1.getId(), bookingDto2.getId());
        Assertions.assertEquals(bookingDto1.getStart(), bookingDto2.getStart());
        Assertions.assertEquals(bookingDto1.getEnd(), bookingDto2.getEnd());
        Assertions.assertEquals(bookingDto1.getItem().getId(), bookingDto2.getItem().getId());
        Assertions.assertEquals(bookingDto1.getBooker().getId(), bookingDto2.getBooker().getId());
        Assertions.assertEquals(bookingDto1.getStatus(), bookingDto2.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getBookingByUserRejectedTest() {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusSeconds(1), start.plusSeconds(2));
        bookingService.add(bookingInputDto, 2);
        bookingService.setStatus(1, false, 1);
        List<BookingDto> bookingDtos = bookingService.getAllByUser(2, "REJECTED", pageable);
        BookingDto bookingDto1 = bookingDtos.get(0);
        BookingDto bookingDto2 = new BookingDto(1, start.plusSeconds(1), start.plusSeconds(2), itemDto1, userDto2, Status.REJECTED);
        Assertions.assertEquals(bookingDto1.getId(), bookingDto2.getId());
        Assertions.assertEquals(bookingDto1.getStart(), bookingDto2.getStart());
        Assertions.assertEquals(bookingDto1.getEnd(), bookingDto2.getEnd());
        Assertions.assertEquals(bookingDto1.getItem().getId(), bookingDto2.getItem().getId());
        Assertions.assertEquals(bookingDto1.getBooker().getId(), bookingDto2.getBooker().getId());
        Assertions.assertEquals(bookingDto1.getStatus(), bookingDto2.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getBookingByUserDefaultTest() {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusSeconds(1), start.plusSeconds(2));
        bookingService.add(bookingInputDto, 2);
        List<BookingDto> bookingDtos = bookingService.getAllByUser(2, "ALL", pageable);
        BookingDto bookingDto1 = bookingDtos.get(0);
        BookingDto bookingDto2 = new BookingDto(1, start.plusSeconds(1), start.plusSeconds(2), itemDto1, userDto2, Status.WAITING);
        Assertions.assertEquals(bookingDto1.getId(), bookingDto2.getId());
        Assertions.assertEquals(bookingDto1.getStart(), bookingDto2.getStart());
        Assertions.assertEquals(bookingDto1.getEnd(), bookingDto2.getEnd());
        Assertions.assertEquals(bookingDto1.getItem().getId(), bookingDto2.getItem().getId());
        Assertions.assertEquals(bookingDto1.getBooker().getId(), bookingDto2.getBooker().getId());
        Assertions.assertEquals(bookingDto1.getStatus(), bookingDto2.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getBookingByUserWaitingTest() throws InterruptedException {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusSeconds(1), start.plusSeconds(2));
        bookingService.add(bookingInputDto, 2);
        List<BookingDto> bookingDtos = bookingService.getAllByUser(2, "WAITING", pageable);
        BookingDto bookingDto1 = bookingDtos.get(0);
        BookingDto bookingDto2 = new BookingDto(1, start.plusSeconds(1), start.plusSeconds(2), itemDto1, userDto2, Status.WAITING);
        Assertions.assertEquals(bookingDto1.getId(), bookingDto2.getId());
        Assertions.assertEquals(bookingDto1.getStart(), bookingDto2.getStart());
        Assertions.assertEquals(bookingDto1.getEnd(), bookingDto2.getEnd());
        Assertions.assertEquals(bookingDto1.getItem().getId(), bookingDto2.getItem().getId());
        Assertions.assertEquals(bookingDto1.getBooker().getId(), bookingDto2.getBooker().getId());
        Assertions.assertEquals(bookingDto1.getStatus(), bookingDto2.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getBookingByOwnerFutureTest() {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusHours(1), start.plusHours(3));
        bookingService.add(bookingInputDto, 2);
        List<BookingDto> bookingDtos = bookingService.getAllByOwner(1, "FUTURE", pageable);
        System.out.println(bookingDtos.size());
        BookingDto bookingDto1 = bookingDtos.get(0);
        BookingDto bookingDto2 = new BookingDto(1, start.plusHours(1), start.plusHours(3), itemDto1, userDto2, Status.WAITING);
        Assertions.assertEquals(bookingDto1.getId(), bookingDto2.getId());
        Assertions.assertEquals(bookingDto1.getStart(), bookingDto2.getStart());
        Assertions.assertEquals(bookingDto1.getEnd(), bookingDto2.getEnd());
        Assertions.assertEquals(bookingDto1.getItem().getId(), bookingDto2.getItem().getId());
        Assertions.assertEquals(bookingDto1.getBooker().getId(), bookingDto2.getBooker().getId());
        Assertions.assertEquals(bookingDto1.getStatus(), bookingDto2.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getBookingByOwnerPastTest() throws InterruptedException {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusSeconds(1), start.plusSeconds(2));
        bookingService.add(bookingInputDto, 2);
        TimeUnit.SECONDS.sleep(3);
        List<BookingDto> bookingDtos = bookingService.getAllByOwner(1, "PAST", pageable);
        BookingDto bookingDto1 = bookingDtos.get(0);
        BookingDto bookingDto2 = new BookingDto(1, start.plusSeconds(1), start.plusSeconds(2), itemDto1, userDto2, Status.WAITING);
        Assertions.assertEquals(bookingDto1.getId(), bookingDto2.getId());
        Assertions.assertEquals(bookingDto1.getStart(), bookingDto2.getStart());
        Assertions.assertEquals(bookingDto1.getEnd(), bookingDto2.getEnd());
        Assertions.assertEquals(bookingDto1.getItem().getId(), bookingDto2.getItem().getId());
        Assertions.assertEquals(bookingDto1.getBooker().getId(), bookingDto2.getBooker().getId());
        Assertions.assertEquals(bookingDto1.getStatus(), bookingDto2.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getBookingByOwnerCurrentTest() throws InterruptedException {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusSeconds(1), start.plusSeconds(10));
        bookingService.add(bookingInputDto, 2);
        TimeUnit.SECONDS.sleep(2);
        List<BookingDto> bookingDtos = bookingService.getAllByOwner(1, "CURRENT", pageable);
        BookingDto bookingDto1 = bookingDtos.get(0);
        BookingDto bookingDto2 = new BookingDto(1, start.plusSeconds(1), start.plusSeconds(10), itemDto1, userDto2, Status.WAITING);
        Assertions.assertEquals(bookingDto1.getId(), bookingDto2.getId());
        Assertions.assertEquals(bookingDto1.getStart(), bookingDto2.getStart());
        Assertions.assertEquals(bookingDto1.getEnd(), bookingDto2.getEnd());
        Assertions.assertEquals(bookingDto1.getItem().getId(), bookingDto2.getItem().getId());
        Assertions.assertEquals(bookingDto1.getBooker().getId(), bookingDto2.getBooker().getId());
        Assertions.assertEquals(bookingDto1.getStatus(), bookingDto2.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getBookingByOwnerWaitingTest() throws InterruptedException {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusSeconds(1), start.plusSeconds(10));
        bookingService.add(bookingInputDto, 2);
        List<BookingDto> bookingDtos = bookingService.getAllByOwner(1, "WAITING", pageable);
        BookingDto bookingDto1 = bookingDtos.get(0);
        BookingDto bookingDto2 = new BookingDto(1, start.plusSeconds(1), start.plusSeconds(10), itemDto1, userDto2, Status.WAITING);
        Assertions.assertEquals(bookingDto1.getId(), bookingDto2.getId());
        Assertions.assertEquals(bookingDto1.getStart(), bookingDto2.getStart());
        Assertions.assertEquals(bookingDto1.getEnd(), bookingDto2.getEnd());
        Assertions.assertEquals(bookingDto1.getItem().getId(), bookingDto2.getItem().getId());
        Assertions.assertEquals(bookingDto1.getBooker().getId(), bookingDto2.getBooker().getId());
        Assertions.assertEquals(bookingDto1.getStatus(), bookingDto2.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getBookingByOwnerRejectedTest() throws InterruptedException {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusSeconds(1), start.plusSeconds(10));
        bookingService.add(bookingInputDto, 2);
        bookingService.setStatus(1, false, 1);
        List<BookingDto> bookingDtos = bookingService.getAllByOwner(1, "REJECTED", pageable);
        BookingDto bookingDto1 = bookingDtos.get(0);
        BookingDto bookingDto2 = new BookingDto(1, start.plusSeconds(1), start.plusSeconds(10), itemDto1, userDto2, Status.REJECTED);
        Assertions.assertEquals(bookingDto1.getId(), bookingDto2.getId());
        Assertions.assertEquals(bookingDto1.getStart(), bookingDto2.getStart());
        Assertions.assertEquals(bookingDto1.getEnd(), bookingDto2.getEnd());
        Assertions.assertEquals(bookingDto1.getItem().getId(), bookingDto2.getItem().getId());
        Assertions.assertEquals(bookingDto1.getBooker().getId(), bookingDto2.getBooker().getId());
        Assertions.assertEquals(bookingDto1.getStatus(), bookingDto2.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getBookingByOwnerDefaultTest() throws InterruptedException {
        LocalDateTime start = LocalDateTime.now();
        userService.add(userDto1);
        userService.add(userDto2);
        itemService.add(itemDto1, 1);
        BookingInputDto bookingInputDto = new BookingInputDto(1, 1, start.plusSeconds(1), start.plusSeconds(10));
        bookingService.add(bookingInputDto, 2);
        List<BookingDto> bookingDtos = bookingService.getAllByOwner(1, "ALL", pageable);
        BookingDto bookingDto1 = bookingDtos.get(0);
        BookingDto bookingDto2 = new BookingDto(1, start.plusSeconds(1), start.plusSeconds(10), itemDto1, userDto2, Status.WAITING);
        Assertions.assertEquals(bookingDto1.getId(), bookingDto2.getId());
        Assertions.assertEquals(bookingDto1.getStart(), bookingDto2.getStart());
        Assertions.assertEquals(bookingDto1.getEnd(), bookingDto2.getEnd());
        Assertions.assertEquals(bookingDto1.getItem().getId(), bookingDto2.getItem().getId());
        Assertions.assertEquals(bookingDto1.getBooker().getId(), bookingDto2.getBooker().getId());
        Assertions.assertEquals(bookingDto1.getStatus(), bookingDto2.getStatus());
    }
}
