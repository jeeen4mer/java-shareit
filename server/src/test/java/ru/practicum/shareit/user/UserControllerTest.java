package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@mail.ru")
                .build();
    }

    @SneakyThrows
    @Test
    void addUserTest() {
        when(userService.addUser(userDto))
                .thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }


    @SneakyThrows
    @Test
    void getUserTest() {
        long userId = 0L;

        when(userService.getUser(userId))
                .thenReturn(userDto);

        String result = mockMvc.perform(get("/users/{userId}", userId)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void getAllUsersTest() {
        when(userService.getAllUsers())
                .thenReturn(List.of(userDto));

        String result = mockMvc.perform(get("/users/")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(userDto)), result);
    }

    @SneakyThrows
    @Test
    void updateUserTest() {
        when(userService.updateUser(userDto, userDto.getId()))
                .thenReturn(userDto);

        String result = mockMvc.perform(patch("/users/{userId}", userDto.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void deleteUserTest() {
        Long userId = 0L;
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUser(userId);

    }
}

