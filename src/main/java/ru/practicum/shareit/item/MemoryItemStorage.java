package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
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

    public List<Item> getMyItems(int id) {
        List<Item> myItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() == id) {
                myItems.add(item);
            }
        }
        return myItems;
    }

    public List<Item> getItemsForRent(String text) {
        List<Item> itemsForRent = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getAvailable() && (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                itemsForRent.add(item);
            }
        }
        return itemsForRent;
    }

    public List<Integer> findAllItemsId() {
        return new ArrayList<>(items.keySet());
    }
}
