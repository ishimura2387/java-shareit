package ru.practicum.shareit.request;

import java.util.List;

public interface RequestService {
    ItemRequestDto addRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoOutput> getMyRequest(long userId);

    List<ItemRequestDtoOutput> getAllRequestOtherUsers(long userId, Integer from, Integer size);

    ItemRequestDtoOutput getRequest(long userId, long requestId);
}
