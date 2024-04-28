package ru.practicum.shareit.requestTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestWithItemsResponseDto;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RestRequestTest {
    @Mock
    private RequestService requestService;
    @InjectMocks
    private ItemRequestController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    LocalDateTime start = LocalDateTime.of(2030, 12, 25, 12, 00, 00);
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy,M,d,H,m");
    User user = new User(1L, "name1", "email1@mail.ru");
    ItemRequestDto itemRequestDto = new ItemRequestDto(1, "description 1 request", user, start);
    ItemRequestWithItemsResponseDto itemRequestWithItemsResponseDto = new ItemRequestWithItemsResponseDto(1, "description 1 requestOutput", start, null);

    List<ItemRequestWithItemsResponseDto> itemRequestWithItemsResponseDtoList = new ArrayList<>();

    @BeforeEach
    void beforeEachTest() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        mapper.registerModule(new JavaTimeModule());

    }

    @Test
    void createItemRequestTest() throws Exception {
        when(requestService.add(any(Long.class), any()))
                .thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor.id", is(itemRequestDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.requestor.name", is(itemRequestDto.getRequestor().getName())))
                .andExpect(jsonPath("$.requestor.email", is(itemRequestDto.getRequestor().getEmail())))
                .andExpect(jsonPath("$.created").value(Arrays.stream(itemRequestDto.getCreated().format(dtf)
                                .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())));
    }

    @Test
    void getMyRequestsTest() throws Exception {
        itemRequestWithItemsResponseDtoList.add(itemRequestWithItemsResponseDto);
        when(requestService.getAll(any(Long.class)))
                .thenReturn(List.of(itemRequestWithItemsResponseDto));
        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(itemRequestWithItemsResponseDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(itemRequestWithItemsResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestWithItemsResponseDto.getDescription())))
                .andExpect(jsonPath("$.[0].created").value(Arrays.stream(itemRequestWithItemsResponseDto.getCreated().format(dtf)
                                .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())));
    }

    @Test
    void getItemRequestsPageableTest() throws Exception {
        itemRequestWithItemsResponseDtoList.add(itemRequestWithItemsResponseDto);
        when(requestService.getAllOther(any(Long.class), any(Pageable.class)))
                .thenReturn(List.of(itemRequestWithItemsResponseDto));
        mvc.perform(get("/requests/all?from=1&size=2")
                        .content(mapper.writeValueAsString(itemRequestWithItemsResponseDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(itemRequestWithItemsResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestWithItemsResponseDto.getDescription())))
                .andExpect(jsonPath("$.[0].created").value(Arrays.stream(itemRequestWithItemsResponseDto.getCreated().format(dtf)
                        .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())));
    }

    @Test
    void getItemRequestTest() throws Exception {
        when(requestService.get(any(Long.class), any(Long.class)))
                .thenReturn(itemRequestWithItemsResponseDto);
        mvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(itemRequestWithItemsResponseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemRequestWithItemsResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestWithItemsResponseDto.getDescription())))
                .andExpect(jsonPath("$.created").value(Arrays.stream(itemRequestWithItemsResponseDto.getCreated().format(dtf)
                                .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())));
    }
}

