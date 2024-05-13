package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, CommentDto commentDto) {
        String path = "/" + itemId + "/comment";
        return post(path, userId, commentDto);
    }

    public ResponseEntity<Object> addItem(long userId, ItemDto itemDto) {
        String path = "";
        return post(path, userId, itemDto);
    }

    public ResponseEntity<Object> update(long itemId, long userId, ItemDto itemDto) {
        String path = "/" + itemId;
        return patch(path, userId, itemDto);
    }

    public ResponseEntity<Object> get(long itemId, long userId) {
        String path = "/" + itemId;
        return get(path, userId);
    }

    public ResponseEntity<Object> search(String text, Integer from, Integer size) {
        String path = "/search?text=" + text + "&from=" + from + "&size=" + size;
        return get(path);
    }

    public ResponseEntity<Object> getAll(long userId, Integer from, Integer size) {
        String path = "?from=" + from + "&size=" + size;
        return get(path, userId);
    }
}
