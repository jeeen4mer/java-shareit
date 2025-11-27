package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CreateItemDto;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                null
        );
    }

    public static Item toItem(CreateItemDto createItemDto, Long ownerId) {
        return new Item(
                null,
                createItemDto.getName(),
                createItemDto.getDescription(),
                createItemDto.getAvailable(),
                ownerId,
                createItemDto.getRequestId()
        );
    }
}