package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceIntegrationTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createShouldSaveUserWhenEmailIsUniqueTest() {
        UserDto dto = new UserDto();
        dto.setName("John");
        dto.setEmail("john@example.com");

        User userToSave = UserMapper.toUser(dto);
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("John");
        savedUser.setEmail("john@example.com");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(userToSave)).thenReturn(savedUser);

        UserDto result = userService.create(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John");
        verify(userRepository).save(userToSave);
    }

    @Test
    void createShouldThrowDuplicateEmailExceptionTest() {
        UserDto dto = new UserDto();
        dto.setEmail("exists@test.com");

        when(userRepository.existsByEmail("exists@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(dto))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("Email уже используется");
    }

    @Test
    void updateShouldUpdateNameOnlyTest() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");

        UserDto updateDto = new UserDto();
        updateDto.setName("New Name");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        UserDto result = userService.update(userId, updateDto);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getEmail()).isEqualTo("old@example.com");
        verify(userRepository, never()).existsByEmail(anyString());
    }

    @Test
    void updateShouldUpdateEmailTest() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John");
        existingUser.setEmail("old@example.com");

        UserDto updateDto = new UserDto();
        updateDto.setEmail("new@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        UserDto result = userService.update(userId, updateDto);

        assertThat(result.getEmail()).isEqualTo("new@example.com");
        verify(userRepository).existsByEmail("new@example.com");
    }

    @Test
    void updateShouldThrowDuplicateEmailExceptionTest() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@example.com");

        UserDto updateDto = new UserDto();
        updateDto.setEmail("taken@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.update(userId, updateDto))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("Email уже используется");
    }

    @Test
    void updateShouldNotCheckEmailExistenceTest() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("same@example.com");

        UserDto updateDto = new UserDto();
        updateDto.setEmail("same@example.com");
        updateDto.setName("New Name");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        userService.update(userId, updateDto);

        verify(userRepository, never()).existsByEmail(anyString());
    }

    @Test
    void getByIdShouldReturnUserDtoTest() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("John");
        user.setEmail("john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.getById(userId);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John");
    }

    @Test
    void getByIdShouldThrowNotFoundExceptionTest() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден: " + userId);
    }

    @Test
    void deleteShouldDeleteUserTest() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.delete(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteShouldThrowNotFoundExceptionTest() {
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден: " + userId);
    }
}