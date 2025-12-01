package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    private User user;
    private CommentRequestDto commentRequestDto;
    private ItemWithBookingsDto itemWithBookingsDto;
    private CommentResponseDto commentResponseDto;
    private ItemRequestDto itemRequestDto;
    private ItemResponseDto itemResponseDto;

    @BeforeEach
    void setUpTest() {
        user = User.builder()
                .id(1L)
                .name("vvvvvv")
                .email("vvvvvvv@vvvvv.ru")
                .build();

        BookingShortDto lastBooking = BookingShortDto.builder()
                .id(1L)
                .bookerId(1L)
                .build();

        BookingShortDto nextBooking = BookingShortDto.builder()
                .id(1L)
                .bookerId(1L)
                .build();

        commentRequestDto = CommentRequestDto.builder()
                .text("text")
                .build();

        commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .text("text")
                .itemId(1L)
                .authorName("vvvvvv")
                .created(LocalDateTime.of(2024, 1, 2, 23, 0, 0))
                .build();

        itemWithBookingsDto = ItemWithBookingsDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(false)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(List.of(commentResponseDto))
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(false)
                .ownerId(1L)
                .requestId(1L)
                .build();

        itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("nameff")
                .description("descriptionff")
                .available(false)
                .requestId(1L)
                .build();
    }

    @SneakyThrows
    @Test
    void addItemTest() {
        when(itemService.addItem(user.getId(), itemRequestDto))
                .thenReturn(itemResponseDto);
        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemResponseDto), result);
    }

    @SneakyThrows
    @Test
    void updateItemTest() {
        when(itemService.updateItem(itemRequestDto, 1L, 1L))
                .thenReturn(itemResponseDto);
        String result = mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemResponseDto), result);
    }

    @SneakyThrows
    @Test
    void getItemTest() {
        Long userId = 1L;
        Long itemId = 1L;
        when(itemService.getItem(userId, itemId))
                .thenReturn(itemWithBookingsDto);
        String result = mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemWithBookingsDto), result);
    }

    @SneakyThrows
    @Test
    void createCommentTest() {
        when(itemService.addComment(user.getId(), itemRequestDto.getId(), commentRequestDto))
                .thenReturn(commentResponseDto);
        String result = mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(commentResponseDto), result);
    }
}

