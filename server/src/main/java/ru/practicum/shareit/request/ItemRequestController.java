package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestOutDto addItemRequest(@RequestBody ItemRequestInDto itemRequestInDto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.addItemRequest(itemRequestInDto, userId);
    }

    @GetMapping
    public List<ItemRequestOutDto> getAllMineRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllMineRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestOutDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestOutDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam Integer from,
                                                      @RequestParam Integer size) {
        return itemRequestService.getAllItemRequests(userId, from, size);
    }
}
