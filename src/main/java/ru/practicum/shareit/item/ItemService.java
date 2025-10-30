package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(CreateItemDto createItemDto, Long ownerId);

    ItemDto getItemById(Long id);

    List<ItemDto> getItemsByOwnerId(Long ownerId);

    ItemDto updateItem(Long id, UpdateItemDto updateItemDto, Long ownerId);

    void deleteItem(Long id);

    List<ItemDto> searchItems(String text);
}