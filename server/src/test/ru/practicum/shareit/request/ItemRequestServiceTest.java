 package ru.practicum.shareit.request;

 import org.junit.jupiter.api.Test;
 import org.junit.jupiter.api.extension.ExtendWith;
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
 import org.mockito.junit.jupiter.MockitoExtension;
 import org.springframework.data.domain.PageRequest;
 import ru.practicum.shareit.item.ItemRepository;
 import ru.practicum.shareit.request.dto.CreateItemRequestDto;
 import ru.practicum.shareit.request.dto.ItemRequestDto;
 import ru.practicum.shareit.user.User;
 import ru.practicum.shareit.user.UserRepository;

 import java.time.LocalDateTime;
 import java.util.List;
 import java.util.Optional;

 import static org.mockito.ArgumentMatchers.*;
 import static org.mockito.Mockito.when;
 import static org.junit.jupiter.api.Assertions.*;

 @ExtendWith(MockitoExtension.class)
 class ItemRequestServiceTest {

     @Mock
     private ItemRequestRepository itemRequestRepository;

     @Mock
     private UserRepository userRepository;

     @Mock
     private ItemRepository itemRepository;

     @InjectMocks
     private ItemRequestServiceImpl itemRequestService;

     @Test
     void createRequest_ShouldCreateAndReturnRequest() {
         CreateItemRequestDto createDto = new CreateItemRequestDto("Нужна дрель");
         Long userId = 1L;

         User user = new User();
         user.setId(userId);
         user.setName("User");
         user.setEmail("user@mail.ru");

         ItemRequest itemRequest = new ItemRequest();
         itemRequest.setId(1L);
         itemRequest.setDescription("Нужна дрель");
         itemRequest.setRequester(user);
         itemRequest.setCreated(LocalDateTime.now());

         when(userRepository.findById(userId)).thenReturn(Optional.of(user));
         when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

         ItemRequestDto result = itemRequestService.createRequest(createDto, userId);

         assertNotNull(result);
         assertEquals(1L, result.getId());
         assertEquals("Нужна дрель", result.getDescription());
     }

     @Test
     void getUserRequests_ShouldReturnUserRequests() {
         Long userId = 1L;

         User user = new User();
         user.setId(userId);

         ItemRequest request1 = new ItemRequest();
         request1.setId(1L);
         request1.setRequester(user);

         ItemRequest request2 = new ItemRequest();
         request2.setId(2L);
         request2.setRequester(user);

         when(userRepository.findById(userId)).thenReturn(Optional.of(user));
         when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId))
                 .thenReturn(List.of(request1, request2));
         when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of());

         List<ItemRequestDto> result = itemRequestService.getUserRequests(userId);

         assertNotNull(result);
         assertEquals(2, result.size());
     }

     @Test
     void getUserRequests_WhenUserNotFound_ShouldThrowException() {
         Long userId = 1L;

         when(userRepository.findById(userId)).thenReturn(Optional.empty());

         assertThrows(org.springframework.web.server.ResponseStatusException.class,
                 () -> itemRequestService.getUserRequests(userId));
     }

     @Test
     void getOtherUsersRequests_ShouldReturnPaginatedRequests() {
         Long userId = 1L;
         int from = 0;
         int size = 10;

         User user = new User();
         user.setId(userId);

         User otherUser = new User();
         otherUser.setId(2L);

         ItemRequest request = new ItemRequest();
         request.setId(1L);
         request.setRequester(otherUser);
         request.setDescription("Test request");
         request.setCreated(LocalDateTime.now());

         when(itemRequestRepository.findAllByRequesterIdNot(eq(userId), any(PageRequest.class)))
                 .thenReturn(List.of(request));
         when(userRepository.findById(userId)).thenReturn(Optional.of(user));
         when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of());

         List<ItemRequestDto> result = itemRequestService.getOtherUsersRequests(userId, from, size);

         assertNotNull(result);
         assertEquals(1, result.size());
         assertEquals(1L, result.get(0).getId());
     }

     @Test
     void getOtherUsersRequests_WhenUserNotFound_ShouldThrowException() {
         Long userId = 1L;
         int from = 0;
         int size = 10;

         when(userRepository.findById(userId)).thenReturn(Optional.empty());

         assertThrows(org.springframework.web.server.ResponseStatusException.class,
                 () -> itemRequestService.getOtherUsersRequests(userId, from, size));
     }

     @Test
     void getRequestById_ShouldReturnRequest() {
         Long requestId = 1L;
         Long userId = 1L;

         User user = new User();
         user.setId(userId);

         ItemRequest request = new ItemRequest();
         request.setId(requestId);
         request.setDescription("Test request");
         request.setCreated(LocalDateTime.now());

         when(userRepository.findById(userId)).thenReturn(Optional.of(user));
         when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
         when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of());

         ItemRequestDto result = itemRequestService.getRequestById(requestId, userId);

         assertNotNull(result);
         assertEquals(requestId, result.getId());
         assertEquals("Test request", result.getDescription());
     }

     @Test
     void getRequestById_WhenUserNotFound_ShouldThrowException() {
         Long requestId = 1L;
         Long userId = 1L;

         when(userRepository.findById(userId)).thenReturn(Optional.empty());

         assertThrows(org.springframework.web.server.ResponseStatusException.class,
                 () -> itemRequestService.getRequestById(requestId, userId));
     }

     @Test
     void getRequestById_WhenRequestNotFound_ShouldThrowException() {
         Long requestId = 1L;
 Long userId = 1L;

 User user = new User();
 user.setId(userId);

 when(userRepository.findById(userId)).thenReturn(Optional.of(user));
 when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

 assertThrows(org.springframework.web.server.ResponseStatusException.class,
         () -> itemRequestService.getRequestById(requestId, userId));
 }
 }