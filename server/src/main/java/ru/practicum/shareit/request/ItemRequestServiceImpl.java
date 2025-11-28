package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createRequest(CreateItemRequestDto createItemRequestDto, Long userId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Пользователь не найден"));

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(createItemRequestDto.getDescription());
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);

        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(savedItemRequest.getId());
        dto.setDescription(savedItemRequest.getDescription());
        dto.setCreated(savedItemRequest.getCreated());
        dto.setItems(List.of());
        return dto;
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Пользователь не найден"));

        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);

        return requests.stream().map(request -> {
            ItemRequestDto dto = new ItemRequestDto();
            dto.setId(request.getId());
            dto.setDescription(request.getDescription());
            dto.setCreated(request.getCreated());

            List<Item> relatedItems = itemRepository.findByRequestId(request.getId());
            List<ItemDto> itemDtos = relatedItems.stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
            dto.setItems(itemDtos);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getOtherUsersRequests(Long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Пользователь не найден"));

        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNot(userId, pageable);

        return requests.stream().map(request -> {
            ItemRequestDto dto = new ItemRequestDto();
            dto.setId(request.getId());
            dto.setDescription(request.getDescription());
            dto.setCreated(request.getCreated());

            List<Item> relatedItems = itemRepository.findByRequestId(request.getId());
            List<ItemDto> itemDtos = relatedItems.stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
            dto.setItems(itemDtos);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Пользователь не найден"));

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Запрос не найден"));

        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());

        List<Item> relatedItems = itemRepository.findByRequestId(requestId);
        List<ItemDto> itemDtos = relatedItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        dto.setItems(itemDtos);
        return dto;
    }
}