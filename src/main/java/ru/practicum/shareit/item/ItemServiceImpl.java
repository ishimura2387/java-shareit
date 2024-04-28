package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки вещи на наличие в Storage! Вещь не найдена!"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(itemId, userId,
                LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new IllegalArgumentException("Чтобы оставлять отзывы нужно иметь завершенное бронирование вещи!");
        }
        Comment comment = commentMapper.toComment(commentDto, user, item);
        return commentMapper.fromComment(commentRepository.save(comment));
    }

    @Override
    public ItemDto add(ItemDto itemDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(user);
        if (itemDto.getRequestId() != 0) {
            item.setRequest(requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Ошибка проверки запроса на наличие в Storage! " +
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
    public ItemWithDateResponseDto get(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки вещи на наличие в Storage! Вещь не найдена!"));
        ItemWithDateResponseDto itemWithDateResponseDto = new ItemWithDateResponseDto();
        if (item.getOwner().getId() == userId) {
            List<Long> ids = new ArrayList<>();
            ids.add(itemId);
            List<Booking> bookings = getBooking(ids);
            itemWithDateResponseDto = itemMapper.toItemDtoWithDate(item);
            LocalDateTime localDateTime = LocalDateTime.now();
            Optional<Booking> nextBooking = bookings.stream().filter(booking ->
                    booking.getStart().isAfter(localDateTime)).reduce((a, b) -> b);
            Optional<Booking> lastBooking = bookings.stream().filter(booking ->
                    booking.getStart().isBefore(localDateTime)).reduce((a, b) -> a);
            if (!nextBooking.isEmpty()) {
                itemWithDateResponseDto.setNextBooking(bookingMapper.toBookingDtoShort(nextBooking.get()));
            }
            if (!lastBooking.isEmpty()) {
                itemWithDateResponseDto.setLastBooking(bookingMapper.toBookingDtoShort(lastBooking.get()));
            }
        } else {
            itemWithDateResponseDto = itemMapper.toItemDtoWithDate(item);
        }
        List<Long> ids = new ArrayList<>();
        ids.add(item.getId());
        List<CommentDto> comments = commentRepository.findAll(ids).stream().map(comment ->
                commentMapper.fromComment(comment)).collect(Collectors.toList());
        itemWithDateResponseDto.setComments(comments);
        return itemWithDateResponseDto;
    }

    @Override
    public List<ItemWithDateResponseDto> getAll(long id, Pageable pageable) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        List<ItemWithDateResponseDto> itemWithDateResponseDtos = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        items = itemRepository.getItemsByOwnerId(id, pageable);
        List<Long> itemIds = new ArrayList<>();
        for (Item item : items) {
            itemIds.add(item.getId());
        }
        List<Comment> allComments = commentRepository.findAll(itemIds);
        List<Booking> allBookings = getBooking(itemIds);
        HashMap<Long, List<CommentDto>> itemComment = new HashMap<>();
        HashMap<Long, List<Booking>> itemBooking = new HashMap<>();
        for (Item item : items) {
            List<CommentDto> comments = new ArrayList<>();
            List<Booking> bookings = new ArrayList<>();
            for (Comment comment : allComments) {
                if (comment.getItem().getId() == item.getId()) {
                    comments.add(commentMapper.fromComment(comment));
                }
            }
            for (Booking booking : allBookings) {
                if (booking.getItem().getId() == item.getId()) {
                    bookings.add(booking);
                }
            }
            itemComment.put(item.getId(),comments);
            itemBooking.put(item.getId(),bookings);
        }
        for (Item item : items) {
            ItemWithDateResponseDto itemWithDateResponseDto = itemMapper.toItemDtoWithDate(item);
            LocalDateTime localDateTime = LocalDateTime.now();
            Optional<Booking> nextBooking = itemBooking.get(item.getId()).stream().filter(booking ->
                    booking.getItem().getId() == item.getId()).filter(booking ->
                    booking.getStart().isAfter(localDateTime)).reduce((a, b) -> b);
            Optional<Booking> lastBooking = itemBooking.get(item.getId()).stream().filter(booking ->
                    booking.getItem().getId() == item.getId()).filter(booking ->
                    booking.getStart().isBefore(localDateTime)).reduce((a, b) -> a);
            if (!nextBooking.isEmpty()) {
                itemWithDateResponseDto.setNextBooking(bookingMapper.toBookingDtoShort(nextBooking.get()));
            }
            if (!lastBooking.isEmpty()) {
                itemWithDateResponseDto.setLastBooking(bookingMapper.toBookingDtoShort(lastBooking.get()));
            }
            itemWithDateResponseDto.setComments(itemComment.get(item.getId()));
            itemWithDateResponseDtos.add(itemWithDateResponseDto);
        }
        return itemWithDateResponseDtos;
    }

    @Override
    public List<ItemDto> search(String text, Pageable pageable) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> items = new ArrayList<>();
        items = itemRepository.getItemsForRent(text, pageable);
        return items.stream().map(item -> itemMapper.fromItem(item)).collect(Collectors.toList());
    }

    private List<Booking> getBooking(List<Long> itemIds) {
        List<Booking> bookings = bookingRepository.findAll(Status.APPROVED, itemIds,
                Sort.by(Sort.Direction.DESC, "start"));
        return bookings;
    }
}
