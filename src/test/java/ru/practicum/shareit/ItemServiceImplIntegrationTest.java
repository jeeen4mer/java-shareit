package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User(null, "Owner", "owner@example.com");
        booker = new User(null, "Booker", "booker@example.com");
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);

        item = new Item(null, "Дрель", "Мощная дрель", true, owner.getId(), null);
        item = itemRepository.save(item);
    }

    @Test
    void getItemsByOwnerId_ShouldReturnItemsWithBookings() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        List<ItemDto> result = itemService.getItemsByOwnerId(owner.getId());

        assertEquals(1, result.size());
        ItemDto itemDto = result.get(0);
        assertEquals("Дрель", itemDto.getName());
        assertNotNull(itemDto.getLastBooking());
        assertEquals(booker.getId(), itemDto.getLastBooking().getBookerId());
    }

    @Test
    void searchItems_WithAvailableText_ShouldReturnMatchingItems() {
        List<ItemDto> result = itemService.searchItems("дрель");

        assertEquals(1, result.size());
        assertEquals("Дрель", result.get(0).getName());
    }

    @Test
    void searchItems_WithBlankText_ShouldReturnEmptyList() {
        List<ItemDto> result = itemService.searchItems("");

        assertTrue(result.isEmpty());
    }

    @Test
    void createItem_WithRequestId_ShouldCreateItemWithRequest() {
        CreateItemDto createDto = new CreateItemDto("Шуруповерт", "Аккумуляторный", true, 1L);

        ItemDto result = itemService.createItem(createDto, owner.getId());

        assertNotNull(result);
        assertEquals("Шуруповерт", result.getName());
        assertEquals("Аккумуляторный", result.getDescription());
    }
}