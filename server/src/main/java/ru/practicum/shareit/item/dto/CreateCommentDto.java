package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentDto {
    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(max = 1000, message = "Текст комментария не может превышать 1000 символов")
    private String text;
}