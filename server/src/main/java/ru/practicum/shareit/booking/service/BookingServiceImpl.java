package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDto create(Long userId, BookingCreateDto dto) {
        User booker = getUser(userId);
        Item item = getItem(dto.getItemId());

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Владелец не может бронировать свою вещь");
        }

        Booking booking = Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        return toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = getBooking(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Только владелец может подтвердить бронирование");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BookingAlreadyProcessedException("Бронирование уже обработано");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return toBookingDto(booking);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        getUser(userId);
        Booking booking = getBooking(bookingId);
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Доступ только автору или владельцу");
        }
        return toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByBooker(Long bookerId, BookingState state) {
        getUser(bookerId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;

        if (state == null || state == BookingState.ALL) {
            bookings = bookingRepository.findByBookerId(bookerId, sort);
        } else if (state == BookingState.CURRENT) {
            bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(bookerId, now, now, sort);
        } else if (state == BookingState.PAST) {
            bookings = bookingRepository.findByBookerIdAndEndBefore(bookerId, now, sort);
        } else if (state == BookingState.FUTURE) {
            bookings = bookingRepository.findByBookerIdAndStartAfter(bookerId, now, sort);
        } else if (state == BookingState.WAITING) {
            bookings = bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.WAITING, sort);
        } else if (state == BookingState.REJECTED) {
            bookings = bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, sort);
        } else {
            throw new UnsupportedStateException("Unknown state: " + state);
        }

        return bookings.reversed().stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllByOwner(Long ownerId, BookingState state) {
        getUser(ownerId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;

        if (state == null || state == BookingState.ALL) {
            bookings = bookingRepository.findByItem_Owner_Id(ownerId, sort);
        } else if (state == BookingState.CURRENT) {
            bookings = bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfter(ownerId, now, now, sort);
        } else if (state == BookingState.PAST) {
            bookings = bookingRepository.findByItem_Owner_IdAndEndBefore(ownerId, now, sort);
        } else if (state == BookingState.FUTURE) {
            bookings = bookingRepository.findByItem_Owner_IdAndStartAfter(ownerId, now, sort);
        } else if (state == BookingState.WAITING) {
            bookings = bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.WAITING, sort);
        } else if (state == BookingState.REJECTED) {
            bookings = bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.REJECTED, sort);
        } else {
            throw new UnsupportedStateException("Unknown state: " + state);
        }

        return bookings.reversed().stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + id));
    }

    private Item getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: " + id));
    }

    private Booking getBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено: " + id));
    }
}