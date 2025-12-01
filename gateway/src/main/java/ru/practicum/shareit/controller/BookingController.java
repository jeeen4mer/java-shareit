package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.dto.BookingCreateDto;

import static ru.practicum.shareit.config.HeaderConstants.X_SHARER_USER_ID;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @Valid @RequestBody BookingCreateDto dto) {
        return bookingClient.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId) {
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBooker(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getAllByBooker(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getAllByOwner(userId, state);
    }
}