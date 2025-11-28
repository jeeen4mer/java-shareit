package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.CreateUserDto;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_ShouldCreateAndReturnUser() {
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setName("John");
        createUserDto.setEmail("john@example.com");

        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");

        UserDto expectedDto = new UserDto();
        expectedDto.setId(1L);
        expectedDto.setName("John");
        expectedDto.setEmail("john@example.com");

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(createUserDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getName());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("John Updated");
        userDto.setEmail("john.updated@example.com");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John");
        existingUser.setEmail("john@example.com");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john.updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(userId, userDto);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("John Updated", result.getName());
        assertEquals("john.updated@example.com", result.getEmail());
    }

    @Test
    void updateUser_WhenUserNotFound_ShouldThrowException() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("John Updated");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateUser(userId, userDto));
    }

    @Test
    void updateUser_ShouldUpdateOnlyName() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("John Updated");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John");
        existingUser.setEmail("john@example.com");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(userId, userDto);

        assertNotNull(result);
        assertEquals("John Updated", result.getName());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    void updateUser_ShouldUpdateOnlyEmail() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setEmail("john.updated@example.com");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John");
        existingUser.setEmail("john@example.com");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("John");
        updatedUser.setEmail("john.updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(userId, userDto);

        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals("john.updated@example.com", result.getEmail());
    }

    @Test
    void getUserById_ShouldReturnUser() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("John");
        user.setEmail("john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("John", result.getName());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    void getUserById_WhenUserNotFound_ShouldThrowException() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(userId));
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("John");
        user1.setEmail("john@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane");
        user2.setEmail("jane@example.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void getAllUsers_WhenNoUsers_ShouldReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_WhenUserNotFound_ShouldThrowException() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.deleteUser(userId));
    }
}