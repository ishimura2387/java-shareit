package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.debug("Обработка запроса POST/items/" + itemId + "/comment");
        CommentDto comment = itemService.addComment(commentDto, userId, itemId);
        log.debug("Создан отзыв: {}", comment);
        return comment;
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        log.debug("Обработка запроса POST/items");
        ItemDto item = itemService.add(itemDto, userId);
        log.debug("Создана вещь: {}", item);
        return item;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        log.debug("Обработка запроса PATCH/items/" + itemId);
        ItemDto item = itemService.update(itemDto, userId, itemId);
        log.debug("Изменена вещь: {}", item);
        return item;
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithDate get(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Обработка запроса GET/items/" + itemId);
        ItemDtoWithDate item = itemService.get(itemId, userId);
        log.debug("Получена вещь: {}", item);
        return item;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                @RequestParam(required = false) @Min(1) Integer size) {
        log.debug("Обработка запроса GET/items/search");
        List<ItemDto> items = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        if (size == null) {
            items = itemService.searchSort(text, sort);
        } else {
            int page = from / size;
            Pageable pageable = PageRequest.of(page, size, sort);
            items = itemService.searchPageable(text, pageable);
        }
        log.debug("Получен список с размером: {}", items.size());
        return items;
    }

    @GetMapping
    public List<ItemDtoWithDate> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                        @RequestParam(required = false) @Min(1) Integer size) {
        log.debug("Обработка запроса GET/items");
        List<ItemDtoWithDate> items = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        if (size == null) {
            items = itemService.getAllSort(userId, sort);
        } else {
            int page = from / size;
            Pageable pageable = PageRequest.of(page, size, sort);
            items = itemService.getAllPageable(userId, pageable);
        }
        log.debug("Получен список с размером: {}", items.size());
        return items;
    }
}
