package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemRequestClient;
import ru.practicum.shareit.dto.ItemRequestCreateDto;

import static ru.practicum.shareit.config.HeaderConstants.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(X_SHARER_USER_ID) Long requestorId,
            @Valid @RequestBody ItemRequestCreateDto dto) {
        return itemRequestClient.create(requestorId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwn(
            @RequestHeader(X_SHARER_USER_ID) Long requestorId) {
        return itemRequestClient.getOwnRequests(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long requestId) {
        return itemRequestClient.getById(userId, requestId);
    }
}