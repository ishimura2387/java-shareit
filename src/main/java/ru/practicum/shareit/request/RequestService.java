package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface RequestService {
    ItemRequestDto add(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestWithItemsResponseDto> getAll(long userId);

    List<ItemRequestWithItemsResponseDto> getAllOtherSort(long userId, Sort sort);

    List<ItemRequestWithItemsResponseDto> getAllOtherPageable(long userId, Pageable pageable);

    ItemRequestWithItemsResponseDto get(long userId, long requestId);
}
