package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.CommentException;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.OwnerException;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityNotFoundException;
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
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    @Override
    public CommentDto createNewComment(CommentDto commentDto, long userId, long itemId) {
        checkItem(itemId);
        checkUser(userId);
        checkComment(userId, itemId);
        Comment comment = commentMapper.toComment(commentDto);
        comment.setItem(itemRepository.getById(itemId));
        comment.setAuthor(userRepository.getById(userId));
        comment.setCreated(LocalDateTime.now());
        log.debug("Обработка запроса POST /items/{itemId}/comment. Создан отзыв: {}", comment);
        return commentMapper.fromComment(commentRepository.save(comment));
    }

    @Override
    public ItemDto createNewItem(ItemDto itemDto, long userId) {
        checkUser(userId);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userRepository.getById(userId));
        item.setRequest(null); // видимо реалиация будет в следующих спринтах? в ТЗ ничего не сказано
        log.debug("Обработка запроса POST /items. Создана вещь: {}", item);
        return itemMapper.fromItem(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long userId, long itemId) {
        checkItem(itemId);
        checkUser(userId);
        checkOwner(userId, itemId);
        Item oldItem = itemRepository.getById(itemId);
        Item item = itemMapper.updateItem(itemDto, oldItem);
        item.setOwner(oldItem.getOwner());
        log.debug("Обработка запроса PUT /items. Вещь изменена: {}", item);
        return itemMapper.fromItem(itemRepository.save(item));
    }

    @Override
    public ItemDtoWithDate getItem(long itemId, long userId) {
        try {
            log.debug("Обработка запроса GET /items.Запрошена вещь c id: {}", itemId);
            Item item = itemRepository.getById(itemId);
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
        } catch (EntityNotFoundException e) {
            log.debug("Ошибка проверки вещи на наличие в Storage! Вещь не найдена!");
            throw new NullObjectException("Вещь не найдена!");
        }
    }

    @Override
    public List<ItemDtoWithDate> getMyItems(long id) {
        checkUser(id);
        log.debug("Обработка запроса GET /items.Запрошены вещи пользователя: {}", id);
        List<Item> items = itemRepository.getItemsByOwnerIdOrderById(id);
        List<ItemDtoWithDate> itemDtoWithDates = new ArrayList<>();
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
    public List<ItemDto> getItemsForRent(String text) {
        log.debug("Обработка запроса GET /items.Запрошены вещи c описанием: {}", text);
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.getItemsForRent(text);
        return items.stream().map(item -> itemMapper.fromItem(item)).collect(Collectors.toList());
    }

    private void checkUser(long id) {
        if (userRepository.findById(id).isEmpty()) {
            log.debug("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
            throw new NullObjectException("Пользователь не найден!");
        }
    }

    private void checkOwner(long userId, long itemId) {
        if (userId != itemRepository.getById(itemId).getOwner().getId()) {
            throw new OwnerException("Нет прав для редактирования");
        }
    }

    private void checkItem(long id) {
        if (itemRepository.findById(id).isEmpty()) {
            log.debug("Ошибка проверки вещи на наличие в Storage! Вещь не найдена!");
            throw new NullObjectException("Вещь не найдена!");
        }
    }

    private void checkComment(long userId, long itemId) {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            log.debug("Ошибка проверки на возможность оставлять отзывы");
            throw new CommentException("Чтобы оставлять отзывы нужно иметь завершенное бронирование вещи!");
        }
    }

    private ItemDtoWithDate setDate(Item item) {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStatusIsOrderByStartDesc(item.getId(), Status.APPROVED);
        ItemDtoWithDate itemDtoWithDate = itemMapper.toItemDtoWithDate(item);
        LocalDateTime localDateTime = LocalDateTime.now();
        Optional<Booking> nextBooking = bookings.stream().filter(booking -> booking.getStart().isAfter(localDateTime)).reduce((a, b) -> b);
        Optional<Booking> lastBooking = bookings.stream().filter(booking -> booking.getStart().isBefore(localDateTime)).reduce((a, b) -> a);
        System.out.println(nextBooking);
        System.out.println(lastBooking);
        if (!nextBooking.isEmpty()) {
            itemDtoWithDate.setNextBooking(bookingMapper.toBookingDtoShort(nextBooking.get()));
        }
        if (!lastBooking.isEmpty()) {
            itemDtoWithDate.setLastBooking(bookingMapper.toBookingDtoShort(lastBooking.get()));
        }
        return itemDtoWithDate;
    }
}
