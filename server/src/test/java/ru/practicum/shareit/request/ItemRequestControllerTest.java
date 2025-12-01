package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    private ItemRequestInDto itemRequestInDto;
    private ItemRequestOutDto itemRequestOutDto;

    @BeforeEach
    void setUp() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("vvvvv")
                .email("vvvvvv@vvvvvv.ru")
                .build();
        String description = "description";
        LocalDateTime dateTime = LocalDateTime.of(2024, 4, 1, 23, 0, 0);

        itemRequestInDto = ItemRequestInDto.builder()
                .description(description)
                .build();

        itemRequestOutDto = ItemRequestOutDto.builder()
                .id(1L)
                .description(description)
                .requester(user)
                .created(dateTime)
                .build();
    }

    @SneakyThrows
    @Test
    void addItemRequestTest() {
        when(itemRequestService.addItemRequest(itemRequestInDto, 1L))
                .thenReturn(itemRequestOutDto);
        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestInDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemRequestOutDto), result);
    }

//    @Test
//    @SneakyThrows
//    void addItemRequestBadStatusTest() {
//        itemRequestInDto = itemRequestInDto.toBuilder()
//                .description(null)
//                .build();
//
//        mockMvc.perform(post("/requests")
//                        .header("X-Sharer-User-Id", 1)
//                        .content(objectMapper.writeValueAsString(itemRequestInDto))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//
//        verify(itemRequestService, never()).addItemRequest(any(ItemRequestInDto.class), anyLong());
//    }

//    @SneakyThrows
//    @Test
//    void getAllMineRequestsTest() {
//        when(itemRequestService.getAllMineRequests(1L))
//                .thenReturn(List.of(itemRequestOutDto));
//        String result = mockMvc.perform(get("/requests")
//                        .header("X-Sharer-User-Id", 1))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//        assertEquals(objectMapper.writeValueAsString(List.of(itemRequestOutDto)), result);
//    }

//    @SneakyThrows
//    @Test
//    void getAllItemRequestsTest() {
//        long userId = 1L;
//        when(itemRequestService.getAllItemRequests(userId, 0, 10))
//                .thenReturn(List.of(itemRequestOutDto));
//        String result = mockMvc.perform(get("/requests/all")
//                        .header("X-Sharer-User-Id", 1))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//        assertEquals(objectMapper.writeValueAsString(List.of(itemRequestOutDto)), result);
//    }

    @SneakyThrows
    @Test
    void getItemRequestByIdTest() {
        long userId = 1L;
        long itemRequestId = 1L;
        when(itemRequestService.getItemRequestById(userId, itemRequestId))
                .thenReturn(itemRequestOutDto);
        String result = mockMvc.perform(get("/requests/{requestId}", itemRequestId)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemRequestOutDto), result);
    }
}

