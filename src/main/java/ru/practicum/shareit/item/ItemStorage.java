package ru.practicum.shareit.item;

import java.util.List;

public interface ItemStorage {
    Item createNewItem(Item item);

    Item updateItem(Item item);

    Item getItem(int id);

    List<Item> getMyItems(int id);

    List<Item> getItemsForRent(String text);

    List<Integer> findAllItemsId();
}
