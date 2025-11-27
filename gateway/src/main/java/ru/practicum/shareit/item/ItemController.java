package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@Valid @RequestBody CreateItemDto createItemDto,
                              @RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemService.createItem(createItemDto, ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Long id,
                               @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.getItemById(id, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwnerId(@RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemService.getItemsByOwnerId(ownerId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable Long id,
                              @RequestBody UpdateItemDto updateItemDto,
                              @RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemService.updateItem(id, updateItemDto, ownerId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(required = false) String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @Valid @RequestBody CreateCommentDto createCommentDto,
                                 @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.addComment(itemId, createCommentDto, userId);
    }
}