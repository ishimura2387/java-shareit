package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID) long userId, @PathVariable long itemId,
                                             @RequestBody @Valid CommentDto commentDto) {
        log.debug("Обработка запроса POST/items/" + itemId + "/comment");
        ResponseEntity<Object> comment = itemClient.addComment(userId, itemId, commentDto);
        log.info("Создан отзыв: {}, userId={}", comment.getBody(), userId);
        return comment;
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(USER_ID) long userId, @RequestBody @Valid ItemDto itemDto) {
        log.debug("Обработка запроса POST/items");
        ResponseEntity<Object> item = itemClient.addItem(userId, itemDto);
        log.info("Создан итем: {}, userId={}", item.getBody(), userId);
        return item;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable long itemId, @RequestHeader(USER_ID) long userId,
                          @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        log.debug("Обработка запроса PATCH/items/" + itemId);
        ResponseEntity<Object> item = itemClient.update(itemId, userId, itemDto);
        log.debug("Изменена вещь: {}, userId={}", item.getBody(), userId);
        return item;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable long itemId, @RequestHeader(USER_ID) long userId) {
        log.debug("Обработка запроса GET/items/" + itemId);
        ResponseEntity<Object> item = itemClient.get(itemId, userId);
        log.debug("Получена вещь: {}, userId={}", item.getBody(), userId);
        return item;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                @RequestParam(defaultValue = "0") @Min(0) int from,
                                @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Обработка запроса GET/items/search");
        ResponseEntity<Object> items = itemClient.search(text, from, size);
        log.debug("Получен список: {}", items.getBody());
        return items;
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(USER_ID) long userId,
                                                @RequestParam(defaultValue = "0") @Min(0) int from,
                                                @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Обработка запроса GET/items");
        ResponseEntity<Object> items = itemClient.getAll(userId, from, size);
        log.debug("Получен список: {}, userId={}", items.getBody(), userId);
        return items;
    }
}