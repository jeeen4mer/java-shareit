package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemStorage itemStorage;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserStorage userStorage;

    @Test
    void addItemTest() {
        Long userId = 1L;
        Long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        item.setName("item1");
        item.setDescription("description1");
        ItemResponseDto responseDto = new ItemResponseDto();
        responseDto.setName("item1");
        responseDto.setDescription("description1");
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("item1");
        itemRequestDto.setDescription("description1");
        User user = new User();
        user.setId(userId);
        user.setName("John Doe");

        when(userStorage.existsById(userId)).thenReturn(true);
        when(itemMapper.toItemFromRequest(itemRequestDto)).thenReturn(item);
        when(itemStorage.save(item)).thenReturn(item);
        when(itemMapper.toItemResponseDto(item)).thenReturn(responseDto);
        ItemResponseDto result = itemService.addItem(userId, itemRequestDto);
        assertNotNull(result);
        assertEquals("item1", result.getName());
        assertEquals("description1", result.getDescription());
    }

    @Test
    void addItem_NotExistUserTest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("item1");
        itemRequestDto.setDescription("description1");
        Long itemId = 100L;
        assertThrows(ValidException.class, () -> itemService.addItem(itemId, itemRequestDto));
    }

    @Test
    void updateItemTest() {
        ItemResponseDto responseDto = new ItemResponseDto();
        responseDto.setName("Updated Item Name");
        responseDto.setDescription("Updated Item Description");
        responseDto.setAvailable(true);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("Updated Item Name");
        itemRequestDto.setDescription("Updated Item Description");
        itemRequestDto.setAvailable(true);
        Long userId = 1L;
        Long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        item.setName("Initial Item Name");
        item.setDescription("Initial Item Description");
        item.setAvailable(false);
        User owner = new User();
        owner.setId(userId);
        item.setOwner(owner);

        Mockito.when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(itemMapper.toItemFromRequest(itemRequestDto)).thenReturn(item);
        Mockito.when(itemMapper.toItemResponseDto(item)).thenReturn(responseDto);
        when(itemStorage.save(item)).thenReturn(item);
        ItemResponseDto updatedItemResponseDto = itemService.updateItem(itemRequestDto, userId, itemId);
        assertEquals("Updated Item Name", updatedItemResponseDto.getName());
        assertEquals("Updated Item Description", updatedItemResponseDto.getDescription());
        assertTrue(updatedItemResponseDto.getAvailable());
    }

    @Test
    void updateItem_ItemRequestNullTest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        assertNull(itemRequestDto.getDescription());
        assertThrows(ValidException.class, () -> itemService.updateItem(itemRequestDto, 1L, 1L));
    }

    @Test
    void updateItem_NotExistItemTest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("Updated Item Name");
        itemRequestDto.setDescription("Updated Item Description");
        itemRequestDto.setAvailable(true);
        Long userId = 100L;
        Long itemId = 100L;
        assertThrows(ValidException.class, () -> itemService.updateItem(itemRequestDto, itemId, userId));
    }

    @Test
    void updateItem_NullItemRequestDto_ExceptionThrown() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemRequestDto itemRequestDto = null;

        assertThrows(ValidException.class, () -> itemService.updateItem(itemRequestDto, userId, itemId));
    }

    @Test
    void addComment_UserNotFound_ExceptionThrown() {
        Long userId = 1L;
        Long itemId = 1L;
        CommentRequestDto commentRequestDto = new CommentRequestDto();

        when(userStorage.existsById(userId)).thenReturn(false);

        assertThrows(ValidException.class, () -> itemService.addComment(userId, itemId, commentRequestDto));
    }
}
