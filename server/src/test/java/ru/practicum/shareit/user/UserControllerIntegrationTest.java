package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    private UserDto userInputDTO;
    private UserDto userOutputDTO;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Long userId1 = 1L;
    private final Long invalidId = 999L;

    public void setUp() {

        userInputDTO = UserDto.builder()
                .email("RuRu@yandex.ru")
                .name("RuRu")
                .build();
        userOutputDTO = UserDto.builder()
                .id(userId1)
                .email("RuRu@yandex.ru")
                .name("RuRu")
                .build();
    }

    @Test
    @SneakyThrows
    public void testCreateUser_ResulStatusCreated() {
        setUp();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userInputDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userOutputDTO)));
    }

    @Test
    @SneakyThrows
    public void testGetUserById_ResulStatusOk() {
        setUp();

        mvc.perform(get("/users/{userId}", userId1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userOutputDTO)))
                .andReturn();
    }

    @Test
    @SneakyThrows
    public void testGetUserById_WithInvalidId_ResulStatusNotFound() {

        mvc.perform(get("/users/{invalidUserId}", invalidId))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        ValidException.class));
    }

    @Test
    @SneakyThrows
    public void testUpdateUser_WithInvalidId_ResulStatusNotFound() {
        setUp();
        userInputDTO = userInputDTO.toBuilder().name("updateRuRu").build();

        mvc.perform(patch("/users/{userId}", invalidId)
                        .content(mapper.writeValueAsString(userInputDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        ValidException.class));
    }
}