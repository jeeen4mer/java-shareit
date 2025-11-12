package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingInfo lastBooking;
    private BookingInfo nextBooking;
    private List<CommentDto> comments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingInfo {
        private Long id;
        private Long bookerId;
        private LocalDateTime start;
        private LocalDateTime end;
    }
}