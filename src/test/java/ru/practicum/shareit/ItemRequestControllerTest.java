package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

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
        ItemRequestDto responseDto = new ItemRequestDto(1L, "Нужна дрель",
                LocalDateTime.now(), List.of());

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
    void getUserRequests_ShouldReturnUserRequests() throws Exception {
        ItemRequestDto request1 = new ItemRequestDto(1L, "Запрос 1",
                LocalDateTime.now(), List.of());
        ItemRequestDto request2 = new ItemRequestDto(2L, "Запрос 2",
                LocalDateTime.now(), List.of());

        when(itemRequestService.getUserRequests(userId))
                .thenReturn(List.of(request1, request2));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void getOtherUsersRequests_ShouldReturnPaginatedRequests() throws Exception {
        ItemRequestDto request = new ItemRequestDto(1L, "Запрос",
                LocalDateTime.now(), List.of());

        when(itemRequestService.getOtherUsersRequests(eq(userId), anyInt(), anyInt()))
                .thenReturn(List.of(request));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getRequestById_ShouldReturnRequest() throws Exception {
        Long requestId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto(requestId, "Запрос",
                LocalDateTime.now(), List.of());

        when(itemRequestService.getRequestById(requestId, userId))
                .thenReturn(requestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Запрос"));
    }
}