package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.mapper.ItemMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public ItemDto createItem(CreateItemDto createItemDto, Long ownerId) {
        return null;
    }

    @Override
    public ItemDto getItemById(Long id, Long userId) {
        return null;
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Long ownerId) {
        List<Item> items = itemRepository.findByOwnerIdOrderById(ownerId);

        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Long id, UpdateItemDto updateItemDto, Long ownerId) {

        return null;
    }

    @Override
    public void deleteItem(Long id) {
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<Item> items = itemRepository.searchAvailableItems(text);

        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long itemId, CreateCommentDto createCommentDto, Long userId) {
        return null;
    }
}