package ru.practicum.shareit.bookingTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingInputDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class RestBookingTest {
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    LocalDateTime start = LocalDateTime.of(2030, 12, 25, 12, 00, 00);
    LocalDateTime end = LocalDateTime.of(2031, 12, 25, 12, 00, 00);
    private UserDto userDto1 = new UserDto(1, "user1", "user1@mail.ru");
    private ItemDto itemDto1 = new ItemDto(1, "item 1", "description item 1",
            true, null, 0);
    private BookingDto bookingDto = new BookingDto(1, start, end, itemDto1, userDto1, Status.WAITING);
    private BookingInputDto bookingInputDto = new BookingInputDto(1, 1, LocalDateTime.of(2030, 12,
            25, 12, 00, 00), LocalDateTime.of(2031, 12, 25, 12,
            00, 00));
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy,M,d,H,m");
    private List<BookingDto> bookingDtos = new ArrayList<>();

    @BeforeEach
    void beforeEachTest() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        mapper.registerModule(new JavaTimeModule());

    }

    @Test
    void createNewBookingTest() throws Exception {
        when(bookingService.add(any(), anyLong()))
                .thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start").value(Arrays.stream(bookingDto.getStart().format(dtf)
                                .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())))
                .andExpect(jsonPath("$.end").value(Arrays.stream(bookingDto.getEnd().format(dtf)
                                .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void setBookingStatusTest() throws Exception {
        when(bookingService.setStatus(any(Long.class), any(Boolean.class), any(Long.class)))
                .thenReturn(bookingDto);
        mvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start").value(Arrays.stream(bookingDto.getStart().format(dtf)
                                .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())))
                .andExpect(jsonPath("$.end").value(Arrays.stream(bookingDto.getEnd().format(dtf)
                                .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getBookingTest() throws Exception {
        when(bookingService.get(any(Long.class), any(Long.class)))
                .thenReturn(bookingDto);
        mvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start").value(Arrays.stream(bookingDto.getStart().format(dtf)
                                .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())))
                .andExpect(jsonPath("$.end").value(Arrays.stream(bookingDto.getEnd().format(dtf)
                                .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), Status.class));
    }

    @Test
    void getBookingsByUserSortTest() throws Exception {
        bookingDtos.add(bookingDto);
        when(bookingService.getAllByUserSort(any(Long.class), any(String.class),
                any(Sort.class)))
                .thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(bookingDtos))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start").value(Arrays.stream(bookingDto.getStart().format(dtf)
                                .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())))
                .andExpect(jsonPath("$.[0].end").value(Arrays.stream(bookingDto.getEnd().format(dtf)
                                .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getBookingsByUserPageableTest() throws Exception {
        bookingDtos.add(bookingDto);
        when(bookingService.getAllByUserPageable(any(Long.class), any(String.class),
                any(Pageable.class)))
                .thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings?from=1&size=2")
                        .content(mapper.writeValueAsString(bookingDtos))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start").value(Arrays.stream(bookingDto.getStart().format(dtf)
                        .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())))
                .andExpect(jsonPath("$.[0].end").value(Arrays.stream(bookingDto.getEnd().format(dtf)
                        .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getBookingsByOwnerSortTest() throws Exception {
        bookingDtos.add(bookingDto);
        when(bookingService.getAllByOwnerSort(any(Long.class), any(String.class),
                any(Sort.class)))
                .thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(bookingDtos))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start").value(Arrays.stream(bookingDto.getStart().format(dtf)
                                .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())))
                .andExpect(jsonPath("$.[0].end").value(Arrays.stream(bookingDto.getEnd().format(dtf)
                                .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getBookingsByOwnerPageableTest() throws Exception {
        bookingDtos.add(bookingDto);
        when(bookingService.getAllByOwnerPageable(any(Long.class), any(String.class),
                any(Pageable.class)))
                .thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings/owner?from=1&size=2")
                        .content(mapper.writeValueAsString(bookingDtos))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start").value(Arrays.stream(bookingDto.getStart().format(dtf)
                        .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())))
                .andExpect(jsonPath("$.[0].end").value(Arrays.stream(bookingDto.getEnd().format(dtf)
                        .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().toString())));
    }
}
