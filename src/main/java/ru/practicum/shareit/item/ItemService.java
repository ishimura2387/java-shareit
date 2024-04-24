package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto itemDto, long userId);

    ItemDto update(ItemDto itemDto, long userId, long itemId);

    ItemDtoWithDate get(long itemId, long userId);

    List<ItemDtoWithDate> getAll(long id, Integer from, Integer size);

    List<ItemDto> search(String text, Integer from, Integer size);

    CommentDto addComment(CommentDto commentDto, long userId, long itemId);
}
