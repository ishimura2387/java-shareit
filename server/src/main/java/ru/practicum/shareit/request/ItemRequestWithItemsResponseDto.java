package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.ItemWithRequestResponseDto;

import java.util.List;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemRequestWithItemsResponseDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemWithRequestResponseDto> items;
}
