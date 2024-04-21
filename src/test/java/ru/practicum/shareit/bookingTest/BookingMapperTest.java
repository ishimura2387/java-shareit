package ru.practicum.shareit.bookingTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingDtoShort;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.item.Item;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingMapperTest {
    private final BookingMapper bookingMapper;
    private UserDto userDto = new UserDto(1, "user1", "user1@mail.ru");
    private User user = new User(2, "user2", "user2@mail.ru");
    private ItemDto itemDto = new ItemDto(1, "item 1", "description item 1",
            true, null, 0);
    private Item item = new Item(2, "item 2", "description item 2",
            false, null, null);

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void toBookingTest() {
        LocalDateTime start = LocalDateTime.now();
        BookingDto bookingDto = new BookingDto(1, start.plusSeconds(1), start.plusSeconds(2),  itemDto, userDto, Status.WAITING);
        Booking bookingNull = bookingMapper.toBooking(null);
        Assertions.assertNull(bookingNull);
        Booking booking = bookingMapper.toBooking(bookingDto);
        Assertions.assertEquals(booking.getClass(), Booking.class);
        Assertions.assertEquals(bookingDto.getId(), booking.getId());
        Assertions.assertEquals(bookingDto.getStart(), booking.getStart());
        Assertions.assertEquals(bookingDto.getEnd(), booking.getEnd());
        Assertions.assertEquals(bookingDto.getItem().getId(), booking.getItem().getId());
        Assertions.assertEquals(bookingDto.getBooker().getId(), booking.getBooker().getId());
        Assertions.assertEquals(bookingDto.getStatus(), booking.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void fromBookingTest() {
        LocalDateTime start = LocalDateTime.now();
        Booking booking = new Booking(1, start.plusSeconds(1), start.plusSeconds(2),  item, user, Status.WAITING);
        BookingDto bookingNull = bookingMapper.fromBooking(null);
        Assertions.assertNull(bookingNull);
        BookingDto bookingDto = bookingMapper.fromBooking(booking);
        Assertions.assertEquals(bookingDto.getClass(), BookingDto.class);
        Assertions.assertEquals(bookingDto.getId(), booking.getId());
        Assertions.assertEquals(bookingDto.getStart(), booking.getStart());
        Assertions.assertEquals(bookingDto.getEnd(), booking.getEnd());
        Assertions.assertEquals(bookingDto.getItem().getId(), booking.getItem().getId());
        Assertions.assertEquals(bookingDto.getBooker().getId(), booking.getBooker().getId());
        Assertions.assertEquals(bookingDto.getStatus(), booking.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void bookingUpdateNullTest() {
        LocalDateTime start = LocalDateTime.now();
        BookingDto bookingDto = new BookingDto(1, start.plusSeconds(1), start.plusSeconds(2),  itemDto, userDto, Status.WAITING);
        Booking booking = new Booking(2, start.plusSeconds(3), start.plusSeconds(4),  item, user, Status.WAITING);
        Booking booking2 = bookingMapper.updateBooking(null, booking);
        Assertions.assertEquals(booking2.getId(), booking.getId());
        Assertions.assertEquals(booking2.getStart(), booking.getStart());
        Assertions.assertEquals(booking2.getEnd(), booking.getEnd());
        Assertions.assertEquals(booking2.getItem().getId(), booking.getItem().getId());
        Assertions.assertEquals(booking2.getBooker().getId(), booking.getBooker().getId());
        Assertions.assertEquals(booking2.getStatus(), booking.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void bookingUpdateTest() {
        LocalDateTime start = LocalDateTime.now();
        BookingDto bookingDto = new BookingDto(1, start.plusSeconds(1), start.plusSeconds(2),  itemDto, userDto, Status.REJECTED);
        Booking booking = new Booking(2, start.plusSeconds(3), start.plusSeconds(4),  item, user, Status.WAITING);
        Booking booking2 = bookingMapper.updateBooking(bookingDto, booking);
        Assertions.assertEquals(booking2.getId(), bookingDto.getId());
        Assertions.assertEquals(booking2.getStart(), bookingDto.getStart());
        Assertions.assertEquals(booking2.getEnd(), bookingDto.getEnd());
        Assertions.assertEquals(booking2.getItem().getId(), bookingDto.getItem().getId());
        Assertions.assertEquals(booking2.getBooker().getId(), bookingDto.getBooker().getId());
        Assertions.assertEquals(booking2.getStatus(), bookingDto.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void toBookingDtoShortTest() {
        LocalDateTime start = LocalDateTime.now();
        Booking booking = new Booking(1, start.plusSeconds(1), start.plusSeconds(2),  item, user, Status.WAITING);
        BookingDtoShort bookingNull = bookingMapper.toBookingDtoShort(null);
        Assertions.assertNull(bookingNull);
        BookingDtoShort bookingDtoShort = bookingMapper.toBookingDtoShort(booking);
        Assertions.assertEquals(bookingDtoShort.getClass(), BookingDtoShort.class);
        Assertions.assertEquals(bookingDtoShort.getId(), booking.getId());
        Assertions.assertEquals(bookingDtoShort.getStart(), booking.getStart());
        Assertions.assertEquals(bookingDtoShort.getEnd(), booking.getEnd());
        Assertions.assertEquals(bookingDtoShort.getBookerId(), booking.getBooker().getId());
        Assertions.assertEquals(bookingDtoShort.getStatus(), booking.getStatus());
    }
}
