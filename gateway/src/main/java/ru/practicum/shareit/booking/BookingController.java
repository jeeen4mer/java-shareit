package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.constant.State;
import ru.practicum.shareit.exception.ValidException;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                             @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingClient.addBooking(bookerId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam Boolean approved) {
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOfOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                     @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        State updatedState = validState(state);
        return bookingClient.getBookingsOfOwner(ownerId, updatedState, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsOfBooker(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                      @RequestParam(defaultValue = "ALL") String state,
                                                      @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                      @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        State updatedState = validState(state);
        return bookingClient.getBookingsOfBooker(bookerId, updatedState, from, size);
    }

    private State validState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidException(String.format("Unknown state: %s", state), HttpStatus.BAD_REQUEST);
        }
    }
}
