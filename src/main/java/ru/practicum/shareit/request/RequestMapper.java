package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemDtoRequestAnswer;
import ru.practicum.shareit.user.User;

import java.util.Collections;
import java.util.List;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class RequestMapper {
    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequestor(itemRequest.getRequestor());
        itemRequestDto.setCreated(itemRequest.getCreated());
        return itemRequestDto;
    }

    public ItemRequestDtoOutput toItemRequestDtoOutput(ItemRequest itemRequest, List<ItemDtoRequestAnswer> items) {
        ItemRequestDtoOutput itemRequestDtoOutput = new ItemRequestDtoOutput();
        itemRequestDtoOutput.setId(itemRequest.getId());
        itemRequestDtoOutput.setDescription(itemRequest.getDescription());
        itemRequestDtoOutput.setCreated(itemRequest.getCreated());
        itemRequestDtoOutput.setItems(items);
        return itemRequestDtoOutput;
    }

    public ItemRequestDtoOutput toItemRequestDtoOutputNullRequest(ItemRequest itemRequest) {
        ItemRequestDtoOutput itemRequestDtoOutput = new ItemRequestDtoOutput();
        itemRequestDtoOutput.setId(itemRequest.getId());
        itemRequestDtoOutput.setDescription(itemRequest.getDescription());
        itemRequestDtoOutput.setCreated(itemRequest.getCreated());
        itemRequestDtoOutput.setItems(Collections.EMPTY_LIST);
        return itemRequestDtoOutput;
    }
}
