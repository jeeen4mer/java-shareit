package ru.practicum.shareit;

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
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreateBookingDtoJsonTest {

    @Autowired
    private JacksonTester<CreateBookingDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testSerialization() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        CreateBookingDto dto = new CreateBookingDto(1L, start, end);

        JsonContent<CreateBookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).hasJsonPathValue("$.start");
        assertThat(result).hasJsonPathValue("$.end");
    }

    @Test
    void testDeserialization() throws JsonProcessingException {
        String jsonContent = "{\"itemId\": 1, \"start\": \"2024-01-01T10:00:00\", \"end\": \"2024-01-02T10:00:00\"}";

        CreateBookingDto dto = objectMapper.readValue(jsonContent, CreateBookingDto.class);

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 2, 10, 0));
    }

    @Test
    void whenItemIdIsNull_ShouldFailValidation() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        CreateBookingDto dto = new CreateBookingDto(null, start, end);

        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("ID вещи не может быть пустым");
    }

    @Test
    void whenStartIsInPast_ShouldFailValidation() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        CreateBookingDto dto = new CreateBookingDto(1L, start, end);

        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Дата начала должна быть в настоящем или будущем");
    }

    @Test
    void whenEndIsNotInFuture_ShouldFailValidation() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now();
        CreateBookingDto dto = new CreateBookingDto(1L, start, end);

        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Дата окончания должна быть в будущем");
    }
}