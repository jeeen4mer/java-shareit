package ru.practicum.shareit.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.ItemRequestClient;
import ru.practicum.shareit.dto.ItemRequestCreateDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.config.HeaderConstants.X_SHARER_USER_ID;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestClient requestClient;

    @Test
    void shouldCreateRequestTest() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Нужна дрель");

        when(requestClient.create(eq(1L), any())).thenReturn(ResponseEntity.ok().body("{\"id\": 1}"));

        mockMvc.perform(post("/requests")
                        .header(X_SHARER_USER_ID, 1)
                        .content("{\"description\": \"Нужна дрель\"}")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400WhenDescriptionBlankTest() throws Exception {
        mockMvc.perform(post("/requests")
                        .header(X_SHARER_USER_ID, 1)
                        .content("{\"description\": \" \"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetOwnRequestsTest() throws Exception {
        when(requestClient.getOwnRequests(1L)).thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/requests")
                        .header(X_SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetAllRequestsTest() throws Exception {
        when(requestClient.getAllRequests(eq(1L), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID, 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }
}