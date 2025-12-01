package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemResponseDto addItem(Long userId, ItemRequestDto itemDto);

    ItemResponseDto updateItem(ItemRequestDto itemDto, Long userId, Long itemId);

    ItemWithBookingsDto getItem(Long itemId, Long userId);

    List<ItemWithBookingsDto> getItemsOfOwner(Long userId, Integer from, Integer size);

    List<ItemResponseDto> searchItem(String text, Integer from, Integer size);

    CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentDto);
}
