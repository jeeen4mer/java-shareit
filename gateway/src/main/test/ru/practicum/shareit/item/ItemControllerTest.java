package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private final Long userId = 1L;

    @Test
    void createItem_ShouldReturnCreatedItem() throws Exception {

        CreateItemDto createDto = new CreateItemDto();
        createDto.setName("Test Item Name");
        createDto.setDescription("Test Item Description");
        createDto.setAvailable(true);

        ItemDto responseDto = new ItemDto();

        when(itemService.createItem(any(CreateItemDto.class), eq(userId)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getItem_ShouldReturnItem() throws Exception {
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto();

        when(itemService.getItemById(itemId, userId))
                .thenReturn(itemDto);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    void searchItems_ShouldReturnItems() throws Exception {
        when(itemService.searchItems(anyString()))
                .thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk());
    }
}