package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;


@RestController
@Validated
@Slf4j
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestService requestServiceImpl;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto add(@RequestHeader(USER_ID) long userId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Обработка запроса POST/requests");
        ItemRequestDto itemRequest = requestServiceImpl.add(userId, itemRequestDto);
        log.debug("Создан запрос: {}", itemRequest);
        return itemRequest;
    }

    @GetMapping
    public List<ItemRequestWithItemsResponseDto> getAll(@RequestHeader(USER_ID) long userId) {
        log.debug("Обработка запроса GET/requests");
        List<ItemRequestWithItemsResponseDto> itemRequests = requestServiceImpl.getAll(userId);
        log.debug("Получен список с размером: {}", itemRequests.size());
        return itemRequests;
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemsResponseDto> getAllOther(@RequestHeader(USER_ID) long userId,
                                                             @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                             @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.debug("Обработка запроса GET/requests/all");
        List<ItemRequestWithItemsResponseDto> itemRequests = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, sort);
        itemRequests = requestServiceImpl.getAllOther(userId, pageable);
        log.debug("Получен список с размером: {}", itemRequests.size());
        return itemRequests;
    }

    @GetMapping("{requestId}")
    public ItemRequestWithItemsResponseDto get(@RequestHeader(USER_ID) long userId, @PathVariable long requestId) {
        log.debug("Обработка запроса GET/requests/" + requestId);
        ItemRequestWithItemsResponseDto itemRequest = requestServiceImpl.get(userId, requestId);
        log.debug("Получен запрос: {}", itemRequest);
        return itemRequest;
    }
}
