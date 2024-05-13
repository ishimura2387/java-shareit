package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addBooking(long userId, BookingInputDto requestDto) {
        String path = "";
        return post(path, userId, requestDto);
    }

    public ResponseEntity<Object> setStatus(long bookingId, boolean approved, long userId) {
        String path = "/" + bookingId + "?approved=" + approved;
        return patch(path, userId);
    }

    public ResponseEntity<Object> get(long userId, long bookingId) {
        String path = "/" + bookingId;
        return get(path, userId);
    }

    public ResponseEntity<Object> getByUser(long userId, String state, Integer from, Integer size) {
        String path = "?state=" + state + "&from=" + from + "&size=" + size;
        return get(path, userId);
    }

    public ResponseEntity<Object> getByOwner(long userId, String state, Integer from, Integer size) {
        String path = "/owner?state=" + state + "&from=" + from + "&size=" + size;
        return get(path, userId);
    }
}
