package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemWithRequestResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto add(long userId, ru.practicum.shareit.request.ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        ItemRequest itemRequest = requestMapper.toItemRequest(itemRequestDto, user);
        return requestMapper.toItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestWithItemsResponseDto> getAll(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки " +
                        "пользователя на наличие в Storage! Пользователь не найден!"));
        List<ItemRequest> requests = requestRepository.findAllByRequestorId(userId, Sort.by(Sort.Direction.ASC,
                "created"));
        List<Long> ids = new ArrayList<>();
        HashMap<Long, List<Item>> itemRequestItems = new HashMap<>();
        for (ItemRequest itemRequest : requests) {
            ids.add(itemRequest.getId());
            itemRequestItems.put(itemRequest.getId(), new ArrayList<Item>());
        }
        List<ItemRequestWithItemsResponseDto> itemRequestWithItemsResponseDtos = new ArrayList<>();
        List<Item> items = itemRepository.findAllByRequestIdIn(ids);
        if (items.size() > 0) {
            for (Item item : items) {
                Long idRequest = item.getRequest().getId();
                List<Item> list = itemRequestItems.get(idRequest);
                list.add(item);
                itemRequestItems.replace(idRequest, list);
            }
            for (ItemRequest itemRequest : requests) {
                List<ItemWithRequestResponseDto> itemsDto = itemRequestItems.get(itemRequest.getId()).stream().map(item ->
                        itemMapper.toItemDtoRequestAnswer(item)).collect(Collectors.toList());
                ItemRequestWithItemsResponseDto itemDtoRequestAnswer = requestMapper.toItemRequestDtoOutput(itemRequest, itemsDto);
                itemRequestWithItemsResponseDtos.add(itemDtoRequestAnswer);
            }
        } else {
            itemRequestWithItemsResponseDtos = requests.stream().map(itemRequest ->
                    requestMapper.toItemRequestDtoOutputNullRequest(itemRequest)).collect(Collectors.toList());
        }
        return itemRequestWithItemsResponseDtos;
    }

    @Override
    public List<ItemRequestWithItemsResponseDto> getAllOther(long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        List<ItemRequestWithItemsResponseDto> itemRequestWithItemsResponseDtos = new ArrayList<>();
        List<ItemRequest> request = new ArrayList<>();
        Page<ItemRequest> pages = requestRepository.findAllByRequestorIdNot(userId, pageable);
        request = pages.get().collect(Collectors.toList());
        List<Long> ids = new ArrayList<>();
        HashMap<Long, List<Item>> itemRequestItems = new HashMap<>();
        for (ItemRequest itemRequest : request) {
            ids.add(itemRequest.getId());
            itemRequestItems.put(itemRequest.getId(), new ArrayList<Item>());
        }
        List<Item> items = itemRepository.findAllByRequestIdIn(ids);
        if (items.size() > 0) {
            for (Item item : items) {
                Long idRequest = item.getRequest().getId();
                List<Item> list = itemRequestItems.get(idRequest);
                list.add(item);
                itemRequestItems.replace(idRequest, list);
            }
            for (ItemRequest itemRequest : request) {
                List<ItemWithRequestResponseDto> itemsDto = itemRequestItems.get(itemRequest.getId()).stream().map(item ->
                        itemMapper.toItemDtoRequestAnswer(item)).collect(Collectors.toList());
                ItemRequestWithItemsResponseDto itemDtoRequestAnswer = requestMapper.toItemRequestDtoOutput(itemRequest, itemsDto);
                itemRequestWithItemsResponseDtos.add(itemDtoRequestAnswer);
            }
        } else {
            itemRequestWithItemsResponseDtos = request.stream().map(itemRequest ->
                    requestMapper.toItemRequestDtoOutputNullRequest(itemRequest)).collect(Collectors.toList());
        }
        return itemRequestWithItemsResponseDtos;
    }

    @Override
    public ItemRequestWithItemsResponseDto get(long userId, long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки пользователя на наличие в Storage! " +
                        "Пользователь не найден!"));
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Ошибка проверки запроса на наличие в Storage! " +
                        "Запрос не найден!"));
        List<ItemWithRequestResponseDto> items = itemRepository.findAllByRequestId(itemRequest.getId(),
                Sort.by(Sort.Direction.ASC, "id")).stream().map(item ->
                itemMapper.toItemDtoRequestAnswer(item)).collect(Collectors.toList());
        if (items.size() > 0) {
            return requestMapper.toItemRequestDtoOutput(itemRequest, items);
        } else {
            return requestMapper.toItemRequestDtoOutputNullRequest(itemRequest);
        }
    }
}
