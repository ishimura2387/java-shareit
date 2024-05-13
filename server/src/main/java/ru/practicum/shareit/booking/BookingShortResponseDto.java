package ru.practicum.shareit.booking;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BookingShortResponseDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long bookerId;
    private Status status;
}