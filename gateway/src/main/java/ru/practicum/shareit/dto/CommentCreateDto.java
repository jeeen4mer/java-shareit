package ru.practicum.shareit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateDto {
    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(min = 1, max = 4096, message = "Комментарий должен быть от 1 до 4096 символов")
    private String text;
}