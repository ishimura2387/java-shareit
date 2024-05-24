package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemWithRequestResponseDto {
    private long id;
    private String name;
    private Boolean available;
    private String description;
    private long requestId;
}
