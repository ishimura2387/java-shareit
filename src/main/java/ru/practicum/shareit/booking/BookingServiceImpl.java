package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AvailableException;
import ru.practicum.shareit.exception.NoOneApprovedException;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.OwnerException;
import ru.practicum.shareit.exception.SetStatusBookingException;
import ru.practicum.shareit.exception.StateException;
import ru.practicum.shareit.exception.TimeBookingValidationException;
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
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;

    public BookingDto add(BookingInputDto bookingInputDto, long userId) {
        Booking booking = bookingMapper.fromBookingDtoInput(bookingInputDto);
        Item item = itemRepository.findById(booking.getItem().getId()).
                orElseThrow(() -> new NullObjectException("Ошибка проверки вещи на наличие в Storage! Вещь не найдена!"));
        booking.setItem(item);
        User booker = userRepository.findById(userId).
                orElseThrow(() -> new NullObjectException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        booking.setBooker(booker);
        if (!item.getAvailable()) {
            throw new AvailableException("Вещь недоступна для бронирования!");
        }
        if (userId == item.getOwner().getId()) {
            throw new OwnerException("Пользователь не может забронировать свою вещь!");
        }
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getStart().isBefore(LocalDateTime.now()) ||
                booking.getEnd().isBefore(LocalDateTime.now()) || booking.getEnd().equals(booking.getStart())) {
            throw new TimeBookingValidationException("Время бронирования не корректно!");
        }
        List<Booking> bookings = bookingRepository.findAllByItemIdAndEndBetweenOrStartBetween(booking.getItem().getId(),
                booking.getStart(), booking.getEnd());
        if (bookings.size() > 0) {
            throw new TimeBookingValidationException("На данное время уже есть бронирование!");
        }
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);
        return bookingMapper.fromBooking(booking);
    }

    public BookingDto setStatus(long bookingId, boolean approved, long idUser) {
        Booking booking = bookingRepository.findById(bookingId).
                orElseThrow(() -> new NullObjectException("Ошибка проверки бронирования на наличие в Storage! " +
                        "Бронирование не найдено!"));
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

    public BookingDto get(long idBooking, long idUser) {
        Booking booking = bookingRepository.findById(idBooking).filter(b -> b.getBooker().getId() == idUser
                        || b.getItem().getOwner().getId() == idUser).
                orElseThrow(() -> new NullObjectException("Ошибка " + "проверки бронирования на наличие в Storage! " +
                        "Бронирование не найдено!"));
        if (!(booking.getItem().getOwner().getId() == idUser || booking.getBooker().getId() == idUser)) {
            throw new OwnerException("Запрашивать бронирование может только автор или собственник вещи");
        }
        return bookingMapper.fromBooking(booking);
    }

    public List<BookingDto> getAllByUserSort(long userId, String state, Sort sort) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new NullObjectException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        if (!Arrays.stream(State.values()).anyMatch(e -> e.name().equals(state))) {
            throw new StateException("Unknown state: " + state);
        }
        List<Booking> bookings = new ArrayList<>();
        switch (state.toUpperCase()) {
            case "CURRENT":
                LocalDateTime time = LocalDateTime.now();
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId, time, time, sort);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(userId, LocalDateTime.now(), sort);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now(), sort);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndStatusIs(userId, Status.WAITING, sort);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatusIs(userId, Status.REJECTED, sort);
                break;
            default:
                bookings = bookingRepository.findAllByBookerId(userId, sort);
                break;
        }
        return bookings.stream().map(booking -> bookingMapper.fromBooking(booking)).collect(Collectors.toList());
    }

    public List<BookingDto> getAllByUserPageable(long userId, String state, Pageable pageable) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new NullObjectException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        if (!Arrays.stream(State.values()).anyMatch(e -> e.name().equals(state))) {
            throw new StateException("Unknown state: " + state);
        }
        List<Booking> bookings = new ArrayList<>();
        switch (state.toUpperCase()) {
            case "CURRENT":
                LocalDateTime time = LocalDateTime.now();
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId, time, time, pageable);
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
        return bookings.stream().map(booking -> bookingMapper.fromBooking(booking)).collect(Collectors.toList());
    }

    public List<BookingDto> getAllByOwnerSort(long ownerId, String state, Sort sort) {
        User owner = userRepository.findById(ownerId).
                orElseThrow(() -> new NullObjectException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        if (!Arrays.stream(State.values()).anyMatch(e -> e.name().equals(state))) {
            throw new StateException("Unknown state: " + state);
        }
        List<Booking> bookings = new ArrayList<>();
        switch (state.toUpperCase()) {
            case "CURRENT":
                LocalDateTime time = LocalDateTime.now();
                bookings = bookingRepository.findAllByItem_OwnerIdAndStartBeforeAndEndAfter(ownerId, time, time, sort);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItem_OwnerIdAndAndEndBefore(ownerId, LocalDateTime.now(), sort);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItem_OwnerIdAndStartAfter(ownerId, LocalDateTime.now(), sort);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByItem_OwnerIdAndStatusIs(ownerId, Status.WAITING, sort);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByItem_OwnerIdAndStatusIs(ownerId, Status.REJECTED, sort);
                break;
            default:
                bookings = bookingRepository.findAllByItem_OwnerId(ownerId, sort);
                break;
        }
        return bookings.stream().map(booking -> bookingMapper.fromBooking(booking)).collect(Collectors.toList());
    }

    public List<BookingDto> getAllByOwnerPageable(long ownerId, String state, Pageable pageable) {
        User owner = userRepository.findById(ownerId).
                orElseThrow(() -> new NullObjectException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        if (!Arrays.stream(State.values()).anyMatch(e -> e.name().equals(state))) {
            throw new StateException("Unknown state: " + state);
        }
        List<Booking> bookings = new ArrayList<>();
        switch (state.toUpperCase()) {
            case "CURRENT":
                LocalDateTime time = LocalDateTime.now();
                bookings = bookingRepository.findAllByItem_OwnerIdAndStartBeforeAndEndAfter(ownerId, time, time, pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItem_OwnerIdAndAndEndBefore(ownerId, LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItem_OwnerIdAndStartAfter(ownerId, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByItem_OwnerIdAndStatusIs(ownerId, Status.WAITING, pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByItem_OwnerIdAndStatusIs(ownerId, Status.REJECTED, pageable);
                break;
            default:
                bookings = bookingRepository.findAllByItem_OwnerId(ownerId, pageable);
                break;
        }
        return bookings.stream().map(booking -> bookingMapper.fromBooking(booking)).collect(Collectors.toList());
    }
}
