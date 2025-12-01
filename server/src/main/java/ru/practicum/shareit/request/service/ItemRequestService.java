package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestOutDto addItemRequest(ItemRequestInDto itemRequestInDto, Long userId);

    ItemRequestOutDto getItemRequestById(Long userId, Long requestId);

    List<ItemRequestOutDto> getAllMineRequests(Long userId);

    List<ItemRequestOutDto> getAllItemRequests(Long userId, Integer from, Integer size);
}
