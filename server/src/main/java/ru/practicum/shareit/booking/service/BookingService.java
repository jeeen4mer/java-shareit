package ru.practicum.shareit.booking.service;


import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto addBooking(Long bookerId, BookingRequestDto bookingRequestDto);

    BookingResponseDto updateBooking(Long userId, Long bookingId, Boolean approved);

    BookingResponseDto getBooking(Long userId, Long bookingId);

    List<BookingResponseDto> getBookingsOfOwner(Long ownerId, String state, Integer from, Integer size);

    List<BookingResponseDto> getBookingsOfBooker(Long bookerId, String state, Integer from, Integer size);
}
