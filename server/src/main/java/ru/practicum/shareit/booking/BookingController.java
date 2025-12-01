package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto addBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                         @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.addBooking(bookerId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long bookingId,
                                            @RequestParam Boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsOfOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                       @RequestParam String state,
                                                       @RequestParam Integer from,
                                                       @RequestParam Integer size) {
        return bookingService.getBookingsOfOwner(ownerId, state, from, size);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsOfBooker(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                        @RequestParam String state,
                                                        @RequestParam Integer from,
                                                        @RequestParam Integer size) {
        return bookingService.getBookingsOfBooker(bookerId, state, from, size);
    }
}
