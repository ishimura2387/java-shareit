package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.item.ItemDtoRequestAnswer;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto addRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NullObjectException("Ошибка проверки " +
                "пользователя на наличие в Storage! Пользователь не найден!"));
        ItemRequest itemRequest = requestMapper.toItemRequest(itemRequestDto, user);
        log.debug("Обработка запроса POST /requests. Создан запрос: {}", itemRequest);
        return requestMapper.toItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDtoOutput> getMyRequest(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NullObjectException("Ошибка проверки " +
                "пользователя на наличие в Storage! Пользователь не найден!"));
        List<ItemRequest> requests = requestRepository.findAllByRequestorIdOrderByCreated(userId);
        List<Long> ids = new ArrayList<>();
        for (ItemRequest itemRequest : requests) {
            ids.add(itemRequest.getId());
        }
        List<ItemRequestDtoOutput> itemRequestDtoOutputs = new ArrayList<>();
        List<Item> items = itemRepository.findAllByRequestIdIn(ids);
        if (items.size() > 0) {
            for (ItemRequest itemRequest : requests) {
                List<ItemDtoRequestAnswer> itemsDto = items.stream().filter(item -> item.getRequest().getId() == itemRequest.getId())
                        .map(item -> itemMapper.toItemDtoRequestAnswer(item)).collect(Collectors.toList());
                ItemRequestDtoOutput itemDtoRequestAnswer = requestMapper.toItemRequestDtoOutput(itemRequest, itemsDto);
                itemRequestDtoOutputs.add(itemDtoRequestAnswer);
            }
        } else {
            itemRequestDtoOutputs = requests.stream().map(itemRequest -> requestMapper.toItemRequestDtoOutputNullRequest(itemRequest))
                    .collect(Collectors.toList());
        }
        return itemRequestDtoOutputs;
    }

    @Override
    public List<ItemRequestDtoOutput> getAllRequestOtherUsers(long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NullObjectException("Ошибка проверки " +
                "пользователя на наличие в Storage! Пользователь не найден!"));
        List<ItemRequestDtoOutput> itemRequestDtoOutputs = new ArrayList<>();
        List<ItemRequest> request = new ArrayList<>();
        if (size == null) {
            request = requestRepository.findAllByRequestorIdNotZeroSize(userId).stream().skip(from).collect(Collectors.toList());
            List<Long> ids = new ArrayList<>();
            for (ItemRequest itemRequest : request) {
                ids.add(itemRequest.getId());
            }
            List<Item> items = itemRepository.findAllByRequestIdIn(ids);
            if (items.size() > 0) {
                for (ItemRequest itemRequest : request) {
                    List<ItemDtoRequestAnswer> itemsDto = items.stream().filter(item -> item.getRequest().getId() == itemRequest.getId())
                            .map(item -> itemMapper.toItemDtoRequestAnswer(item)).collect(Collectors.toList());
                    ItemRequestDtoOutput itemDtoRequestAnswer = requestMapper.toItemRequestDtoOutput(itemRequest, itemsDto);
                    itemRequestDtoOutputs.add(itemDtoRequestAnswer);
                }
            } else {
                itemRequestDtoOutputs = request.stream().map(itemRequest -> requestMapper.toItemRequestDtoOutputNullRequest(itemRequest))
                        .collect(Collectors.toList());
            }
        } else {
            if (size < 0 || size == 0 || from < 0) {
                throw new PaginationException("Ошибка пагинации!");
            }
            int page;
            if (from == size) {
                page = from / size - 1;
            } else {
                page = from / size;
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));
            Page<ItemRequest> pages = requestRepository.findAllByRequestorIdNot(userId, pageable);
            request = pages.get().collect(Collectors.toList());
            List<Long> ids = new ArrayList<>();
            for (ItemRequest itemRequest : request) {
                ids.add(itemRequest.getId());
            }
            List<Item> items = itemRepository.findAllByRequestIdIn(ids);
            if (items.size() > 0) {
                for (ItemRequest itemRequest : request) {
                    List<ItemDtoRequestAnswer> itemsDto = items.stream().filter(item -> item.getRequest().getId() == itemRequest.getId())
                            .map(item -> itemMapper.toItemDtoRequestAnswer(item)).collect(Collectors.toList());
                    ItemRequestDtoOutput itemDtoRequestAnswer = requestMapper.toItemRequestDtoOutput(itemRequest, itemsDto);
                    itemRequestDtoOutputs.add(itemDtoRequestAnswer);
                }
            } else {
                itemRequestDtoOutputs = request.stream().map(itemRequest -> requestMapper.toItemRequestDtoOutputNullRequest(itemRequest))
                        .collect(Collectors.toList());
            }
        }
        return itemRequestDtoOutputs;
    }

    @Override
    public ItemRequestDtoOutput getRequest(long userId, long requestId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NullObjectException("Ошибка проверки " +
                "пользователя на наличие в Storage! Пользователь не найден!"));
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() -> new NullObjectException("Ошибка проверки " +
                "запроса на наличие в Storage! Запрос не найден!"));
        List<ItemDtoRequestAnswer> items = itemRepository.findAllByRequestIdOrderById(itemRequest.getId()).stream().map(item ->
                itemMapper.toItemDtoRequestAnswer(item)).collect(Collectors.toList());
        if (items.size() > 0) {
            return requestMapper.toItemRequestDtoOutput(itemRequest, items);
        } else {
            return requestMapper.toItemRequestDtoOutputNullRequest(itemRequest);
        }
    }
}
