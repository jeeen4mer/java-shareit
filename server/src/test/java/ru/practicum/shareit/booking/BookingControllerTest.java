package ru.practicum.shareit.booking;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constant.Status;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    private UserDto userDto;
    private BookingRequestDto bookingRequestDto;
    private BookingResponseDto bookingResponseDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("vvv")
                .email("vvvvv@vvvvv.ru")
                .build();

        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(false)
                .requestId(1L)
                .build();

        bookingRequestDto = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2025, 1, 1, 23, 0, 0))
                .end(LocalDateTime.of(2025, 1, 2, 23, 0, 0))
                .build();

        bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 1, 1, 23, 0, 0))
                .end(LocalDateTime.of(2025, 1, 2, 23, 0, 0))
                .booker(userDto)
                .item(itemResponseDto)
                .status(Status.WAITING)
                .build();
    }

    @Test
    @SneakyThrows
    void addBookingTest() {
        when(bookingService.addBooking(userDto.getId(), bookingRequestDto))
                .thenReturn(bookingResponseDto);
        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(bookingResponseDto), result);
    }

    @Test
    @SneakyThrows
    void updateBookingTest() {
        when(bookingService.updateBooking(1L, 1L, true))
                .thenReturn(bookingResponseDto);
        String result = mockMvc.perform(patch("/bookings/{bookingId}?approved=true", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(bookingResponseDto), result);
    }

    @Test
    @SneakyThrows
    void getBookingTest() {
        when(bookingService.getBooking(1L, 1L))
                .thenReturn(bookingResponseDto);
        String result = mockMvc.perform(get("/bookings/{bookerId}", 1L)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(bookingResponseDto), result);
    }

    @Test
    @SneakyThrows
    void getBookingOfOwnerTest() {
        when(bookingService.getBookingsOfOwner(1L, "ALL", 0, 10))
                .thenReturn(List.of(bookingResponseDto));
        String result = mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(List.of(bookingResponseDto)), result);
    }

    @Test
    @SneakyThrows
    void getBookingOfBookerTest() {
        when(bookingService.getBookingsOfBooker(1L, "ALL", 0, 10))
                .thenReturn(List.of(bookingResponseDto));
        String result = mockMvc.perform(get("/bookings?state=ALL&from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(List.of(bookingResponseDto)), result);
    }
}
