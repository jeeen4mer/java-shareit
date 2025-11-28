package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBooking_ShouldReturnBookingDto() {
        CreateBookingDto createDto = new CreateBookingDto(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        Long userId = 1L;

        ru.practicum.shareit.user.User booker = new ru.practicum.shareit.user.User();
        booker.setId(userId);
        booker.setName("Booker");
        booker.setEmail("booker@example.com");

        ru.practicum.shareit.item.Item item = new ru.practicum.shareit.item.Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwnerId(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(createDto.getStart());
        booking.setEnd(createDto.getEnd());
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        BookingDto expectedDto = new BookingDto();
        expectedDto.setId(1L);
        expectedDto.setStart(createDto.getStart());
        expectedDto.setEnd(createDto.getEnd());

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(createDto.getItemId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(expectedDto);

        BookingDto result = bookingService.createBooking(createDto, userId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void createBooking_WhenUserNotFound_ShouldThrowException() {
        CreateBookingDto createDto = new CreateBookingDto(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        Long userId = 1L;

        lenient().when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> bookingService.createBooking(createDto, userId));
    }

    @Test
    void createBooking_WhenItemNotFound_ShouldThrowException() {
        CreateBookingDto createDto = new CreateBookingDto(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        Long userId = 1L;

        ru.practicum.shareit.user.User user = new ru.practicum.shareit.user.User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(createDto.getItemId())).thenReturn(Optional.empty());

        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> bookingService.createBooking(createDto, userId));
    }

    @Test
    void createBooking_WhenUserIsOwner_ShouldThrowException() {
        CreateBookingDto createDto = new CreateBookingDto(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        Long userId = 1L;

        ru.practicum.shareit.user.User user = new ru.practicum.shareit.user.User();
        user.setId(userId);
        user.setName("Owner");
        user.setEmail("owner@example.com");

        ru.practicum.shareit.item.Item item = new ru.practicum.shareit.item.Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwnerId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(createDto.getItemId())).thenReturn(Optional.of(item));

        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> bookingService.createBooking(createDto, userId));
    }

    @Test
    void getBookingById_WhenUserNotFound_ShouldThrowException() {
        Long bookingId = 1L;
        Long userId = 1L;

        lenient().when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> bookingService.getBookingById(bookingId, userId));
    }

    @Test
    void getBookingById_WhenBookingNotFound_ShouldThrowException()  {
        Long bookingId = 1L;
        Long userId = 1L;

        ru.practicum.shareit.user.User user = new ru.practicum.shareit.user.User();
        user.setId(userId);

        lenient().when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        lenient().when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> bookingService.getBookingById(bookingId, userId));
    }
}