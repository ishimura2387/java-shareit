package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
    private long requestId;
}

