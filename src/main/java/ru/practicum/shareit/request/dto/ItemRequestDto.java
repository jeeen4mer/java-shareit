package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    private LocalDateTime created;
    private List<ResponseItem> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseItem {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long requestId;
        private Long ownerId;
    }
}