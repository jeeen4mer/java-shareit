package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;


@Validated
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestBody @Valid ItemRequestDto itemRequestDto) {
        itemRequestDto.setOwnerId(userId);
        return itemClient.addItem(userId, itemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemRequestDto itemRequestDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId) {
        return itemClient.updateItem(itemRequestDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsOfOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                  @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return itemClient.getItemsOfOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchForTheItem(@RequestParam String text,
                                                   @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                   @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return itemClient.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody @Valid CommentRequestDto commentRequestDto) {
        return itemClient.addComment(userId, itemId, commentRequestDto);
    }
}
