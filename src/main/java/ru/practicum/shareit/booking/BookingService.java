package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingInputDto bookingInputDto, long userId);

    BookingDto setBookingStatus(long bookingId, boolean approved, long userId);

    BookingDto getBooking(long idBooking, long idUser);

    List<BookingDto> getBookingsByUser(long userId, String state);

    List<BookingDto> getBookingsByOwner(long ownerId, String state);
}
