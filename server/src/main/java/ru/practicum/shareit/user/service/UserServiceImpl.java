package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserStorage userStorage;

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        return userMapper.toUserDto(userStorage.save(userMapper.fromUserDto(userDto)));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new ValidException("Пользователя с таким id не существует", HttpStatus.NOT_FOUND));
        userDto.setId(userId);
        if (userDto.getEmail() == null) {
            userDto.setEmail(user.getEmail());
        }
        if (userDto.getName() == null) {
            userDto.setName(user.getName());
        }
        return userMapper.toUserDto(userStorage.save(userMapper.fromUserDto(userDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUser(Long userId) {
        return userMapper.toUserDto(userStorage.findById(userId).orElseThrow(() ->
                new ValidException("Пользователя с таким id не существует", HttpStatus.NOT_FOUND)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userMapper.toUserDtoList(userStorage.findAll());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userStorage.deleteById(userId);
    }
}

