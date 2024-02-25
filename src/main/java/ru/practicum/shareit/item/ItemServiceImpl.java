package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.OwnerException;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createNewItem(ItemDto itemDto, int userId) {
        checkUser(userId);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userStorage.getUser(userId));
        item.setRequest(null); // видимо реалиация будет в следующих спринтах? в ТЗ ничего не сказано
        log.debug("Обработка запроса POST /items. Создана вещь: {}", item);
        return itemMapper.fromItem(itemStorage.createNewItem(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int userId, int itemId) {
        itemStorage.checkItemForService(itemId);
        checkUser(userId);
        checkOwner(userId, itemId);
        Item item = itemMapper.updateItem(itemDto, itemStorage.getItem(itemId));
        item.setOwner(userStorage.getUser(userId));
        log.debug("Обработка запроса PUT /items. Вещь изменена: {}", item);
        return itemMapper.fromItem(itemStorage.updateItem(item));
    }

    @Override
    public ItemDto getItem(int id) {
        itemStorage.checkItemForService(id);
        log.debug("Обработка запроса GET /items.Запрошена вещь c id: {}", id);
        return itemMapper.fromItem(itemStorage.getItem(id));
    }

    @Override
    public List<ItemDto> getMyItems(int id) {
        checkUser(id);
        log.debug("Обработка запроса GET /items.Запрошены вещи пользователя: {}", id);
        List<Item> items = itemStorage.getMyItems(id);
        return items.stream().map(item -> itemMapper.fromItem(item)).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsForRent(String text) {
        log.debug("Обработка запроса GET /items.Запрошены вещи c описанием: {}", text);
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> items = itemStorage.getItemsForRent(text);
        return items.stream().map(item -> itemMapper.fromItem(item)).collect(Collectors.toList());
    }

    private void checkUser(int id) {
        if (userStorage.getUser(id) == null) {
            log.debug("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
            throw new NullObjectException("Пользователь не найден!");
        }
    }

    private void checkOwner(int userId, int itemId) {
        if (userId != itemStorage.getItem(itemId).getOwner().getId()) {
            throw new OwnerException("Нет прав для редактирования");
        }
    }
}
