package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.springframework.boot.test.json.JsonContent;
import org.springframework.util.StreamUtils;
import ru.practicum.shareit.booking.BookingInputDto;

import org.springframework.core.io.Resource;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingInputDto> jacksonTester;
    @Value("classpath:booking.json")
    Resource bookingResource;

    @Test
    void serializeInCorrectFormat() throws Exception {
        JsonContent<BookingInputDto> result = json.write(bookingInputDto);

    }
}
