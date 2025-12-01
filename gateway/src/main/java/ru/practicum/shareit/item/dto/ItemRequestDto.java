package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

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