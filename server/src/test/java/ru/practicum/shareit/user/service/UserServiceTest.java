package ru.practicum.shareit.user.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserStorage userStorage;

    @Mock
    private UserMapper userMapper;

    private User user;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("vvvvv")
                .email("vvvvv@vvvvvvv.ru")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("vvvvv")
                .email("vvvvv@vvvvvvv.ru")
                .build();
    }

    @AfterEach
    void afterFinish() {
        userStorage.deleteAll();
    }

    @Test
    @SneakyThrows
    void addUserTest() {
        when(userMapper.fromUserDto(userDto)).thenReturn(user);
        when(userStorage.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);
        UserDto result = userService.addUser(userDto);
        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
    }

    @Test
    public void testUpdateUser_userNotFoundTest() {
        Long userId = 100L;
        when(userStorage.findById(userId)).thenReturn(Optional.empty());
        assertThrows(ValidException.class, () -> userService.updateUser(userDto, userId));
    }

    @Test
    void updateUserDTO_existingUserTest() {
        UserDto userrr = new UserDto(1L, null, null);

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(userStorage.save(any())).thenReturn(user);
        when(userMapper.fromUserDto(any())).thenReturn(user);
        when(userMapper.toUserDto(any())).thenReturn(userDto);


        UserDto updatedUserDTO = userService.updateUser(userrr, 1L);

        assertThat(updatedUserDTO.getName()).isEqualTo(user.getName());
        assertThat(updatedUserDTO.getEmail()).isEqualTo(user.getEmail());
        verify(userStorage, times(1)).findById(1L);
        verify(userMapper, times(1)).fromUserDto(userrr);
        verify(userStorage, times(1)).save(user);
    }

    @Test
    @SneakyThrows
    void getUserTest() {
        when(userStorage.findById(userDto.getId())).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);
        UserDto result = userService.getUser(userDto.getId());
        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
    }

    @Test
    void testGetUser_userNotFoundTest() {
        Long userId = 1L;
        when(userStorage.findById(userId)).thenReturn(Optional.empty());
        assertThrows(ValidException.class, () -> userService.getUser(userId));
    }

    @Test
    @SneakyThrows
    void getAllUsersTest() {
        when(userStorage.findAll()).thenReturn(List.of(user));
        when(userMapper.toUserDtoList(List.of(user))).thenReturn(List.of(userDto));
        List<UserDto> users = List.of(userDto);
        assertNotNull(users);
        assertEquals(users, userService.getAllUsers());
    }

    @Test
    @SneakyThrows
    void deleteUserTest() {
        userService.deleteUser(user.getId());
    }
}
