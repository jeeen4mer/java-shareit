package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.dto.CommentCreateDto;
import ru.practicum.shareit.dto.CommentDto;
import ru.practicum.shareit.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.config.HeaderConstants.X_SHARER_USER_ID;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemClient itemClient;

    @Test
    void shouldCreateItemTest() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setName("Дрель");
        dto.setDescription("Аккумуляторная");
        dto.setAvailable(true);

        ItemDto created = new ItemDto();
        created.setId(1L);
        created.setName("Дрель");
        created.setDescription("Аккумуляторная");
        created.setAvailable(true);

        when(itemClient.create(eq(1L), any())).thenReturn(ResponseEntity.ok(created));

        mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, 1)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldReturn400WhenNameBlankTest() throws Exception {
        ItemDto invalid = new ItemDto();
        invalid.setName("   ");
        invalid.setDescription("desc");
        invalid.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, 1)
                        .content(mapper.writeValueAsString(invalid))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAddCommentTest() throws Exception {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Отличная вещь!");

        CommentDto response = new CommentDto();
        response.setId(1L);
        response.setText("Отличная вещь!");
        response.setAuthorName("John");

        when(itemClient.addComment(eq(1L), eq(2L), any())).thenReturn(ResponseEntity.ok(response));

        mockMvc.perform(post("/items/2/comment")
                        .header(X_SHARER_USER_ID, 1)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Отличная вещь!"));
    }

    @Test
    void shouldSearchItemsTest() throws Exception {
        when(itemClient.search("дрель")).thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk());
    }
}