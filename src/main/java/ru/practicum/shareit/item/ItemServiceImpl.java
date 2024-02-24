package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.OwnerException;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createNewItem(ItemDto itemDto, int userId) {
        if (itemDto.getOwner() == 0) {
            itemDto.setOwner(userId);
        }
        checkUser(userId);
        Item item = itemMapper.toItem(itemDto);
        log.debug("Обработка запроса POST /items. Создана вещь: {}", item);
        return itemMapper.fromItem(itemStorage.createNewItem(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int userId, int itemId) {
        if (itemDto.getOwner() == 0) {
            itemDto.setOwner(userId);
        }
        checkUser(userId);
        checkOwner(itemDto, userId, itemId);
        Item item = itemMapper.updateItem(itemDto, itemStorage.getItem(itemId));
        log.debug("Обработка запроса PUT /items. Вещь изменена: {}", item);
        return itemMapper.fromItem(itemStorage.updateItem(item));
    }

    @Override
    public ItemDto getItem(int id) {
        checkItem(id);
        log.debug("Обработка запроса GET /items.Запрошена вещь c id: {}", id);
        return itemMapper.fromItem(itemStorage.getItem(id));
    }

    @Override
    public List<ItemDto> getMyItems(int id) {
        log.debug("Обработка запроса GET /items.Запрошены вещи пользователя: {}", id);
        checkUser(id);
        List<Item> items = itemStorage.getMyItems(id);
        List<ItemDto> itemDto = new ArrayList<>();
        for (Item item : items) {
            itemDto.add(itemMapper.fromItem(item));
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsForRent(String text) {
        log.debug("Обработка запроса GET /items.Запрошены вещи c описанием: {}", text);
        List<ItemDto> itemDto = new ArrayList<>();
        if (text.isEmpty()) {
            return itemDto;
        }
        List<Item> items = itemStorage.getItemsForRent(text);
        for (Item item : items) {
            itemDto.add(itemMapper.fromItem(item));
        }
        return itemDto;
    }

    private void checkItem(int id) {
        if (!itemStorage.findAllItemsId().contains(id)) {
            log.debug("Ошибка проверки вещи на наличие в Storage! Вещь не найдена!");
            throw new NullObjectException("Вещь не найдена!");
        }
    }

    private void checkUser(int id) {
        if (!userStorage.findAllUsersId().contains(id)) {
            log.debug("Ошибка проверки пользователя на наличие в Storage! Пользователь не найден!");
            throw new NullObjectException("Пользователь не найден!");
        }
    }

    private void checkOwner(ItemDto itemDto, int userId, int itemId) {
        if (userId != getItem(itemId).getOwner()) {
            throw new OwnerException("Нет прав для редактирования");
        }
    }
}
