package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.CommentException;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;


    @Override
    public CommentDto createNewComment(CommentDto commentDto, long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NullObjectException("Ошибка проверки " +
                "вещи на наличие в Storage! Вещь не найдена!"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NullObjectException("Ошибка проверки " +
                "пользователя на наличие в Storage! Пользователь не найден!"));
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            log.debug("Ошибка проверки на возможность оставлять отзывы");
            throw new CommentException("Чтобы оставлять отзывы нужно иметь завершенное бронирование вещи!");
        }
        Comment comment = commentMapper.toComment(commentDto, user, item);
        log.debug("Обработка запроса POST /items/{itemId}/comment. Создан отзыв: {}", comment);
        return commentMapper.fromComment(commentRepository.save(comment));
    }

    @Override
    public ItemDto createNewItem(ItemDto itemDto, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NullObjectException("Ошибка проверки " +
                "пользователя на наличие в Storage! Пользователь не найден!"));
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(user);
        if (itemDto.getRequestId() != 0) {
            item.setRequest(requestRepository.findById(itemDto.getRequestId()).orElseThrow(() -> new NullObjectException("Ошибка проверки " +
                    "запроса на наличие в Storage! Запрос не найден!")));
        } else {
            item.setRequest(null);
        }
        log.debug("Обработка запроса POST /items. Создана вещь: {}", item);
        return itemMapper.fromItem(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long userId, long itemId) {
        Item oldItem = itemRepository.findByIdAndOwnerId(itemId, userId);
        Item item = itemMapper.updateItem(itemDto, oldItem);
        item.setOwner(oldItem.getOwner());
        log.debug("Обработка запроса PUT /items. Вещь изменена: {}", item);
        return itemMapper.fromItem(itemRepository.save(item));
    }

    @Override
    public ItemDtoWithDate getItem(long itemId, long userId) {
        log.debug("Обработка запроса GET /items.Запрошена вещь c id: {}", itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NullObjectException("Ошибка проверки " +
                "вещи на наличие в Storage! Вещь не найдена!"));
        ItemDtoWithDate itemDtoWithDate = new ItemDtoWithDate();
        if (item.getOwner().getId() == userId) {
            itemDtoWithDate = setDate(item);
        } else {
            itemDtoWithDate = itemMapper.toItemDtoWithDate(item);
        }
        List<CommentDto> comments = commentRepository.findAllByItemId(item.getId()).stream().map(comment ->
                commentMapper.fromComment(comment)).collect(Collectors.toList());
        itemDtoWithDate.setComments(comments);
        return itemDtoWithDate;
    }

    @Override
    public List<ItemDtoWithDate> getMyItems(long id, Integer from, Integer size) {
        User user = userRepository.findById(id).orElseThrow(() -> new NullObjectException("Ошибка проверки " +
                "пользователя на наличие в Storage! Пользователь не найден!"));
        log.debug("Обработка запроса GET /items.Запрошены вещи пользователя: {}", id);
        List<ItemDtoWithDate> itemDtoWithDates = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        if (size == null) {
            items = itemRepository.getItemsByOwnerId(id, Sort.by(Sort.Direction.ASC, "id"));
        } else {
            if (size < 0 || size == 0 || from < 0) {
                throw new PaginationException("Ошибка пагинации!");
            }
            int page;
            if (from.longValue() == size.longValue()) {
                page = from / size - 1;
            } else {
                page = from / size;
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            items = itemRepository.getItemsByOwnerId(id, pageable);
        }
        for (Item item : items) {
            ItemDtoWithDate itemDtoWithDate = setDate(item);
            List<CommentDto> comments = commentRepository.findAllByItemId(item.getId()).stream().map(comment ->
                    commentMapper.fromComment(comment)).collect(Collectors.toList());
            itemDtoWithDate.setComments(comments);
            itemDtoWithDates.add(itemDtoWithDate);
        }
        return itemDtoWithDates;
    }

    @Override
    public List<ItemDto> getItemsForRent(String text, Integer from, Integer size) {
        log.debug("Обработка запроса GET /items.Запрошены вещи c описанием: {}", text);
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> items = new ArrayList<>();
        if (size == null) {
            items = itemRepository.getItemsForRent(text);
        } else {
            if (size < 0 || size == 0 || from < 0) {
                throw new PaginationException("Ошибка пагинации!");
            }
            int page;
            if (from.longValue() == size.longValue()) {
                page = from / size - 1;
            } else {
                page = from / size;
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            items = itemRepository.getItemsForRentWithPagination(text, pageable);
        }
        return items.stream().map(item -> itemMapper.fromItem(item)).collect(Collectors.toList());
    }

    private ItemDtoWithDate setDate(Item item) {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStatusIs(item.getId(), Status.APPROVED,
                Sort.by(Sort.Direction.DESC, "start"));
        ItemDtoWithDate itemDtoWithDate = itemMapper.toItemDtoWithDate(item);
        LocalDateTime localDateTime = LocalDateTime.now();
        Optional<Booking> nextBooking = bookings.stream().filter(booking -> booking.getStart().isAfter(localDateTime)).reduce((a, b) -> b);
        Optional<Booking> lastBooking = bookings.stream().filter(booking -> booking.getStart().isBefore(localDateTime)).reduce((a, b) -> a);
        if (!nextBooking.isEmpty()) {
            itemDtoWithDate.setNextBooking(bookingMapper.toBookingDtoShort(nextBooking.get()));
        }
        if (!lastBooking.isEmpty()) {
            itemDtoWithDate.setLastBooking(bookingMapper.toBookingDtoShort(lastBooking.get()));
        }
        return itemDtoWithDate;
    }
}
