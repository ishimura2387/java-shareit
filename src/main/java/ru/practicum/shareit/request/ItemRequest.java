package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequest {
    public int id;
    @NotBlank
    private String description;
    @NotNull
    private int requestor;
    @NotNull
    private LocalDateTime created;
}
