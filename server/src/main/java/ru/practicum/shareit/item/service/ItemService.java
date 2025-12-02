package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getById(Long userId, Long itemId);

    ItemWithBookingsDto findByIdWithDetails(Long userId, Long itemId);

    List<ItemWithBookingsDto> findByOwnerId(Long ownerId);

    List<ItemDto> search(String text);

    void delete(Long userId, Long itemId);

    CommentDto addComment(Long userId, Long itemId, CommentCreateDto dto);

}