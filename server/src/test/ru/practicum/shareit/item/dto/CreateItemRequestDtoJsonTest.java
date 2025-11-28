package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreateItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<CreateItemRequestDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testSerialization() throws Exception {
        CreateItemRequestDto dto = new CreateItemRequestDto("Нужна дрель");

        JsonContent<CreateItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Нужна дрель");
    }

    @Test
    void testDeserialization() throws JsonProcessingException {
        String jsonContent = "{\"description\": \"Нужна дрель\"}";

        CreateItemRequestDto dto = objectMapper.readValue(jsonContent, CreateItemRequestDto.class);

        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
    }

    @Test
    void whenDescriptionIsBlank_ShouldFailValidation() {
        CreateItemRequestDto dto = new CreateItemRequestDto("");

        Set<ConstraintViolation<CreateItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Описание не может быть пустым");
    }

    @Test
    void whenDescriptionIsNull_ShouldFailValidation() {
        CreateItemRequestDto dto = new CreateItemRequestDto(null);

        Set<ConstraintViolation<CreateItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Описание не может быть пустым");
    }

    @Test
    void whenDescriptionIsValid_ShouldPassValidation() {
        CreateItemRequestDto dto = new CreateItemRequestDto("Valid description");

        Set<ConstraintViolation<CreateItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}