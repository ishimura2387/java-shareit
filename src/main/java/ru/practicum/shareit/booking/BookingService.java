package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingDto add(BookingInputDto bookingInputDto, long userId);

    BookingDto setStatus(long bookingId, boolean approved, long userId);

    BookingDto get(long idBooking, long idUser);

    List<BookingDto> getAllByUser(long userId, String state, Integer from, Integer size);

    List<BookingDto> getAllByOwner(long ownerId, String state, Integer from, Integer size);
}
