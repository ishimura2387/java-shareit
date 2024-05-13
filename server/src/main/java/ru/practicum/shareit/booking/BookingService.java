package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {
    BookingDto add(BookingInputDto bookingInputDto, long userId);

    BookingDto setStatus(long bookingId, boolean approved, long userId);

    BookingDto get(long idBooking, long idUser);

    List<BookingDto> getAllByUser(long userId, String state, Pageable pageable);

    List<BookingDto> getAllByOwner(long ownerId, String state, Pageable pageable);
}
