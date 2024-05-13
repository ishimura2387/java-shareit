package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingInputDto {
	private long itemId;
	@NotNull
	private LocalDateTime start;
	@NotNull
	private LocalDateTime end;
}
