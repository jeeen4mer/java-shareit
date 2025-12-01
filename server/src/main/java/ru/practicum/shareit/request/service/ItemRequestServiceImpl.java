package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestStorage itemRequestStorage;
    private final UserStorage userStorage;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestOutDto addItemRequest(ItemRequestInDto itemRequestInDto, Long userId) {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new ValidException("Пользователя с таким id нет", HttpStatus.NOT_FOUND));
        LocalDateTime localDateTime = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(user);
        itemRequest.setDescription(itemRequestInDto.getDescription());
        itemRequest.setCreated(localDateTime);
        itemRequestStorage.save(itemRequest);
        return itemRequestMapper.toItemRequestOutDto(itemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestOutDto getItemRequestById(Long userId, Long requestId) {
        if (!userStorage.existsById(userId)) {
            throw new ValidException("Пользователя с таким id нет", HttpStatus.NOT_FOUND);
        }
        ItemRequest itemRequest = itemRequestStorage.findById(requestId).orElseThrow(() ->
                new ValidException("Запроса на предмет с таким id нет", HttpStatus.NOT_FOUND));
        ItemRequestOutDto itemRequestOutDto = itemRequestMapper.toItemRequestOutDto(itemRequest);
        itemRequestOutDto.setItems(itemStorage.findAllByRequestId(requestId).stream()
                .map(itemMapper::toItemResponseDto)
                .collect(Collectors.toList()));
        return itemRequestOutDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestOutDto> getAllMineRequests(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new ValidException("Пользователя с таким id нет", HttpStatus.NOT_FOUND);
        }
        List<ItemRequest> itemRequests = itemRequestStorage.findAllByRequesterId(userId);
        return getItemRequestOutDtoList(itemRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestOutDto> getAllItemRequests(Long userId, Integer from, Integer size) {
        if (!userStorage.existsById(userId)) {
            throw new ValidException("Пользователя с таким id нет", HttpStatus.NOT_FOUND);
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        List<ItemRequest> itemRequests = itemRequestStorage.findAllByRequesterIdNot(userId, pageable).getContent();
        return getItemRequestOutDtoList(itemRequests);
    }

    private List<ItemRequestOutDto> getItemRequestOutDtoList(List<ItemRequest> itemRequests) {
        List<Item> items = itemStorage.findAllByRequestIdIn(itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList()));

        return itemRequests.stream()
                .map(itemRequestMapper::toItemRequestOutDto)
                .peek(a -> a.setItems(items.stream()
                        .map(itemMapper::toItemResponseDto)
                        .filter(b -> Objects.equals(b.getRequestId(), a.getId()))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}
