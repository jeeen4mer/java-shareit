package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createRequest(@Valid @RequestBody CreateItemRequestDto createItemRequestDto,
                                        @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.createRequest(createItemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherUsersRequests(@RequestHeader(USER_ID_HEADER) Long userId,
                                                      @RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "10") int size) {
        return itemRequestService.getOtherUsersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable Long requestId,
                                         @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.getRequestById(requestId, userId);
    }
}