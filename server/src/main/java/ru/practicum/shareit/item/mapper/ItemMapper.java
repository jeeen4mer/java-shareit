package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(source = "ownerId", target = "owner.id")
    Item toItemFromRequest(ItemRequestDto requestDto);

    ItemResponseDto toItemResponseDto(Item item);

    List<ItemResponseDto> toItemResponseListDto(List<Item> items);

    ItemWithBookingsDto toItemWithBookingsDto(Item item);

    List<ItemWithBookingsDto> toItemWithBookingsListDto(List<Item> items);

}
