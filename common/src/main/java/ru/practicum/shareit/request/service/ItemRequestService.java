package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(CreateItemRequestDto createItemRequestDto, Long userId);

    List<ItemRequestDto> getUserRequests(Long userId);

    List<ItemRequestDto> getOtherUsersRequests(Long userId, int from, int size);

    ItemRequestDto getRequestById(Long requestId, Long userId);
}