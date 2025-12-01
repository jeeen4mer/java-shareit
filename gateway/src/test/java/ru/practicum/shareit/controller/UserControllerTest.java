package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.UserClient;
import ru.practicum.shareit.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserClient userClient;

    @Test
    void shouldCreateUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("John");
        userDto.setEmail("john@example.com");

        when(userClient.create(any())).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void shouldReturn400WhenEmailBlank() throws Exception {
        UserDto invalid = new UserDto();
        invalid.setName("John");
        invalid.setEmail("   ");

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(invalid))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenEmailInvalid() throws Exception {
        UserDto invalid = new UserDto();
        invalid.setEmail("not-an-email");

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(invalid))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldGetUserById() throws Exception {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");

        when(userClient.getById(1L)).thenReturn(ResponseEntity.ok(user));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        when(userClient.getAll()).thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteUser() throws Exception {
        when(userClient.delete(1L)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    private UserDto userDto() {
        UserDto dto = new UserDto();
        dto.setName("Test");
        dto.setEmail("test@example.com");
        return dto;
    }
}