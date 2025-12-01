package ru.practicum.shareit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class DtoValidationJsonTest {

    @Autowired
    private JacksonTester<UserDto> userJson;

    @Autowired
    private JacksonTester<ItemDto> itemJson;

    @Autowired
    private JacksonTester<BookingCreateDto> bookingJson;

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime tomorrow = now.plusDays(1);

    @Test
    void userDtoBlankNameShouldHaveViolationsTest() throws Exception {
        String json = "{ \"name\": \"   \", \"email\": \"john@example.com\" }";

        UserDto dto = userJson.parseObject(json);

        assertThat(validator.validate(dto))
                .hasSize(1)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("name");
    }

    @Test
    void userDtoInvalidEmailShouldHaveViolationsTest() throws Exception {
        String json = "{ \"name\": \"John\", \"email\": \"not-an-email\" }";

        UserDto dto = userJson.parseObject(json);

        assertThat(validator.validate(dto))
                .hasSize(1)
                .extracting(ConstraintViolation::getMessage)
                .contains("Email должен быть валидным");
    }

    @Test
    void itemDtoBlankNameShouldHaveViolationsTest() throws Exception {
        String json = "{ \"name\": \"\", \"description\": \"Хорошая дрель\", \"available\": true }";

        ItemDto dto = itemJson.parseObject(json);

        assertThat(validator.validate(dto))
                .hasSize(1)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("name");
    }

    @Test
    void itemDtoNullAvailableShouldHaveViolationsTest() throws Exception {
        String json = "{ \"name\": \"Дрель\", \"description\": \"Мощная\", \"available\": null }";

        ItemDto dto = itemJson.parseObject(json);

        assertThat(validator.validate(dto))
                .hasSize(1)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("available");
    }

    @Test
    void bookingCreateDtoEndNotInFutureShouldHaveViolationsTest() throws Exception {
        String json = "{ \"itemId\": 1, \"start\": \"" + tomorrow + "\", \"end\": \"" + now + "\" }";

        BookingCreateDto dto = bookingJson.parseObject(json);

        assertThat(validator.validate(dto))
                .isNotEmpty()
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("end");
    }

    @Test
    void bookingCreateDtoNullItemIdShouldHaveViolationsTest() throws Exception {
        String json = "{ \"itemId\": null, \"start\": \"" + tomorrow + "\", \"end\": \"" + tomorrow.plusDays(1) + "\" }";

        BookingCreateDto dto = bookingJson.parseObject(json);

        assertThat(validator.validate(dto))
                .hasSize(1)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("itemId");
    }

    @Test
    void validBookingCreateDtoShouldHaveNoViolationsTest() throws Exception {
        String json = "{ \"itemId\": 999, \"start\": \"" + tomorrow + "\", \"end\": \"" + tomorrow.plusDays(1) + "\" }";

        BookingCreateDto dto = bookingJson.parseObject(json);

        assertThat(validator.validate(dto)).isEmpty();
    }
}
