package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityNotFoundException;
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
        long idOwner = 0;
        try {
            booking.setItem(itemRepository.getById(booking.getItem().getId()));
            booking.setBooker(userRepository.getById(userId));
            idOwner = booking.getItem().getOwner().getId();
        } catch (EntityNotFoundException e) {
            log.debug("Ошибка проверки вещи и пользователя на наличие в Storage! Вещь не найдена!");
            throw new NullObjectException("Вещь не найдена!");
        }
        checkUser(userId);
        if (!booking.getItem().getAvailable()) {
            log.debug("Ошибка бронирования вещи! Вещь недоступна для бронирования!");
            throw new AvailableException("Вещь недоступна для бронирования!");
        }
        if (userId == idOwner) {
            log.debug("Ошибка бронирования вещи! Вещь недоступна для бронирования!");
            throw new OwnerException("Пользователь не может забронировать свою вещь!");
        }
        if (booking.getEnd() == null || booking.getStart() == null || booking.getEnd().isBefore(booking.getStart()) || booking.getStart().isBefore(LocalDateTime.now()) ||
                booking.getEnd().isBefore(LocalDateTime.now()) || booking.getEnd().equals(booking.getStart())
        ) {
            log.debug("Ошибка валидации времени бронирования!");
            throw new TimeBookingValidationException("Конец бронирования не ожет быть раньше начала бронирования!");
        }
        booking.setStatus(Status.WAITING);
        log.debug("Обработка запроса POST /items. Создано бронирование: {}", booking);
        bookingRepository.save(booking);
        return bookingMapper.fromBooking(booking);
    }

    public BookingDto setBookingStatus(long bookingId, boolean approved, long idUser) {
        try {
            Booking booking = bookingRepository.getById(bookingId);
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
        } catch (EmptyResultDataAccessException e) {
            log.debug("Ошибка проверки бронирования на наличие в Storage! Бронирование не найдено!");
            throw new NullObjectException("Бронирование не найдено!");
        }
    }

    public BookingDto getBooking(long idBooking, long idUser) {
        try {
            checkUser(idUser);
            Booking booking = bookingRepository.getById(idBooking);
            if (!(booking.getItem().getOwner().getId() == idUser || booking.getBooker().getId() == idUser)) {
                log.debug("Ошибка запроса бронирования!");
                throw new OwnerException("Запрашивать бронирование может только автор или собственник вещи");
            }
            return bookingMapper.fromBooking(booking);
        } catch (EntityNotFoundException e) {
            throw new NullObjectException("Бронирование не найдено!");
        }
    }

    public List<BookingDto> getBookingsByUser(long userId, String state) {
        checkUser(userId);
        if (!Arrays.stream(State.values()).anyMatch(e -> e.name().equals(state))) {
            throw new StateException("Unknown state: " + state);
        }
        List<Booking> bookings = new ArrayList<>();
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
        return bookings.stream().map(booking -> bookingMapper.fromBooking(booking)).collect(Collectors.toList());
    }

    public List<BookingDto> getBookingsByOwner(long ownerId, String state) {
        checkUser(ownerId);
        if (!Arrays.stream(State.values()).anyMatch(e -> e.name().equals(state))) {
            throw new StateException("Unknown state: " + state);
        }
        List<Booking> bookings = new ArrayList<>();
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
        return bookings.stream().map(booking -> bookingMapper.fromBooking(booking)).collect(Collectors.toList());

    }

    private void checkUser(long id) {
        if (userRepository.findById(id).isEmpty()) {
            log.debug("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
            throw new NullObjectException("Пользователь не найден!");
        }
    }
}
