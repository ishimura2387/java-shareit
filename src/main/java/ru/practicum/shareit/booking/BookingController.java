package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingServiceImpl;

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody BookingInputDto bookingInputDto) {
        return bookingServiceImpl.add(bookingInputDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setStatus(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId, @RequestParam boolean approved) {
        return bookingServiceImpl.setStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        return bookingServiceImpl.get(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestParam(defaultValue = "ALL") String state,
                                      @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                      @RequestParam(required = false) Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        if (size == null) {
            return bookingServiceImpl.getAllByUserSort(userId, state, sort);
        } else {
            int page = from / size;
            Pageable pageable = PageRequest.of(page, size, sort);
            return bookingServiceImpl.getAllByUserPageable(userId, state, pageable);
        }
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestParam(defaultValue = "ALL") String state,
                                       @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                       @RequestParam(required = false) Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        if (size == null) {
            return bookingServiceImpl.getAllByOwnerSort(userId, state, sort);
        } else {
            int page = from / size;
            Pageable pageable = PageRequest.of(page, size, sort);
            return bookingServiceImpl.getAllByOwnerPageable(userId, state, pageable);
        }
    }
}
