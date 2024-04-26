package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Обработка запроса POST/requests");
        ItemRequestDto itemRequest = requestServiceImpl.add(userId, itemRequestDto);
        log.debug("Создан запрос: {}", itemRequest);
        return itemRequest;
    }

    @GetMapping
    public List<ItemRequestDtoOutput> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Обработка запроса GET/requests");
        List<ItemRequestDtoOutput> itemRequests = requestServiceImpl.getAll(userId);
        log.debug("Получен список с размером: {}", itemRequests.size());
        return itemRequests;
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOutput> getAllOther(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                  @RequestParam(required = false) @Min(1) Integer size) {
        log.debug("Обработка запроса GET/requests/all");
        List<ItemRequestDtoOutput> itemRequests = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        if (size == null) {
            itemRequests = requestServiceImpl.getAllOtherSort(userId, sort);
        } else {
            int page = from / size;
            Pageable pageable = PageRequest.of(page, size, sort);
            itemRequests = requestServiceImpl.getAllOtherPageable(userId, pageable);
        }
        log.debug("Получен список с размером: {}", itemRequests.size());
        return itemRequests;
    }

    @GetMapping("{requestId}")
    public ItemRequestDtoOutput get(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        log.debug("Обработка запроса GET/requests/" + requestId);
        ItemRequestDtoOutput itemRequest = requestServiceImpl.get(userId, requestId);
        log.debug("Получен запрос: {}", itemRequest);
        return itemRequest;
    }
}
