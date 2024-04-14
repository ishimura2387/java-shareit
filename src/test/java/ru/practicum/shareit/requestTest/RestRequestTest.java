package ru.practicum.shareit.requestTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestDtoOutput;
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
import static org.mockito.ArgumentMatchers.nullable;
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
    User user = new User(1, "name1", "email1@mail.ru");
    ItemRequestDto itemRequestDto = new ItemRequestDto(1, "description 1 request", user, start);
    ItemRequestDtoOutput itemRequestDtoOutput = new ItemRequestDtoOutput(1, "description 1 requestOutput", start, null);

    List<ItemRequestDtoOutput> itemRequestDtoOutputList = new ArrayList<>();

    @BeforeEach
    void beforeEachTest() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        mapper.registerModule(new JavaTimeModule());

    }

    @Test
    void createItemRequestTest() throws Exception {
        when(requestService.addRequest(any(Long.class), any()))
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
        itemRequestDtoOutputList.add(itemRequestDtoOutput);
        when(requestService.getMyRequest(any(Long.class)))
                .thenReturn(List.of(itemRequestDtoOutput));
        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoOutputList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(itemRequestDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDtoOutput.getDescription())))
                .andExpect(jsonPath("$.[0].created").value(Arrays.stream(itemRequestDtoOutput.getCreated().format(dtf)
                                .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())));
    }

    @Test
    void getItemRequestsTest() throws Exception {
        itemRequestDtoOutputList.add(itemRequestDtoOutput);
        when(requestService.getAllRequestOtherUsers(any(Long.class), any(Integer.class), nullable(Integer.class)))
                .thenReturn(List.of(itemRequestDtoOutput));
        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(itemRequestDtoOutputList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(itemRequestDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDtoOutput.getDescription())))
                .andExpect(jsonPath("$.[0].created").value(Arrays.stream(itemRequestDtoOutput.getCreated().format(dtf)
                                .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())));
    }

    @Test
    void getItemRequestTest() throws Exception {
        when(requestService.getRequest(any(Long.class), any(Long.class)))
                .thenReturn(itemRequestDtoOutput);
        mvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(itemRequestDtoOutput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemRequestDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoOutput.getDescription())))
                .andExpect(jsonPath("$.created").value(Arrays.stream(itemRequestDtoOutput.getCreated().format(dtf)
                                .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())));
    }
}

