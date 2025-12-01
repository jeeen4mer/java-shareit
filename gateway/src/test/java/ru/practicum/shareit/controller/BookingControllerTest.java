package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.dto.BookingCreateDto;
import ru.practicum.shareit.dto.BookingDto;
import ru.practicum.shareit.model.BookingStatus;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.config.HeaderConstants.X_SHARER_USER_ID;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingClient bookingClient;

    private final LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
    private final LocalDateTime dayAfterTomorrow = LocalDateTime.now().plusDays(2);

    @Test
    void shouldCreateBookingTest() throws Exception {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);
        dto.setStart(tomorrow);
        dto.setEnd(dayAfterTomorrow);

        BookingDto response = new BookingDto();
        response.setId(1L);
        response.setStatus(BookingStatus.WAITING);

        when(bookingClient.create(eq(1L), any())).thenReturn(ResponseEntity.ok(response));

        mockMvc.perform(post("/bookings")
                        .header(X_SHARER_USER_ID, 1)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldReturn400WhenStartInPastTest() throws Exception {
        BookingCreateDto invalid = new BookingCreateDto();
        invalid.setItemId(1L);
        invalid.setStart(LocalDateTime.now().minusDays(1));
        invalid.setEnd(tomorrow);

        mockMvc.perform(post("/bookings")
                        .header(X_SHARER_USER_ID, 1)
                        .content(mapper.writeValueAsString(invalid))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldApproveBookingTest() throws Exception {
        BookingDto approved = new BookingDto();
        approved.setStatus(BookingStatus.APPROVED);

        when(bookingClient.approve(1L, 2L, true)).thenReturn(ResponseEntity.ok(approved));

        mockMvc.perform(patch("/bookings/2")
                        .header(X_SHARER_USER_ID, 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldGetAllByBookerTest() throws Exception {
        when(bookingClient.getAllByBooker(eq(1L), anyString())).thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, 1)
                        .param("state", "WAITING"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetAllByOwnerTest() throws Exception {
        when(bookingClient.getAllByOwner(eq(1L), anyString())).thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }
}