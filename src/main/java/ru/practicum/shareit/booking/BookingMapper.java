package ru.practicum.shareit.booking;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Booking toBooking(BookingDto bookingDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    BookingDto fromBooking(Booking booking);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Booking updateBooking(BookingDto bookingDto, @MappingTarget Booking booking);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "item.id", source = "itemId")
    Booking fromBookingDtoInput(BookingInputDto bookingInputDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "bookerId", source = "booker.id")
    BookingShortResponseDto toBookingDtoShort(Booking booking);
}

