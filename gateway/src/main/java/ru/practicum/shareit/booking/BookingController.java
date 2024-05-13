package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;
	private static final String USER_ID = "X-Sharer-User-Id";

	@PostMapping
	public ResponseEntity<Object> add(@RequestHeader(USER_ID) long userId,
									  @RequestBody @Valid BookingInputDto bookingInputDto) {
		log.info("Обработка запроса POST/bookings");
		if (bookingInputDto.getEnd().isBefore(bookingInputDto.getStart()) ||
				bookingInputDto.getStart().isBefore(LocalDateTime.now()) ||
				bookingInputDto.getEnd().isBefore(LocalDateTime.now()) ||
				bookingInputDto.getEnd().equals(bookingInputDto.getStart())) {
			throw new IllegalArgumentException("Время бронирования не корректно!");
		}
		ResponseEntity<Object> booking = bookingClient.addBooking(userId, bookingInputDto);
		log.info("Создано бронирование: {}, userId={}", booking.getBody(), userId);
		return booking;
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> setStatus(@RequestHeader(USER_ID) long userId,
											@PathVariable long bookingId,
											@RequestParam boolean approved) {
		log.debug("Обработка запроса PATCH/bookings/" + bookingId);
		ResponseEntity<Object> booking = bookingClient.setStatus(bookingId, approved, userId);
		log.debug("Статус бронирования изменен: {}", booking.getBody());
		return booking;
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> get(@RequestHeader(USER_ID) long userId,
									  @PathVariable long bookingId) {
		log.debug("Обработка запроса GET/bookings/" + bookingId);
		ResponseEntity<Object> booking = bookingClient.get(userId, bookingId);
		log.debug("Получено бронирование: {}", booking.getBody());
		return booking;
	}

	@GetMapping
	public ResponseEntity<Object> getByUser(@RequestHeader(USER_ID) long userId,
											@RequestParam(defaultValue = "ALL") String state,
											@RequestParam(defaultValue = "0") @Min(0) int from,
											@RequestParam(defaultValue = "10") @Min(1) int size) {
		log.debug("Обработка запроса GET/bookings");
		BookingState bookingState = BookingState.from(state)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
		ResponseEntity<Object> booking = bookingClient.getByUser(userId, state, from, size);
		log.debug("Получен список: {}", booking.getBody());
		return booking;
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getByOwner(@RequestHeader(USER_ID) long userId,
											 @RequestParam(defaultValue = "ALL") String state,
											 @RequestParam(defaultValue = "0") @Min(0) int from,
											 @RequestParam(defaultValue = "10") @Min(1) int size) {
		log.debug("Обработка запроса GET/bookings/owner");
		BookingState bookingState = BookingState.from(state)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
		ResponseEntity<Object> booking = bookingClient.getByOwner(userId, state, from, size);
		log.debug("Получен список: {}", booking.getBody());
		return booking;
	}

}
