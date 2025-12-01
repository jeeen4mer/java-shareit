package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestInDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestsClient itemRequestsClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestBody @Valid ItemRequestInDto itemRequestInDto,
                                                 @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestsClient.addItemRequest(itemRequestInDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllMineRequests(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestsClient.getAllMineRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @Positive @PathVariable Long requestId) {
        return itemRequestsClient.getItemRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                     @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return itemRequestsClient.getAllItemRequests(userId, from, size);
    }
}
