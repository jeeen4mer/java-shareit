package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constant.Status;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private BookingService bookingService;

    @Autowired
    ItemRequestStorage itemRequestStorage;

    private ItemRequestDto itemRequestDto;
    private ItemResponseDto itemResponseDto;
    private ItemWithBookingsDto itemWithBookingsDto;
    private User user1;
    private User user2;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Long itemId1 = 1L;
    private final Long userId1 = 1L;
    private final Long userId2 = 2L;
    private final Long invalidId = 999L;
    private final int from = 0;
    private final int size = 2;

    public void init() {
        user1 = User.builder()
                .email("ruru@yandex.ru")
                .name("RuRu")
                .build();
        user2 = User.builder()
                .email("comcom@gmail.com")
                .name("ComCom")
                .build();
        userStorage.save(user1);
        userStorage.save(user2);
    }

    public void setUp() {

        itemRequestDto = ItemRequestDto.builder()
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .build();

        itemResponseDto = ItemResponseDto.builder()
                .id(itemId1)
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .build();

        itemWithBookingsDto = ItemWithBookingsDto.builder()
                .id(itemId1)
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .comments(new ArrayList<>())
                .build();
    }

    @Test
    @Order(0)
    @SneakyThrows
    public void testCreateItem_ReturnsStatusCreated() {
        init();
        setUp();

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemResponseDto)));
    }

    @Test
    @Order(1)
    @SneakyThrows
    public void testCreateItem_WithInvalidUserId_ReturnsStatusNotFound() {
        setUp();

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", invalidId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        ValidException.class));
    }

    @Test
    @Order(2)
    @SneakyThrows
    public void testUpdateItem1_WithInvalidItemId_ReturnsStatusNotFound() {
        setUp();

        itemRequestDto.toBuilder().name("Дрель--").build();

        mvc.perform(patch("/items/{itemId}", invalidId)
                        .header("X-Sharer-User-Id", userId1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        ValidException.class));
    }

    @Test
    @Order(3)
    @SneakyThrows
    public void testCreateItem2_WithUser2_ReturnsStatusCreated() {
        setUp();

        itemRequestDto = itemRequestDto.toBuilder()
                .name("Отвертка")
                .description("Аккумуляторная отвертка")
                .available(true)
                .build();
        itemResponseDto = itemResponseDto.toBuilder()
                .id(2L)
                .name("Отвертка")
                .description("Аккумуляторная отвертка")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId2)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemResponseDto)))
                .andReturn();
    }

    @Test
    @Order(4)
    @SneakyThrows
    public void testCreateItem3_WithUser2_ReturnsStatusCreated() {
        setUp();

        itemRequestDto = itemRequestDto.toBuilder()
                .name("Клей Момент")
                .description("Тюбик суперклея марки Момент")
                .available(true)
                .build();
        itemResponseDto = itemResponseDto.toBuilder()
                .id(3L)
                .name("Клей Момент")
                .description("Тюбик суперклея марки Момент")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId2)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemResponseDto)))
                .andReturn();
    }

    @Test
    @Order(5)
    @SneakyThrows
    public void testGetItem_ReturnsStatusOk() {
        mvc.perform(get("/items/{itemId}", itemId1)
                        .header("X-Sharer-User-Id", userId1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    @SneakyThrows
    public void testGetItem_WithUserIdNotOwner_ReturnsStatusOk() {
        itemResponseDto = ItemResponseDto.builder()
                .id(itemId1)
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .requestId(1L)
                .build();

        mvc.perform(get("/items/{itemId}", itemId1)
                        .header("X-Sharer-User-Id", userId2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    @SneakyThrows
    public void testGetItem_WithInvalidItemId_ReturnsStatusNotFound() {
        setUp();

        mvc.perform(get("/items/{itemId}", invalidId)
                        .header("X-Sharer-User-Id", userId1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        ValidException.class));
    }

    @Test
    @Order(8)
    @SneakyThrows
    public void testGetItem_WithInvalidUserId_ReturnsStatusNotFound() {
        setUp();

        mvc.perform(get("/items/{itemId}", invalidId)
                        .header("X-Sharer-User-Id", invalidId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        ValidException.class));
    }

    @Test
    @Order(9)
    @SneakyThrows
    public void testGetAllItems_WithOwnerId_ReturnsStatusOk() {

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId2)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(10)
    @SneakyThrows
    public void testSearchAllItems_WithText_ReturnsStatusOk() {

        ItemResponseDto itemOutputDTO = ItemResponseDto.builder()
                .id(2L)
                .name("Отвертка")
                .description("Аккумуляторная отвертка")
                .available(true)
                .build();
        String text = "отвер";

        List<ItemResponseDto> items = Collections.singletonList(itemOutputDTO);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId2)
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(items)));
    }

    @Test
    @Order(11)
    @SneakyThrows
    public void testAddComment_ReturnsStatusOk() {
        setUp();
        LocalDateTime now = LocalDateTime.now();

        BookingRequestDto bookingInputDTO = BookingRequestDto.builder()
                .start(now.minusHours(2))
                .end(now.minusHours(1))
                .status(Status.APPROVED)
                .bookerId(userId2)
                .itemId(itemId1)
                .build();
        bookingService.addBooking(2L, bookingInputDTO);
        bookingService.updateBooking(userId1, itemId1, true);

        CommentRequestDto commentInputDTO = CommentRequestDto.builder()
                .text("Add comment from user1")
                .build();

        CommentResponseDto commentOutputDTO = CommentResponseDto.builder()
                .id(1L)
                .text("Add comment from user1")
                .authorName("ComCom")
                .created(now)
                .build();

        mvc.perform(post("/items/{itemId}/comment", itemId1)
                        .header("X-Sharer-User-Id", userId2)
                        .content(mapper.writeValueAsString(commentInputDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentOutputDTO.getText())))
                .andExpect(jsonPath("$.authorName", is(commentOutputDTO.getAuthorName())));
    }

    @Test
    @Order(12)
    @SneakyThrows
    public void testUpdateItem_OnlyName_ReturnsStatusOk() {
        setUp();

        ItemRequestDto itemInputDTO = ItemRequestDto.builder()
                .id(itemId1)
                .name("Дрель++")
                .build();

        mvc.perform(patch("/items/{itemId}", itemId1)
                        .header("X-Sharer-User-Id", userId1)
                        .content(mapper.writeValueAsString(itemInputDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(13)
    @SneakyThrows
    public void testUpdateItem_OnlyDescription_ReturnsStatusOk() {
        setUp();
        ItemRequestDto itemInputDTO = ItemRequestDto.builder()
                .id(itemId1)
                .description("Простая дрель++")
                .build();

        mvc.perform(patch("/items/{itemId}", itemId1)
                        .header("X-Sharer-User-Id", userId1)
                        .content(mapper.writeValueAsString(itemInputDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(14)
    @SneakyThrows
    public void testUpdateItem_InvalidRequestId_ReturnsStatusNotFound() {
        setUp();
        ItemRequestDto itemInputDTO = ItemRequestDto.builder()
                .id(itemId1)
                .requestId(invalidId)
                .build();

        mvc.perform(patch("/items/{itemId}", 100L)
                        .header("X-Sharer-User-Id", userId1)
                        .content(mapper.writeValueAsString(itemInputDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        ValidException.class));
    }

    @Test
    @Order(15)
    @SneakyThrows
    public void testAddComment_WithNotConfirmBooking_ReturnsStatusBadRequest() {
        setUp();

        CommentRequestDto commentInputDTO = CommentRequestDto.builder()
                .text("Add comment from user1")
                .build();

        mvc.perform(post("/items/{itemId}/comment", 2L)
                        .header("X-Sharer-User-Id", userId2)
                        .content(mapper.writeValueAsString(commentInputDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        ValidException.class));
    }

    @Test
    @Order(16)
    @SneakyThrows
    public void testUpdateItem_WithNotOwner_ReturnsStatusNotFound() {
        setUp();

        mvc.perform(patch("/items/{itemId}", itemId1)
                        .header("X-Sharer-User-Id", userId2)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        ValidException.class));
    }

    @Test
    @Order(17)
    @SneakyThrows
    public void testAddComment_WithInvalidId_ReturnsStatusNotFound() {
        setUp();

        CommentRequestDto commentInputDTO = CommentRequestDto.builder()
                .text("Add comment from user1")
                .build();

        mvc.perform(post("/items/{itemId}/comment", invalidId)
                        .header("X-Sharer-User-Id", userId2)
                        .content(mapper.writeValueAsString(commentInputDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        ValidException.class));
    }

    @Test
    @Order(18)
    @SneakyThrows
    public void testSearchAllItems_WithEmptyText_ReturnsStatusOk() {
        String text = "";

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId2)
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of())));
    }
}