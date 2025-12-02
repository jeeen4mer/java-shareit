package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.config.HeaderConstants.X_SHARER_USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getById(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId) {
        return itemService.findByIdWithDetails(userId, itemId);
    }

    @GetMapping
    public List<ItemWithBookingsDto> getAllByOwner(
            @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemService.findByOwnerId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId) {
        itemService.delete(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentCreateDto dto) {
        return itemService.addComment(userId, itemId, dto);
    }
}