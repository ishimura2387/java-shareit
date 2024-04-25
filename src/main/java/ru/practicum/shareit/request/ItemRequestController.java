package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;


@RestController
@Validated
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
                                                  @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                  @RequestParam(required = false) Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        if (size == null) {
            return requestServiceImpl.getAllOtherSort(userId, sort);
        } else {
            int page = from / size;
            Pageable pageable = PageRequest.of(page, size, sort);
            return requestServiceImpl.getAllOtherPageable(userId, pageable);
        }
    }

    @GetMapping("{requestId}")
    public ItemRequestDtoOutput get(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        return requestServiceImpl.get(userId, requestId);
    }
}
