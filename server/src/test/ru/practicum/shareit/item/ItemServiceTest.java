package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void getItemsByOwnerId_ShouldReturnItems() {
        Long ownerId = 1L;
        Item item = new Item();
        item.setId(1L);
        item.setOwnerId(ownerId);

        when(itemRepository.findByOwnerIdOrderById(ownerId)).thenReturn(List.of(item));

        List<ItemDto> result = itemService.getItemsByOwnerId(ownerId);

        assertNotNull(result);
    }

    @Test
    void searchItems_ShouldReturnAvailableItems() {
        when(itemRepository.searchAvailableItems(anyString())).thenReturn(List.of());

        List<ItemDto> result = itemService.searchItems("дрель");

        assertNotNull(result);
    }
}