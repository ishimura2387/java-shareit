package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemWithRequestResponseDto;
import ru.practicum.shareit.user.User;

import java.util.Collections;
import java.util.List;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class RequestMapper {
    public ItemRequest toItemRequest(ru.practicum.shareit.request.ItemRequestDto itemRequestDto, User requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    public ru.practicum.shareit.request.ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ru.practicum.shareit.request.ItemRequestDto itemRequestDto = new ru.practicum.shareit.request.ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequestor(itemRequest.getRequestor());
        itemRequestDto.setCreated(itemRequest.getCreated());
        return itemRequestDto;
    }

    public ItemRequestWithItemsResponseDto toItemRequestDtoOutput(ItemRequest itemRequest, List<ItemWithRequestResponseDto> items) {
        ItemRequestWithItemsResponseDto itemRequestWithItemsResponseDto = new ItemRequestWithItemsResponseDto();
        itemRequestWithItemsResponseDto.setId(itemRequest.getId());
        itemRequestWithItemsResponseDto.setDescription(itemRequest.getDescription());
        itemRequestWithItemsResponseDto.setCreated(itemRequest.getCreated());
        itemRequestWithItemsResponseDto.setItems(items);
        return itemRequestWithItemsResponseDto;
    }

    public ItemRequestWithItemsResponseDto toItemRequestDtoOutputNullRequest(ItemRequest itemRequest) {
        ItemRequestWithItemsResponseDto itemRequestWithItemsResponseDto = new ItemRequestWithItemsResponseDto();
        itemRequestWithItemsResponseDto.setId(itemRequest.getId());
        itemRequestWithItemsResponseDto.setDescription(itemRequest.getDescription());
        itemRequestWithItemsResponseDto.setCreated(itemRequest.getCreated());
        itemRequestWithItemsResponseDto.setItems(Collections.EMPTY_LIST);
        return itemRequestWithItemsResponseDto;
    }
}
