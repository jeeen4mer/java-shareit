package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.dto.CommentCreateDto;
import ru.practicum.shareit.dto.ItemDto;

import static ru.practicum.shareit.config.HeaderConstants.X_SHARER_USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @Valid @RequestBody ItemDto itemDto) {
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId) {
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(
            @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemClient.getAllByOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        return itemClient.search(text);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delete(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId) {
        return itemClient.delete(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentCreateDto dto) {
        return itemClient.addComment(userId, itemId, dto);
    }
}