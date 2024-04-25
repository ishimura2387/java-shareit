package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface RequestService {
    ItemRequestDto add(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoOutput> getAll(long userId);

    List<ItemRequestDtoOutput> getAllOtherSort(long userId, Sort sort);

    List<ItemRequestDtoOutput> getAllOtherPageable(long userId, Pageable pageable);

    ItemRequestDtoOutput get(long userId, long requestId);
}
