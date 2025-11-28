package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateItemRequestDto {
    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CreateItemRequestDto(@JsonProperty("description") String description) {
        this.description = description;
    }
}