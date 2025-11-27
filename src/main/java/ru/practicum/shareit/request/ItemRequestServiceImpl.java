package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto createRequest(CreateItemRequestDto createItemRequestDto, Long userId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(createItemRequestDto.getDescription());
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        return toItemRequestDto(savedRequest);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getOtherUsersRequests(Long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNot(userId, pageable);

        return requests.stream()
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Запрос не найден"));

        return toItemRequestDto(request);
    }

    private ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        List<Item> items = itemRepository.findByRequestId(itemRequest.getId());

        List<ItemRequestDto.ResponseItem> responseItems = items.stream()
                .map(item -> new ItemRequestDto.ResponseItem(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getAvailable(),
                        itemRequest.getId(),
                        item.getOwnerId()
                ))
                .collect(Collectors.toList());

        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                responseItems
        );
    }
}