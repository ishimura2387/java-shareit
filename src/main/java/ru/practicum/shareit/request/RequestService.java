package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RequestService {
    ItemRequestDto add(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestWithItemsResponseDto> getAll(long userId);

    List<ItemRequestWithItemsResponseDto> getAllOther(long userId, Pageable pageable);

    ItemRequestWithItemsResponseDto get(long userId, long requestId);
}
