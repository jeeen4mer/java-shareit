package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestDto {
    @Positive
    private Long id;
    @Size(min = 1, max = 50)
    @NotEmpty
    private String name;
    @NotEmpty
    @Size(min = 1, max = 50)
    private String description;
    @NotNull
    private Boolean available;
    private Long ownerId;
    private Long requestId;
}