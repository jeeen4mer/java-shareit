package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.constant.Status;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private UserStorage userStorage;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserService userService;

    @Mock
    private BookingStorage bookingStorage;

    @Mock
    private ItemStorage itemStorage;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;


    @Test
    void addBookingTest() {
        Long bookerId = 1L;
        User user = User.builder()
                .id(2L)
                .name("John Doe")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Laptop")
                .available(true)
                .owner(user)
                .build();

        UserDto userDto = UserDto.builder()
                .id(2L)
                .name("John Doe")
                .build();

        ItemResponseDto itemDto = ItemResponseDto.builder()
                .id(1L)
                .name("Laptop")
                .available(true)
                .build();


        BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 1, 1, 23, 0, 0))
                .end(LocalDateTime.of(2025, 1, 2, 23, 0, 0))
                .booker(userDto)
                .item(itemDto)
                .status(Status.WAITING)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 1, 1, 23, 0, 0))
                .end(LocalDateTime.of(2025, 1, 2, 23, 0, 0))
                .booker(user)
                .item(item)
                .status(Status.WAITING)
                .build();

        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 1, 2, 23, 0, 0))
                .end(LocalDateTime.of(2024, 1, 2, 23, 1, 0))
                .status(Status.WAITING)
                .build();


        Mockito.when(userStorage.findById(bookerId)).thenReturn(Optional.of(user));
        Mockito.when(itemStorage.findById(bookingRequestDto.getItemId())).thenReturn(Optional.of(item));
        Mockito.when(bookingMapper.toBooking(bookingRequestDto)).thenReturn(booking);
        Mockito.when(bookingStorage.save(booking)).thenReturn(booking);
        Mockito.when(bookingMapper.toBookingResponseDto(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto result = bookingService.addBooking(bookerId, bookingRequestDto);
        result.setBooker(userDto);
        result.setItem(itemDto);
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(user.getId(), result.getBooker().getId());
        assertEquals(Status.WAITING, result.getStatus());
    }

    @Test
    public void testAddBookingRequest_userNotFoundTest() {
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 1, 2, 23, 0, 0))
                .end(LocalDateTime.of(2024, 1, 2, 23, 1, 0))
                .status(Status.WAITING)
                .build();
        Long userId = 100L;
        when(userStorage.findById(userId)).thenReturn(Optional.empty());
        assertThrows(ValidException.class, () -> bookingService.addBooking(100L, bookingRequestDto));
    }

    @Test
    void updateBookingTest() {
        Long userId = 2L;
        Long bookingId = 1L;

        User user = User.builder()
                .id(2L)
                .name("John Doe")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Laptop")
                .available(true)
                .owner(user)
                .build();

        UserDto userDto = UserDto.builder()
                .id(2L)
                .name("John Doe")
                .build();

        ItemResponseDto itemDto = ItemResponseDto.builder()
                .id(1L)
                .name("Laptop")
                .available(true)
                .build();


        BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 1, 1, 23, 0, 0))
                .end(LocalDateTime.of(2025, 1, 2, 23, 0, 0))
                .booker(userDto)
                .item(itemDto)
                .status(Status.WAITING)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 1, 1, 23, 0, 0))
                .end(LocalDateTime.of(2025, 1, 2, 23, 0, 0))
                .booker(user)
                .item(item)
                .status(Status.WAITING)
                .build();

        Mockito.when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));
        Mockito.when(bookingStorage.save(booking)).thenReturn(booking);
        Mockito.when(bookingMapper.toBookingResponseDto(booking)).thenReturn(bookingResponseDto);
        BookingResponseDto result = bookingService.updateBooking(userId, bookingId, true);
        result.setStatus(Status.APPROVED);
        assertEquals(Status.APPROVED, result.getStatus());
        result.setStatus(Status.REJECTED);
        assertEquals(Status.REJECTED, result.getStatus());

    }

    @Test
    void getBookingTest() {
        Long userId = 1L;
        Long bookingId = 1L;

        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Laptop")
                .available(true)
                .owner(user)
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .build();

        ItemResponseDto itemDto = ItemResponseDto.builder()
                .id(1L)
                .name("Laptop")
                .available(true)
                .build();


        BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 1, 1, 23, 0, 0))
                .end(LocalDateTime.of(2025, 1, 2, 23, 0, 0))
                .booker(userDto)
                .item(itemDto)
                .status(Status.WAITING)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 1, 1, 23, 0, 0))
                .end(LocalDateTime.of(2025, 1, 2, 23, 0, 0))
                .booker(user)
                .item(item)
                .status(Status.WAITING)
                .build();

        Mockito.when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));
        Mockito.when(bookingMapper.toBookingResponseDto(booking)).thenReturn(bookingResponseDto);
        BookingResponseDto result = bookingService.getBooking(userId, bookingId);
        assertEquals(booking.getId(), result.getId());

    }

    @Test
    void getBooking_UserNotRelatedToBooking_ExceptionThrown() {
        Long userId = 1L;
        Long bookingId = 3L;
        Booking booking = mock(Booking.class);
        User booker = mock(User.class);
        Item item = mock(Item.class);
        User owner = mock(User.class);

        when(booking.getBooker()).thenReturn(booker);
        when(booker.getId()).thenReturn(2L);
        when(booking.getItem()).thenReturn(item);
        when(item.getOwner()).thenReturn(owner);
        when(owner.getId()).thenReturn(3L);
        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ValidException.class, () -> bookingService.getBooking(userId, bookingId));
    }

    @Test
    void getBookingsOfBooker_InvalidBookerId_ExceptionThrown() {
        Long bookerId = 2L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 5;

        when(userStorage.existsById(bookerId)).thenReturn(false);

        assertThrows(ValidException.class, () -> bookingService.getBookingsOfBooker(bookerId, state, from, size));

    }

    @Test
    public void getBookingsOfBooker_InvalidState_ExceptionThrown() {
        Long bookerId = 1L;
        String state = "INVALID_STATE";
        Integer from = 0;
        Integer size = 5;

        when(userStorage.existsById(bookerId)).thenReturn(true);


        assertThrows(ValidException.class, () -> bookingService.getBookingsOfBooker(bookerId, state, from, size));
    }

    @Test
    void updateBooking_AlreadyApproved_ExceptionThrown() {
        Long userId = 1L;
        Long bookingId = 4L;
        Boolean approved = true;
        Booking booking = mock(Booking.class);
        Item item = mock(Item.class);
        User owner = mock(User.class);

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));
        when(booking.getItem()).thenReturn(item);
        when(item.getOwner()).thenReturn(owner);
        when(owner.getId()).thenReturn(userId);
        when(booking.getStatus()).thenReturn(Status.APPROVED);

        assertThrows(ValidException.class, () -> bookingService.updateBooking(userId, bookingId, approved));
    }
}
