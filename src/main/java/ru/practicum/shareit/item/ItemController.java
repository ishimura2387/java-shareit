package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/{itemId}/comment")
    public CommentDto createNewComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                                       @Valid @RequestBody CommentDto commentDto) {
        return itemService.createNewComment(commentDto, userId, itemId);
    }

    @PostMapping
    public ItemDto createNewItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.createNewItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        return itemService.updateItem(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithDate getItem(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItem(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsForRent(@RequestParam String text) {
        return itemService.getItemsForRent(text);
    }

    @GetMapping
    public List<ItemDtoWithDate> getMyItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getMyItems(userId);
    }
}
