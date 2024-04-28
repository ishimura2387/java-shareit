package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto itemDto, long userId);

    ItemDto update(ItemDto itemDto, long userId, long itemId);

    ItemWithDateResponseDto get(long itemId, long userId);

    List<ItemWithDateResponseDto> getAll(long id, Pageable pageable);

    List<ItemDto> search(String text, Pageable pageable);

    CommentDto addComment(CommentDto commentDto, long userId, long itemId);
}
