package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface BookingService {
    BookingDto add(BookingInputDto bookingInputDto, long userId);

    BookingDto setStatus(long bookingId, boolean approved, long userId);

    BookingDto get(long idBooking, long idUser);

    List<BookingDto> getAllByUserPageable(long userId, String state, Pageable pageable);

    List<BookingDto> getAllByUserSort(long userId, String state, Sort sort);

    List<BookingDto> getAllByOwnerSort(long ownerId, String state, Sort sort);

    List<BookingDto> getAllByOwnerPageable(long ownerId, String state, Pageable pageable);
}
