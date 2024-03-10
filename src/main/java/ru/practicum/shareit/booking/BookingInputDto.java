package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingInputDto {
    private long id;
    @NonNull
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
