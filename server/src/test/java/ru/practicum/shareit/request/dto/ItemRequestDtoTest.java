package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestInDto> json;

    private final ItemRequestInDto itemRequestInputDTO = ItemRequestInDto.builder()
            .description("Хотел бы воспользоваться щёткой для обуви")
            .build();

    @Test
    public void testJsonDeserialization() throws Exception {
        String content = "{\"description\":\"Хотел бы воспользоваться щёткой для обуви\",\"requesterId\":1}";

        ItemRequestInDto result = json.parse(content).getObject();

        assertThat(result.getDescription()).isEqualTo(itemRequestInputDTO.getDescription());
    }
}
