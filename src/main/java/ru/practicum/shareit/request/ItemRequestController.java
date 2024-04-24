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
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody
    ItemRequestDto itemRequestDto) {
        return requestServiceImpl.add(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoOutput> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestServiceImpl.getAll(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOutput> getAllOther(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(defaultValue = "0") Integer from,
                                                         @RequestParam(required = false) Integer size) {
        return requestServiceImpl.getAllOther(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestDtoOutput get(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        return requestServiceImpl.get(userId, requestId);
    }
}
