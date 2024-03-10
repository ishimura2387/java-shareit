package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto createNewItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long userId, long itemId);

    ItemDtoWithDate getItem(long itemId, long userId);

    List<ItemDtoWithDate> getMyItems(long id);

    List<ItemDto> getItemsForRent(String text);

    CommentDto createNewComment(CommentDto commentDto, long userId, long itemId);
}
