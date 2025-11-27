package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private final User user = new User(1L, "User", "user@example.com");
    private final ItemRequest request = new ItemRequest(1L, "Нужна дрель", user, LocalDateTime.now());

    @Test
    void createRequest_WhenUserExists_ShouldCreateRequest() {
        CreateItemRequestDto createDto = new CreateItemRequestDto("Нужна дрель");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto result = itemRequestService.createRequest(createDto, 1L);

        assertNotNull(result);
        assertEquals("Нужна дрель", result.getDescription());
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void createRequest_WhenUserNotExists_ShouldThrowException() {
        CreateItemRequestDto createDto = new CreateItemRequestDto("Нужна дрель");
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () ->
                itemRequestService.createRequest(createDto, 999L));
    }

    @Test
    void getRequestById_WhenRequestNotExists_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () ->
                itemRequestService.getRequestById(999L, 1L));
    }
}