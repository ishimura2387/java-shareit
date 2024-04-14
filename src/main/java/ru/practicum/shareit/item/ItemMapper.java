package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.request.RequestMapper;
import org.mapstruct.Mapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingTarget;

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
    ItemDtoWithDate toItemDtoWithDate(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "requestId", source = "request.id")
    ItemDtoRequestAnswer toItemDtoRequestAnswer(Item item);
}
