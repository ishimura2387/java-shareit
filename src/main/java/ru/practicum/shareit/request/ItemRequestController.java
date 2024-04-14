package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestService requestServiceImpl;

    @PostMapping
    public ItemRequestDto createNewRequest(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody
    ItemRequestDto itemRequestDto) {
        return requestServiceImpl.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoOutput> getMyRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestServiceImpl.getMyRequest(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOutput> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(defaultValue = "0") Integer from,
                                                         @RequestParam(required = false) Integer size) {
        return requestServiceImpl.getAllRequestOtherUsers(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestDtoOutput getItemRequest(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        return requestServiceImpl.getRequest(userId, requestId);
    }
}
