package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestDto create(Long requestorId, ItemRequestCreateDto dto) {
        User requestor = getUser(requestorId);

        ItemRequest request = ItemRequest.builder()
                .description(dto.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();

        ItemRequest saved = requestRepository.save(request);
        return ItemRequestMapper.toItemRequestDto(saved, List.of());
    }

    @Override
    public List<ItemRequestDto> getOwnRequests(Long requestorId) {
        getUser(requestorId);

        return requestRepository.findByRequestorId(requestorId).stream()
                .sorted((a, b) -> b.getCreated().compareTo(a.getCreated()))
                .map(r -> ItemRequestMapper.toItemRequestDto(r, getItemsForRequest(r.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        getUser(userId);

        PageRequest page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));

        return requestRepository.findByRequestorIdNot(userId, page).stream()
                .map(r -> ItemRequestMapper.toItemRequestDto(r, getItemsForRequest(r.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        getUser(userId);
        ItemRequest request = getRequest(requestId);
        return ItemRequestMapper.toItemRequestDto(request, getItemsForRequest(requestId));
    }

    private List<Item> getItemsForRequest(Long requestId) {
        return itemRepository.findByRequestId(requestId);
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + id));
    }

    private ItemRequest getRequest(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Запрос не найден: " + id));
    }
}