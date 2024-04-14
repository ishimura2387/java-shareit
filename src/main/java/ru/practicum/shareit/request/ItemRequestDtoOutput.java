package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.ItemDtoRequestAnswer;

import java.util.List;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemRequestDtoOutput {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDtoRequestAnswer> items;
}
