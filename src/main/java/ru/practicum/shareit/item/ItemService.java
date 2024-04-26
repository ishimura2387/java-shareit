package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto itemDto, long userId);

    ItemDto update(ItemDto itemDto, long userId, long itemId);

    ItemWithDateResponseDto get(long itemId, long userId);


    List<ItemWithDateResponseDto> getAllSort(long id, Sort sort);

    List<ItemWithDateResponseDto> getAllPageable(long id, Pageable pageable);

    List<ItemDto> searchSort(String text, Sort sort);

    List<ItemDto> searchPageable(String text, Pageable pageable);

    CommentDto addComment(CommentDto commentDto, long userId, long itemId);
}
