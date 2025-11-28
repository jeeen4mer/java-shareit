package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private final Long userId = 1L;

    @Test
    void createRequest_ShouldReturnCreatedRequest() throws Exception {
        CreateItemRequestDto createDto = new CreateItemRequestDto("Нужна дрель");

        ItemRequestDto responseDto = new ItemRequestDto();
        responseDto.setId(1L);
        responseDto.setDescription("Нужна дрель");
        responseDto.setCreated(LocalDateTime.now());
        responseDto.setItems(List.of());

        when(itemRequestService.createRequest(any(CreateItemRequestDto.class), eq(userId)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }

    @Test
    void createRequest_WithEmptyDescription_ShouldReturnBadRequest() throws Exception {
        CreateItemRequestDto createDto = new CreateItemRequestDto("");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRequest_WithNullDescription_ShouldReturnBadRequest() throws Exception {
        CreateItemRequestDto createDto = new CreateItemRequestDto(null);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserRequests_ShouldReturnUserRequests() throws Exception {
        ItemRequestDto request1 = new ItemRequestDto();
        request1.setId(1L);
        request1.setDescription("Запрос 1");
        request1.setCreated(LocalDateTime.now());
        request1.setItems(List.of());

        ItemRequestDto request2 = new ItemRequestDto();
        request2.setId(2L);
        request2.setDescription("Запрос 2");
        request2.setCreated(LocalDateTime.now());
        request2.setItems(List.of());

        when(itemRequestService.getUserRequests(userId))
                .thenReturn(List.of(request1, request2));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Запрос 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Запрос 2"));
    }

    @Test
    void getOtherUsersRequests_ShouldReturnPaginatedRequests() throws Exception {
        ItemRequestDto request = new ItemRequestDto();
        request.setId(1L);
        request.setDescription("Запрос");
        request.setCreated(LocalDateTime.now());
        request.setItems(List.of());

        when(itemRequestService.getOtherUsersRequests(eq(userId), anyInt(), anyInt()))
                .thenReturn(List.of(request));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Запрос"));
    }

    @Test
    void getOtherUsersRequests_WithDefaultPagination_ShouldReturnRequests() throws Exception {
        ItemRequestDto request = new ItemRequestDto();
        request.setId(1L);
        request.setDescription("Запрос");
        request.setCreated(LocalDateTime.now());
        request.setItems(List.of());

        when(itemRequestService.getOtherUsersRequests(eq(userId), anyInt(), anyInt()))
                .thenReturn(List.of(request));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getRequestById_ShouldReturnRequest() throws Exception {
        Long requestId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(requestId);
        requestDto.setDescription("Запрос");
        requestDto.setCreated(LocalDateTime.now());
        requestDto.setItems(List.of());

        when(itemRequestService.getRequestById(requestId, userId))
                .thenReturn(requestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Запрос"));
    }

    @Test
    void getRequestById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        Long requestId = 999L;

        when(itemRequestService.getRequestById(requestId, userId))
                .thenThrow(new RuntimeException("Request not found"));

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createRequest_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        CreateItemRequestDto createDto = new CreateItemRequestDto("Нужна дрель");

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserRequests_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }
}