package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingServiceImpl;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto add(@RequestHeader(USER_ID) long userId, @RequestBody BookingInputDto bookingInputDto) {
        log.debug("Обработка запроса POST/bookings");
        BookingDto bookingDto = bookingServiceImpl.add(bookingInputDto, userId);
        log.debug("Создано бронирование: {}", bookingDto);
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setStatus(@RequestHeader(USER_ID) long userId, @PathVariable long bookingId,
                                @RequestParam boolean approved) {
        log.debug("Обработка запроса PATCH/bookings/" + bookingId);
        BookingDto bookingDto = bookingServiceImpl.setStatus(bookingId, approved, userId);
        log.debug("Статус бронирования изменен: {}", bookingDto);
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader(USER_ID) long userId, @PathVariable long bookingId) {
        log.debug("Обработка запроса GET/bookings/" + bookingId);
        BookingDto bookingDto = bookingServiceImpl.get(bookingId, userId);
        log.debug("Получено бронирование: {}", bookingDto);
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> getByUser(@RequestHeader(USER_ID) long userId,
                                      @RequestParam String state,
                                      @RequestParam int from,
                                      @RequestParam int size) {
        log.debug("Обработка запроса GET/bookings");
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<BookingDto> bookingDto = new ArrayList<>();
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, sort);
        bookingDto = bookingServiceImpl.getAllByUser(userId, state, pageable);
        log.debug("Получен список с размером: {}", bookingDto.size());
        return bookingDto;
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwner(@RequestHeader(USER_ID) long userId,
                                       @RequestParam String state,
                                       @RequestParam int from,
                                       @RequestParam int size) {
        log.debug("Обработка запроса GET/bookings/owner");
        List<BookingDto> bookingDto = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, sort);
        bookingDto = bookingServiceImpl.getAllByOwner(userId, state, pageable);
        log.debug("Получен список с размером: {}", bookingDto.size());
        return bookingDto;
    }
}

