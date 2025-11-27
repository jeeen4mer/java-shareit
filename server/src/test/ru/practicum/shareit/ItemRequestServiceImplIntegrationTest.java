package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user1;
    private User user2;
    private ItemRequest request1;
    private ItemRequest request2;

    @BeforeEach
    void setUp() {
        user1 = new User(null, "User1", "user1@example.com");
        user2 = new User(null, "User2", "user2@example.com");
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        request1 = new ItemRequest(null, "Нужна дрель", user1, LocalDateTime.now().minusDays(1));
        request2 = new ItemRequest(null, "Нужен перфоратор", user1, LocalDateTime.now());
        request1 = itemRequestRepository.save(request1);
        request2 = itemRequestRepository.save(request2);

        Item item = new Item(null, "Дрель", "Хорошая дрель", true, user2.getId(), request1.getId());
        itemRepository.save(item);
    }

    @Test
    void createRequest_ShouldCreateRequestSuccessfully() {
        CreateItemRequestDto createDto = new CreateItemRequestDto("Нужен шуруповерт");

        ItemRequestDto result = itemRequestService.createRequest(createDto, user2.getId());

        assertNotNull(result);
        assertEquals("Нужен шуруповерт", result.getDescription());
        assertNotNull(result.getCreated());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void getUserRequests_ShouldReturnUserRequestsOrderedByDateDesc() {
        List<ItemRequestDto> result = itemRequestService.getUserRequests(user1.getId());

        assertEquals(2, result.size());
        assertEquals("Нужен перфоратор", result.get(0).getDescription());
        assertEquals("Нужна дрель", result.get(1).getDescription());

        assertEquals(1, result.get(1).getItems().size());
        assertEquals("Дрель", result.get(1).getItems().get(0).getName());
    }

    @Test
    void getOtherUsersRequests_ShouldReturnOnlyOtherUsersRequests() {
        List<ItemRequestDto> result = itemRequestService.getOtherUsersRequests(user2.getId(), 0, 10);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(req ->
                req.getDescription().contains("дрель") || req.getDescription().contains("перфоратор")));
    }

    @Test
    void getRequestById_ShouldReturnSpecificRequest() {
        ItemRequestDto result = itemRequestService.getRequestById(request1.getId(), user2.getId());

        assertNotNull(result);
        assertEquals("Нужна дрель", result.getDescription());
        assertEquals(1, result.getItems().size());
        assertEquals("Дрель", result.getItems().get(0).getName());
    }
}