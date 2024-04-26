package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.CommentException;
import ru.practicum.shareit.exception.NullObjectException;
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
    public CommentDto addComment(CommentDto commentDto, long userId, long itemId) {
        Item item = itemRepository.findById(itemId).
                orElseThrow(() -> new NullObjectException("Ошибка проверки вещи на наличие в Storage! Вещь не найдена!"));
        User user = userRepository.findById(userId).
                orElseThrow(() -> new NullObjectException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(itemId, userId,
                LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new CommentException("Чтобы оставлять отзывы нужно иметь завершенное бронирование вещи!");
        }
        Comment comment = commentMapper.toComment(commentDto, user, item);
        return commentMapper.fromComment(commentRepository.save(comment));
    }

    @Override
    public ItemDto add(ItemDto itemDto, long userId) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new NullObjectException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(user);
        if (itemDto.getRequestId() != 0) {
            item.setRequest(requestRepository.findById(itemDto.getRequestId()).
                    orElseThrow(() -> new NullObjectException("Ошибка проверки запроса на наличие в Storage! " +
                            "Запрос не найден!")));
        } else {
            item.setRequest(null);
        }
        return itemMapper.fromItem(itemRepository.save(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, long userId, long itemId) {
        Item oldItem = itemRepository.findByIdAndOwnerId(itemId, userId);
        Item item = itemMapper.updateItem(itemDto, oldItem);
        item.setOwner(oldItem.getOwner());
        return itemMapper.fromItem(itemRepository.save(item));
    }

    @Override
    public ItemDtoWithDate get(long itemId, long userId) {
        Item item = itemRepository.findById(itemId).
                orElseThrow(() -> new NullObjectException("Ошибка проверки вещи на наличие в Storage! Вещь не найдена!"));
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
    public List<ItemDtoWithDate> getAllSort(long id, Sort sort) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new NullObjectException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        List<ItemDtoWithDate> itemDtoWithDates = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        items = itemRepository.getItemsByOwnerId(id, Sort.by(Sort.Direction.ASC, "id"));
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
    public List<ItemDtoWithDate> getAllPageable(long id, Pageable pageable) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new NullObjectException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        List<ItemDtoWithDate> itemDtoWithDates = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        items = itemRepository.getItemsByOwnerId(id, pageable);
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
    public List<ItemDto> searchSort(String text, Sort sort) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> items = new ArrayList<>();
        items = itemRepository.getItemsForRent(text);
        return items.stream().map(item -> itemMapper.fromItem(item)).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchPageable(String text, Pageable pageable) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> items = new ArrayList<>();
        items = itemRepository.getItemsForRent(text, pageable);
        return items.stream().map(item -> itemMapper.fromItem(item)).collect(Collectors.toList());
    }

    private ItemDtoWithDate setDate(Item item) {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStatusIs(item.getId(), Status.APPROVED,
                Sort.by(Sort.Direction.DESC, "start"));
        ItemDtoWithDate itemDtoWithDate = itemMapper.toItemDtoWithDate(item);
        LocalDateTime localDateTime = LocalDateTime.now();
        Optional<Booking> nextBooking = bookings.stream().filter(booking ->
                booking.getStart().isAfter(localDateTime)).reduce((a, b) -> b);
        Optional<Booking> lastBooking = bookings.stream().filter(booking ->
                booking.getStart().isBefore(localDateTime)).reduce((a, b) -> a);
        if (!nextBooking.isEmpty()) {
            itemDtoWithDate.setNextBooking(bookingMapper.toBookingDtoShort(nextBooking.get()));
        }
        if (!lastBooking.isEmpty()) {
            itemDtoWithDate.setLastBooking(bookingMapper.toBookingDtoShort(lastBooking.get()));
        }
        return itemDtoWithDate;
    }
}
