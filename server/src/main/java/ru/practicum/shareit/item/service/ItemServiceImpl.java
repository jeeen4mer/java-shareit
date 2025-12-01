package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.constant.Status.APPROVED;
import static ru.practicum.shareit.constant.Status.REJECTED;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;
    private final UserStorage userStorage;
    private final UserMapper userMapper;
    private final BookingStorage bookingStorage;
    private final BookingMapper bookingMapper;
    private final CommentStorage commentStorage;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemResponseDto addItem(Long userId, ItemRequestDto itemRequestDto) {
        if (!userStorage.existsById(userId)) {
            throw new ValidException("Такого пользователя нет", HttpStatus.NOT_FOUND);
        }
        return itemMapper.toItemResponseDto(itemStorage.save(itemMapper.toItemFromRequest(itemRequestDto)));
    }


    @Override
    @Transactional
    public ItemResponseDto updateItem(ItemRequestDto itemRequestDto, Long userId, Long itemId) {
        if (itemRequestDto == null) {
            throw new ValidException("Переданы неверные данные", HttpStatus.BAD_REQUEST);
        }
        Item item = itemStorage.findById(itemId).orElseThrow(() ->
                new ValidException("Предмета с таким id не существует", HttpStatus.NOT_FOUND));

        if (!userId.equals(item.getOwner().getId())) {
            throw new ValidException("Вы не имеете права редактировать данный предмет", HttpStatus.FORBIDDEN);
        }
        itemRequestDto.setId(item.getId());
        itemRequestDto.setOwnerId(userId);
        if (Objects.isNull(itemRequestDto.getDescription())) {
            itemRequestDto.setDescription(item.getDescription());
        }
        if (Objects.isNull(itemRequestDto.getName())) {
            itemRequestDto.setName(item.getName());
        }
        if (Objects.isNull(itemRequestDto.getAvailable())) {
            itemRequestDto.setAvailable(item.getAvailable());
        }

        return itemMapper.toItemResponseDto(itemStorage.save(itemMapper.toItemFromRequest(itemRequestDto)));
    }


    @Override
    @Transactional(readOnly = true)
    public ItemWithBookingsDto getItem(Long itemId, Long userId) {
        ItemWithBookingsDto itemWithBookingsDto = itemMapper.toItemWithBookingsDto(itemStorage.findById(itemId).orElseThrow(() ->
                new ValidException("Предмета с таким id не существует", HttpStatus.NOT_FOUND)));
        List<BookingResponseDto> bookings = bookingMapper.toBookingResponseListDto(bookingStorage.findAllByItem_IdAndStatusIsNot(itemId, REJECTED));
        List<CommentResponseDto> comments = commentMapper.toCommentResponseListDto(commentStorage.findAllByItem_IdOrderByCreatedDesc(itemId));

        if (!itemStorage.existsItemByIdAndOwner_Id(itemId, userId)) {
            itemWithBookingsDto.setComments(comments);
            return itemWithBookingsDto;
        }

        LocalDateTime now = LocalDateTime.now();
        return getItemWithBookingsAndCommentsDto(itemWithBookingsDto, comments, bookings, now);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ItemWithBookingsDto> getItemsOfOwner(Long userId, Integer from, Integer size) {
        List<ItemWithBookingsDto> responseItems = itemMapper.toItemWithBookingsListDto(itemStorage.findAllByOwnerIdOrderById(userId));
        List<Long> itemsIds = responseItems.stream()
                .map(ItemWithBookingsDto::getId)
                .collect(Collectors.toList());
        Map<Long, List<BookingResponseDto>> bookings = bookingStorage.findAllByItem_IdInAndStatusIsNot(itemsIds, REJECTED).stream()
                .map(bookingMapper::toBookingResponseDto)
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        Map<Long, List<CommentResponseDto>> comments = commentStorage.findAllByItem_IdInOrderByCreatedDesc(itemsIds).stream()
                .map(commentMapper::toCommentResponseDto)
                .collect(Collectors.groupingBy(CommentResponseDto::getItemId));
        LocalDateTime now = LocalDateTime.now();

        return responseItems.stream().map(itemDto -> {
            Long itemId = itemDto.getId();
            return getItemWithBookingsAndCommentsDto(itemDto, comments.get(itemId), bookings.get(itemId), now);
        }).collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> searchItem(String text, Integer from, Integer size) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemMapper.toItemResponseListDto(itemStorage.searchItem(text));
    }

    @Override
    public CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto) {
        if (!userStorage.existsById(userId)) {
            throw new ValidException("Пользователя с таким id не существует", HttpStatus.NOT_FOUND);
        }
        if (!itemStorage.existsById(itemId)) {
            throw new ValidException("Предмета с таким id не существует", HttpStatus.NOT_FOUND);
        }
        LocalDateTime now = LocalDateTime.now();

        boolean isBookingConfirmed = bookingStorage.existsByItem_IdAndBooker_IdAndStatusAndEndIsBefore(itemId, userId, APPROVED, now);
        if (!isBookingConfirmed) {
            throw new ValidException("Пользователь не брал в аренду этот предмет", HttpStatus.BAD_REQUEST);
        }
        UserDto userDto = userMapper.toUserDto(userStorage.findById(userId).orElseThrow(() -> new ValidException("Пользователя с таким id не существует", HttpStatus.NOT_FOUND)));
        commentRequestDto.setCreated(now);
        commentRequestDto.setAuthorId(userId);
        commentRequestDto.setItemId(itemId);

        CommentResponseDto commentResponseDto = commentMapper.toCommentResponseDto(commentStorage.save(commentMapper.toCommentFromRequest(commentRequestDto)));
        commentResponseDto.setAuthorName(userDto.getName());

        return commentResponseDto;
    }

    private ItemWithBookingsDto getItemWithBookingsAndCommentsDto(ItemWithBookingsDto item, List<CommentResponseDto> comments,
                                                                  List<BookingResponseDto> bookings, LocalDateTime now) {

        if (Objects.isNull(bookings)) {
            return item.toBuilder()
                    .lastBooking(null)
                    .nextBooking(null)
                    .comments(comments)
                    .build();
        }
        BookingShortDto lastBooking = bookingMapper.toBookingShortDtoFromResponse(bookings.stream()
                .filter(booking -> booking.getStart().isBefore(now))
                .max(Comparator.comparing(BookingResponseDto::getStart))
                .orElse(null));

        BookingShortDto nextBooking = bookingMapper.toBookingShortDtoFromResponse(bookings.stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .min(Comparator.comparing(BookingResponseDto::getStart))
                .orElse(null));

        return item.toBuilder()
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }
}
