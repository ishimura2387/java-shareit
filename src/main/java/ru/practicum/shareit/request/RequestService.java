package ru.practicum.shareit.request;

import java.util.List;

public interface RequestService {
    ItemRequestDto add(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoOutput> getAll(long userId);

    List<ItemRequestDtoOutput> getAllOther(long userId, Integer from, Integer size);

    ItemRequestDtoOutput get(long userId, long requestId);
}
