package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto createBooking(CreateBookingDto createBookingDto, Long userId) {

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Пользователь не найден"));

        Item item = itemRepository.findById(createBookingDto.getItemId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Вещь не найдена"));

        if (item.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(NOT_FOUND, "Пользователь не может забронировать свою вещь");
        }

        if (!item.getAvailable()) {
            throw new ResponseStatusException(NOT_FOUND, "Вещь недоступна для бронирования");
        }

        if (createBookingDto.getStart().isAfter(createBookingDto.getEnd()) ||
                createBookingDto.getStart().isEqual(createBookingDto.getEnd())) {
            throw new ResponseStatusException(NOT_FOUND, "Некорректные даты бронирования");
        }

        Booking booking = new Booking();
        booking.setStart(createBookingDto.getStart());
        booking.setEnd(createBookingDto.getEnd());
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto approveBooking(Long bookingId, Boolean approved, Long userId) {

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Пользователь не найден"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Бронирование не найдено"));

        if (!booking.getItem().getOwnerId().equals(userId)) {
            throw new ResponseStatusException(NOT_FOUND, "Пользователь не является владельцем вещи");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ResponseStatusException(NOT_FOUND, "Бронирование уже подтверждено или отклонено");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking updatedBooking = bookingRepository.save(booking);

        return bookingMapper.toBookingDto(updatedBooking);
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Пользователь не найден"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Бронирование не найдено"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwnerId().equals(userId)) {
            throw new ResponseStatusException(NOT_FOUND, "Пользователь не является владельцем вещи или бронирующим");
        }

        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, BookingState state, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Пользователь не найден"));

        List<Booking> bookings;
        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            case ALL:
            default:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
        }

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long userId, BookingState state, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Пользователь не найден"));

        List<Booking> bookings;
        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            case ALL:
            default:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
                break;
        }

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}