package ru.practicum.shareit.item;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.request.RequestMapper;

@Mapper(componentModel = "spring", uses = {BookingMapper.class, RequestMapper.class})
public interface ItemMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Item toItem(ItemDto itemDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "requestId", source = "request.id")
    ItemDto fromItem(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Item updateItem(ItemDto itemDto, @MappingTarget Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ItemWithDateResponseDto toItemDtoWithDate(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "requestId", source = "request.id")
    ItemWithRequestResponseDto toItemDtoRequestAnswer(Item item);
}
