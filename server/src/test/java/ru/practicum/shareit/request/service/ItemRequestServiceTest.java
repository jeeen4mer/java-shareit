package ru.practicum.shareit.request.service;


import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Nested
@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestStorage itemRequestStorage;

    @Mock
    private UserStorage userStorage;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private ItemStorage itemStorage;

    @Mock
    private ItemMapper itemMapper;

    @Test
    public void testAddItemRequest() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        ItemRequestInDto itemRequestInDto = new ItemRequestInDto();
        itemRequestInDto.setDescription("Test Description");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(user);
        itemRequest.setDescription(itemRequestInDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequestOutDto itemRequestOutDto = new ItemRequestOutDto();

        when(userStorage.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestMapper.toItemRequestOutDto(any(ItemRequest.class))).thenReturn(itemRequestOutDto);

        ItemRequestOutDto result = itemRequestService.addItemRequest(itemRequestInDto, userId);

        assertEquals(itemRequestOutDto, result);
        verify(userStorage).findById(userId);
        verify(itemRequestStorage).save(any(ItemRequest.class));
        verify(itemRequestMapper).toItemRequestOutDto(any(ItemRequest.class));
    }

    @Test
    void addItemRequest_NotExistUserTest() {
        Long userId = 100L;
        assertThrows(ValidException.class, () -> itemRequestService.addItemRequest(ItemRequestInDto.builder().description("Test description").build(), userId));
    }

    @Test
    void getItemRequestByIdTest() {
        Long userId = 1L;
        Long requestId = 1L;
        UserDto userDto = new UserDto(1L, "FF", "fefefe@mail.com");
        User user = new User(1L, "FF", "fefefe@mail.com");
        user.setId(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setRequester(user);

        ItemRequestOutDto itemRequestOutDto = new ItemRequestOutDto();
        itemRequestOutDto.setId(requestId);
        itemRequestOutDto.setRequester(userDto);

        when(userStorage.existsById(userId)).thenReturn(true);
        when(itemRequestStorage.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.toItemRequestOutDto(itemRequest)).thenReturn(itemRequestOutDto);
        when(itemStorage.findAllByRequestId(requestId)).thenReturn(Collections.singletonList(new Item()));
        when(itemStorage.findAllByRequestId(requestId)).thenReturn(List.of(new Item()));
        itemRequestStorage.save(itemRequest);

        ItemRequestOutDto result = itemRequestService.getItemRequestById(userId, requestId);
        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals(userDto, result.getRequester());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void getItemRequest_NotExistUserTest() {
        Long userId = 100L;
        Long requestId = 100L;
        assertThrows(ValidException.class, () -> itemRequestService.getItemRequestById(userId, requestId));
    }

    @Test
    void getItemRequestById_NotExistItemRequestTest() {
        Long requestId = 100L;
        Long userId = 1L;
        when(userStorage.existsById(userId)).thenReturn(true);
        when(itemRequestStorage.findById(requestId)).thenReturn(Optional.empty());
        assertThrows(ValidException.class, () -> itemRequestService.getItemRequestById(userId, requestId));
    }

    @Test
    void getAllMineRequests() {
        UserDto userDto = new UserDto(1L, "JOE", "joe@gmail.com");
        User user = new User();
        user.setId(1L);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setRequester(user);
        ItemRequestOutDto itemRequestOutDto = new ItemRequestOutDto();
        itemRequestOutDto.setId(1L);
        itemRequestOutDto.setRequester(userDto);
        Item item = new Item();
        item.setId(1L);
        item.setRequestId(1L);
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(1L);
        itemResponseDto.setRequestId(1L);

        when(userStorage.existsById(user.getId())).thenReturn(true);
        when(itemRequestStorage.findAllByRequesterId(user.getId())).thenReturn(List.of(itemRequest));
        when(itemStorage.findAllByRequestIdIn(List.of(1L))).thenReturn(List.of(item));
        when(itemRequestMapper.toItemRequestOutDto(itemRequest)).thenReturn(itemRequestOutDto);
        when(itemMapper.toItemResponseDto(item)).thenReturn(itemResponseDto);
        List<ItemRequestOutDto> items = itemRequestService.getAllMineRequests(user.getId());
        assertNotNull(items);
        assertEquals(1, items.size());

    }

    @Test
    void getAllItemRequests_InvalidUserId_ExceptionThrown() {
        Long userId = 2L;
        when(userStorage.existsById(userId)).thenReturn(false);
        assertThrows(ValidException.class, () -> itemRequestService.getAllItemRequests(userId, 0, 5));
    }

    @Test
    void getAllMineRequests_NotExistUserTest() {
        Long userId = 100L;
        assertThrows(ValidException.class, () -> itemRequestService.getAllMineRequests(userId));
    }

    @Test
    void getAllItemRequests_ValidUser_EmptyListReturned() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        List<ItemRequest> itemRequests = Collections.emptyList();
        Page<ItemRequest> pagedResponse = new PageImpl<>(itemRequests);

        when(userStorage.existsById(userId)).thenReturn(true);
        when(itemRequestStorage.findAllByRequesterIdNot(eq(userId), any(Pageable.class))).thenReturn(pagedResponse);

        List<ItemRequestOutDto> result = itemRequestService.getAllItemRequests(userId, from, size);

        assertEquals(Collections.emptyList(), result);
    }
}