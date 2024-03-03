package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NullObjectException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class MemoryItemStorage implements ItemStorage {
    private final Map<Integer, Item> items = new HashMap<>();
    private int counterItemId = 1;

    @Override
    public Item createNewItem(Item item) {
        item.setId(counterItemId);
        items.put(counterItemId, item);
        counterItemId++;
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        return items.replace(item.getId(), item);
    }

    @Override
    public Item getItem(int id) {
        return items.get(id);
    }

    @Override
    public List<Item> getMyItems(int id) {
        List<Item> myItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId() == id) {
                myItems.add(item);
            }
        }
        return myItems;
    }

    @Override
    public List<Item> getItemsForRent(String text) {
        List<Item> itemsForRent = new ArrayList<>();
        String textLower = text.toLowerCase();
        for (Item item : items.values()) {
            if (item.getAvailable() && (item.getName().toLowerCase().contains(textLower) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                itemsForRent.add(item);
            }
        }
        return itemsForRent;
    }

    @Override
    public void checkItemForService(int id) {
        checkItem(id);
    }

    private void checkItem(int id) {
        if (items.get(id) == null) {
            log.debug("Ошибка проверки вещи на наличие в Storage! Вещь не найдена!");
            throw new NullObjectException("Вещь не найдена!");
        }
    }
}
