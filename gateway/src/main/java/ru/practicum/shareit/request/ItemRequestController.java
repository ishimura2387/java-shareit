package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(USER_ID) long userId, @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.debug("Обработка запроса POST/requests");
        ResponseEntity<Object>  itemRequest = itemRequestClient.addItemRequest(userId,itemRequestDto);
        log.debug("Создан запрос: {}, userId={}", itemRequest.getBody(), userId);
        return itemRequest;
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(USER_ID) long userId) {
        log.debug("Обработка запроса GET/requests");
        ResponseEntity<Object> itemRequest = itemRequestClient.getAll(userId);
        log.debug("Получен список с размером: {}, userId={}", itemRequest.getBody());
        return itemRequest;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllOther(@RequestHeader(USER_ID) long userId,
                                                             @RequestParam(defaultValue = "0") @Min(0) int from,
                                                             @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Обработка запроса GET/requests/all");
        ResponseEntity<Object> itemRequest = itemRequestClient.getAllOther(userId, from, size);
        log.debug("Получен список с размером: {},  userId={}", itemRequest.getBody());
        return itemRequest;
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> get(@RequestHeader(USER_ID) long userId, @PathVariable long requestId) {
        log.debug("Обработка запроса GET/requests/" + requestId);
        ResponseEntity<Object> itemRequest = itemRequestClient.get(userId, requestId);
        log.debug("Получен запрос: {},  userId={}", itemRequest.getBody());
        return itemRequest;
    }
}
