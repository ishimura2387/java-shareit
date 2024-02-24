package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto createNewItem(ItemDto itemDto, int userId);

    ItemDto updateItem(ItemDto itemDto, int userId, int itemId);

    ItemDto getItem(int id);

    List<ItemDto> getMyItems(int id);

    List<ItemDto> getItemsForRent(String text);

}
