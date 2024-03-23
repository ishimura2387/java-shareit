package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingServiceImpl;

    @PostMapping
    public BookingDto createNewBooking(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody BookingInputDto bookingInputDto) {
        return bookingServiceImpl.addBooking(bookingInputDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setBookingStatus(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId, @RequestParam boolean approved) {
        return bookingServiceImpl.setBookingStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        return bookingServiceImpl.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(defaultValue = "ALL") String state) {
        return bookingServiceImpl.getBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(defaultValue = "ALL") String state) {
        return bookingServiceImpl.getBookingsByOwner(userId, state);
    }
}
