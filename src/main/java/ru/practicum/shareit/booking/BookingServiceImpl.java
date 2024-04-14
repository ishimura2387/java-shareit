package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;

    public BookingDto addBooking(BookingInputDto bookingInputDto, long userId) {
        Booking booking = bookingMapper.fromBookingDtoInput(bookingInputDto);
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new NullObjectException("Ошибка проверки " +
                "вещи на наличие в Storage! Вещь не найдена!"));
        booking.setItem(item);
        User booker = userRepository.findById(userId).orElseThrow(() -> new NullObjectException("Ошибка проверки " +
                "пользователя на наличие в Storage! Пользователь не найден!"));
        booking.setBooker(booker);
        if (!item.getAvailable()) {
            log.debug("Ошибка бронирования вещи! Вещь недоступна для бронирования!");
            throw new AvailableException("Вещь недоступна для бронирования!");
        }
        if (userId == item.getOwner().getId()) {
            log.debug("Ошибка бронирования вещи! Вещь недоступна для бронирования!");
            throw new OwnerException("Пользователь не может забронировать свою вещь!");
        }
        if (booking.getEnd().isBefore(booking.getStart()) ||
                booking.getStart().isBefore(LocalDateTime.now()) || booking.getEnd().isBefore(LocalDateTime.now()) ||
                booking.getEnd().equals(booking.getStart())) {
            log.debug("Ошибка валидации времени бронирования!");
            throw new TimeBookingValidationException("Время бронирования не корректно!");
        }
        List<Booking> bookings = bookingRepository.findAllByItemIdAndEndBetweenOrStartBetween(booking.getItem().getId(),
                booking.getStart(), booking.getEnd());
        if (bookings.size() > 0) {
            log.debug("Ошибка валидации времени бронирования!");
            throw new TimeBookingValidationException("На данное время уже есть бронирование!");
        }
        booking.setStatus(Status.WAITING);
        log.debug("Обработка запроса POST /items. Создано бронирование: {}", booking);
        bookingRepository.save(booking);
        return bookingMapper.fromBooking(booking);
    }

    public BookingDto setBookingStatus(long bookingId, boolean approved, long idUser) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NullObjectException("Ошибка " +
                "проверки бронирования на наличие в Storage! Бронирование не найдено!"));
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new NoOneApprovedException("Статус уже подтвержден!");
        }
        if (booking.getItem().getOwner().getId() != idUser) {
            throw new SetStatusBookingException("Подтверждение бронирования доступно только владельцу вещи!");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return bookingMapper.fromBooking(booking);
    }

    public BookingDto getBooking(long idBooking, long idUser) {
        Booking booking = bookingRepository.findById(idBooking).filter(b -> b.getBooker().getId() == idUser ||
                b.getItem().getOwner().getId() == idUser).orElseThrow(() -> new NullObjectException("Ошибка " +
                "проверки бронирования на наличие в Storage! Бронирование не найдено!"));
        if (!(booking.getItem().getOwner().getId() == idUser || booking.getBooker().getId() == idUser)) {
            log.debug("Ошибка запроса бронирования!");
            throw new OwnerException("Запрашивать бронирование может только автор или собственник вещи");
        }
        return bookingMapper.fromBooking(booking);
    }

    public List<BookingDto> getBookingsByUser(long userId, String state, Integer from, Integer size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NullObjectException("Ошибка проверки " +
                "пользователя на наличие в Storage! Пользователь не найден!"));
        if (!Arrays.stream(State.values()).anyMatch(e -> e.name().equals(state))) {
            throw new StateException("Unknown state: " + state);
        }
        List<Booking> bookings = new ArrayList<>();
        if (size == null) {
            switch (state.toUpperCase()) {
                case "CURRENT":
                    bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now());
                    break;
                case "PAST":
                    bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                    break;
                case "FUTURE":
                    bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                    break;
                case "WAITING":
                    bookings = bookingRepository.findAllByBookerIdAndStatusIsOrderByStartDesc(userId, Status.WAITING);
                    break;
                case "REJECTED":
                    bookings = bookingRepository.findAllByBookerIdAndStatusIsOrderByStartDesc(userId, Status.REJECTED);
                    break;
                default:
                    bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                    break;
            }
        } else {
            if (size < 0 || size == 0 || from < 0) {
                throw new PaginationException("Ошибка пагинации!");
            }
            int page;
            if (from == size) {
                page = from / size - 1;
            } else {
                page = from / size;
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "start"));
            switch (state.toUpperCase()) {
                case "CURRENT":
                    bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterWithPagination(userId, LocalDateTime.now(), pageable);
                    break;
                case "PAST":
                    bookings = bookingRepository.findAllByBookerIdAndEndBefore(userId, LocalDateTime.now(), pageable);
                    break;
                case "FUTURE":
                    bookings = bookingRepository.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now(), pageable);
                    break;
                case "WAITING":
                    bookings = bookingRepository.findAllByBookerIdAndStatusIs(userId, Status.WAITING, pageable);
                    break;
                case "REJECTED":
                    bookings = bookingRepository.findAllByBookerIdAndStatusIs(userId, Status.REJECTED, pageable);
                    break;
                default:
                    bookings = bookingRepository.findAllByBookerId(userId, pageable);
                    break;
            }
        }
        return bookings.stream().map(booking -> bookingMapper.fromBooking(booking)).collect(Collectors.toList());
    }

    public List<BookingDto> getBookingsByOwner(long ownerId, String state, Integer from, Integer size) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NullObjectException("Ошибка проверки " +
                "пользователя на наличие в Storage! Пользователь не найден!"));
        if (!Arrays.stream(State.values()).anyMatch(e -> e.name().equals(state))) {
            throw new StateException("Unknown state: " + state);
        }
        List<Booking> bookings = new ArrayList<>();
        if (size == null) {
            switch (state.toUpperCase()) {
                case "CURRENT":
                    bookings = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, LocalDateTime.now());
                    break;
                case "PAST":
                    bookings = bookingRepository.findAllByOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
                    break;
                case "FUTURE":
                    bookings = bookingRepository.findAllByOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now());
                    break;
                case "WAITING":
                    bookings = bookingRepository.findAllByOwnerIdAndStatusIsOrderByStartDesc(ownerId, Status.WAITING);
                    break;
                case "REJECTED":
                    bookings = bookingRepository.findAllByOwnerIdAndStatusIsOrderByStartDesc(ownerId, Status.REJECTED);
                    break;
                default:
                    bookings = bookingRepository.findAllByOwnerIdOrderByStartDesc(ownerId);
                    break;
            }
        } else {
            if (size < 0 || size == 0 || from < 0) {
                throw new PaginationException("Ошибка пагинации!");
            }
            int page;
            if (from == size) {
                page = from / size - 1;
            } else {
                page = from / size;
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "start"));
            switch (state.toUpperCase()) {
                case "CURRENT":
                    bookings = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfterWithPagination(ownerId, LocalDateTime.now(), pageable);
                    break;
                case "PAST":
                    bookings = bookingRepository.findAllByOwnerIdAndEndBefore(ownerId, LocalDateTime.now(), pageable);
                    break;
                case "FUTURE":
                    bookings = bookingRepository.findAllByOwnerIdAndStartAfter(ownerId, LocalDateTime.now(), pageable);
                    break;
                case "WAITING":
                    bookings = bookingRepository.findAllByOwnerIdAndStatusIs(ownerId, Status.WAITING, pageable);
                    break;
                case "REJECTED":
                    bookings = bookingRepository.findAllByOwnerIdAndStatusIs(ownerId, Status.REJECTED, pageable);
                    break;
                default:
                    bookings = bookingRepository.findAllByOwnerId(ownerId, pageable);
                    break;
            }
        }
        return bookings.stream().map(booking -> bookingMapper.fromBooking(booking)).collect(Collectors.toList());
    }
}
