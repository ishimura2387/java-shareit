package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {
    public int id;
    @NotNull
    private String description;
    @NotNull
    private User requestor;
    private LocalDateTime created;
}
