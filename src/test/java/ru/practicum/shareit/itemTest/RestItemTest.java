package ru.practicum.shareit.itemTest;

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
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemDtoWithDate;
import ru.practicum.shareit.item.ItemService;
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
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RestItemTest {
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private ItemDto itemDto = new ItemDto(1, "item 1", "description item 1",
            true, null, 0);

    private ItemDtoWithDate itemDtoWithDate = new ItemDtoWithDate(1, "user1", "user1@mail.ru",
            true, null, null, null);
    private UserDto userDto = new UserDto(1, "user1", "user1@mail.ru");

    private CommentDto commentDto = new CommentDto(1, "comment 1 text", "user1",
            LocalDateTime.of(2030, 12, 25, 12, 00, 00));
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy,M,d,H,m");
    List<ItemDto> itemDtos = new ArrayList<>();
    List<ItemDtoWithDate> itemDtoWithDates = new ArrayList<>();

    @BeforeEach
    void beforeEachTest() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        mapper.registerModule(new JavaTimeModule());

    }

    @Test
    void createCommentTest() throws Exception {
        when(itemService.addComment(any(), any(Long.class), any(Long.class)))
                .thenReturn(commentDto);
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created").value(Arrays.stream(commentDto.getCreated().format(dtf)
                                .split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList())));
    }

    @Test
    void createItemTest() throws Exception {
        when(itemService.add(any(), any(Long.class)))
                .thenReturn(itemDto);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.update(any(), any(Long.class), any(Long.class)))
                .thenReturn(itemDto);
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getItemTest() throws Exception {
        when(itemService.get(any(Long.class), any(Long.class)))
                .thenReturn(itemDtoWithDate);
        mvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDtoWithDate.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithDate.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoWithDate.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoWithDate.getAvailable())));
    }

    @Test
    void getItemsForRentTest() throws Exception {
        itemDtos.add(itemDto);
        when(itemService.search(any(String.class), any(Integer.class), nullable(Integer.class)))
                .thenReturn(List.of(itemDto));
        mvc.perform(get("/items/search?text=description")
                        .content(mapper.writeValueAsString(itemDtos))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())));
    }

    @Test
    void getMyItemsTest() throws Exception {
        itemDtoWithDates.add(itemDtoWithDate);
        when(itemService.getAll(any(Long.class), any(Integer.class), nullable(Integer.class)))
                .thenReturn(List.of(itemDtoWithDate));
        mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(itemDtoWithDates))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(itemDtoWithDate.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDtoWithDate.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDtoWithDate.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDtoWithDate.getAvailable())));
    }
}
