package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    ItemRequestOutDto toItemRequestOutDto(ItemRequest itemRequest);

    List<ItemRequestOutDto> toItemRequestOutListDto(List<ItemRequest> itemRequests);

}
