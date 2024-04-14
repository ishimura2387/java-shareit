package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemRequestDto {
    private long id;
    @NotBlank
    private String description;
    private User requestor;
    private LocalDateTime created;
}
