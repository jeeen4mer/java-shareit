package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(CreateItemDto createItemDto, Long ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        Item item = ItemMapper.toItem(createItemDto, ownerId);
        Item savedItem = itemRepository.save(item);
        return toItemDtoWithCommentsOnly(savedItem);
    }

    @Override
    public ItemDto getItemById(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь не найдена"));

        return toItemDtoWithBookingsAndComments(item, userId);
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Long ownerId) {
        List<Item> items = itemRepository.findByOwnerIdOrderById(ownerId);
        return items.stream()
                .map(item -> toItemDtoWithBookingsAndComments(item, ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Long id, UpdateItemDto updateItemDto, Long ownerId) {
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь не найдена"));

        if (!ownerId.equals(existingItem.getOwnerId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Только владелец может редактировать вещь");
        }

        if (updateItemDto.getName() != null) {
            existingItem.setName(updateItemDto.getName());
        }
        if (updateItemDto.getDescription() != null) {
            existingItem.setDescription(updateItemDto.getDescription());
        }
        if (updateItemDto.getAvailable() != null) {
            existingItem.setAvailable(updateItemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);
        return toItemDtoWithBookingsAndComments(updatedItem, ownerId);
    }

    @Override
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchAvailableItems(text).stream()
                .map(item -> toItemDtoWithCommentsOnly(item))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long itemId, CreateCommentDto createCommentDto, Long userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь не найдена"));

        List<Booking> userBookings = bookingRepository.findByBookerIdAndItemIdAndEndBeforeAndStatus(
                userId, itemId, LocalDateTime.now(), BookingStatus.APPROVED);

        if (userBookings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Пользователь не брал эту вещь в аренду или аренда еще не завершена");
        }

        Comment comment = new Comment();
        comment.setText(createCommentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return toCommentDto(savedComment);
    }

    private ItemDto toItemDtoWithBookingsAndComments(Item item, Long userId) {
        ItemDto itemDto = ItemMapper.toItemDto(item);

        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(item.getId());
        List<CommentDto> commentDtos = comments.stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(commentDtos);

        if (item.getOwnerId().equals(userId)) {
            List<Booking> lastBookings = bookingRepository.findLastBooking(
                    item.getId(), LocalDateTime.now());
            if (!lastBookings.isEmpty()) {
                Booking lastBooking = lastBookings.get(0);
                itemDto.setLastBooking(new ItemDto.BookingInfo(
                        lastBooking.getId(),
                        lastBooking.getBooker().getId(),
                        lastBooking.getStart(),
                        lastBooking.getEnd()
                ));
            }

            List<Booking> nextBookings = bookingRepository.findNextBooking(
                    item.getId(), LocalDateTime.now());
            if (!nextBookings.isEmpty()) {
                Booking nextBooking = nextBookings.get(0);
                itemDto.setNextBooking(new ItemDto.BookingInfo(
                        nextBooking.getId(),
                        nextBooking.getBooker().getId(),
                        nextBooking.getStart(),
                        nextBooking.getEnd()
                ));
            }
        } else {
            itemDto.setLastBooking(null);
            itemDto.setNextBooking(null);
        }

        return itemDto;
    }

    private ItemDto toItemDtoWithCommentsOnly(Item item) {
        ItemDto itemDto = ItemMapper.toItemDto(item);

        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(item.getId());
        List<CommentDto> commentDtos = comments.stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(commentDtos);

        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);

        return itemDto;
    }

    private CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}