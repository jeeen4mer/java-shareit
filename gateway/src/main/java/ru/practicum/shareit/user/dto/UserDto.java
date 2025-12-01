package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import static ru.practicum.shareit.constant.Constant.REGEX_EMAIL;

@Data
@Builder(toBuilder = true)
public class UserDto {
    @Positive
    private Long id;
    @Size(max = 30, min = 1, message = "Максимальная длина имени - 30 символов")
    private String name;
    @NotEmpty
    @Email(regexp = REGEX_EMAIL, message = "В 'email' использованы запрещённые символы")
    private String email;

    public UserDto(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
