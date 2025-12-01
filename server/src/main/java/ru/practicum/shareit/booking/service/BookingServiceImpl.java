package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.constant.State;
import ru.practicum.shareit.constant.Status;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.constant.Status.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final BookingStorage bookingStorage;
    private final BookingMapper bookingMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;


    @Override
    @Transactional
    public BookingResponseDto addBooking(Long bookerId, BookingRequestDto bookingRequestDto) {
        User user = userStorage.findById(bookerId).orElseThrow(() ->
                new ValidException("Пользователя с таким id не существует", HttpStatus.NOT_FOUND));

        Item item = itemStorage.findById(bookingRequestDto.getItemId()).orElseThrow(() ->
                new ValidException("Предмета с таким id не существует", HttpStatus.NOT_FOUND));
        if (item.getAvailable().equals(false)) {
            throw new ValidException("Предмет недоступен для аренды", HttpStatus.BAD_REQUEST);
        }
        if (item.getOwner().getId().equals(bookerId)) {
            throw new ValidException("Владелец не имеет права бронировать свои же вещи", HttpStatus.NOT_FOUND);
        }
        validDate(bookingRequestDto);
        bookingRequestDto.setStatus(Status.WAITING);
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setBookerId(user.getId());
        BookingResponseDto bookingResponseDto = bookingMapper.toBookingResponseDto(bookingStorage.save(bookingMapper.toBooking(bookingRequestDto)));
        bookingResponseDto.setItem(itemMapper.toItemResponseDto(item));
        bookingResponseDto.setBooker(userMapper.toUserDto(user));
        return bookingResponseDto;
    }

    @Override
    @Transactional
    public BookingResponseDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(() ->
                new ValidException("Пользователя с таким id не существует", HttpStatus.NOT_FOUND));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidException("Вы не имеете права подтверждать заявку на бронирование", HttpStatus.NOT_FOUND);
        }
        if (booking.getStatus().equals(APPROVED)) {
            throw new ValidException("Предмет уже взят в аренду", HttpStatus.BAD_REQUEST);
        }
        if (approved.equals(true) && booking.getStatus().equals(WAITING)) {
            booking.setStatus(APPROVED);
            return bookingMapper.toBookingResponseDto(bookingStorage.save(booking));
        }
        if (approved.equals(false) && booking.getStatus().equals(WAITING)) {
            booking.setStatus(REJECTED);
            return bookingMapper.toBookingResponseDto(bookingStorage.save(booking));
        }
        return bookingMapper.toBookingResponseDto(bookingStorage.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(() ->
                new ValidException("Бронирования с таким id не существует", HttpStatus.NOT_FOUND));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return bookingMapper.toBookingResponseDto(booking);
        } else {
            throw new ValidException("Вы должны являться либо арендатором, либо арендодателем", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsOfOwner(Long ownerId, String state, Integer from, Integer size) {
        if (!userStorage.existsById(ownerId)) {
            throw new ValidException("Пользователя с таким id нет", HttpStatus.NOT_FOUND);
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        State bookingState;
        try {
            bookingState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidException(String.format("Unknown state: %s", state), HttpStatus.BAD_REQUEST);
        }
        LocalDateTime now = LocalDateTime.now();
        switch (bookingState) {
            case ALL:
                return bookingMapper.toBookingResponseListDto(
                        bookingStorage.findAllByItem_Owner_Id(ownerId, pageable).getContent());
            case CURRENT:
                return bookingMapper.toBookingResponseListDto(
                        bookingStorage.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(ownerId, now, now, pageable).getContent());
            case PAST:
                return bookingMapper.toBookingResponseListDto(
                        bookingStorage.findAllByItem_Owner_IdAndEndIsBefore(ownerId, now, pageable).getContent());
            case FUTURE:
                return bookingMapper.toBookingResponseListDto(
                        bookingStorage.findAllByItem_Owner_IdAndStartIsAfter(ownerId, now, pageable).getContent());
            case WAITING:
                return bookingMapper.toBookingResponseListDto(
                        bookingStorage.findAllByItem_Owner_IdAndStatus(ownerId, WAITING, pageable).getContent());
            case REJECTED:
                return bookingMapper.toBookingResponseListDto(
                        bookingStorage.findAllByItem_Owner_IdAndStatus(ownerId, REJECTED, pageable).getContent());
            default:
                throw new ValidException(String.format("Unknown state: %s", state), HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsOfBooker(Long bookerId, String state, Integer from, Integer size) {
        if (!userStorage.existsById(bookerId)) {
            throw new ValidException("Пользователя с таким id нет", HttpStatus.NOT_FOUND);
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        State bookingState;
        try {
            bookingState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidException(String.format("Unknown state: %s", state), HttpStatus.BAD_REQUEST);
        }
        LocalDateTime now = LocalDateTime.now();
        switch (bookingState) {
            case ALL:
                return bookingMapper.toBookingResponseListDto(
                        bookingStorage.findAllByBooker_Id(bookerId, pageable).getContent());
            case CURRENT:
                return bookingMapper.toBookingResponseListDto(
                        bookingStorage.findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(bookerId, now, now, pageable).getContent());
            case PAST:
                return bookingMapper.toBookingResponseListDto(
                        bookingStorage.findAllByBooker_IdAndEndIsBefore(bookerId, now, pageable).getContent());
            case FUTURE:
                return bookingMapper.toBookingResponseListDto(
                        bookingStorage.findAllByBooker_IdAndStartIsAfter(bookerId, now, pageable).getContent());
            case WAITING:
                return bookingMapper.toBookingResponseListDto(
                        bookingStorage.findAllByBooker_IdAndStatus(bookerId, WAITING, pageable).getContent());
            case REJECTED:
                return bookingMapper.toBookingResponseListDto(
                        bookingStorage.findAllByBooker_IdAndStatus(bookerId, REJECTED, pageable).getContent());
            default:
                throw new ValidException(String.format("Unknown state: %s", state), HttpStatus.BAD_REQUEST);
        }
    }

    private void validDate(BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getEnd().isBefore(bookingRequestDto.getStart())) {
            throw new ValidException("Время начала аренды не может быть позже, чем время ее окончания", HttpStatus.BAD_REQUEST);
        } else if (bookingRequestDto.getEnd().equals(bookingRequestDto.getStart())) {
            throw new ValidException("Время аренды не может быть равно нулю", HttpStatus.BAD_REQUEST);
        }
    }
}
