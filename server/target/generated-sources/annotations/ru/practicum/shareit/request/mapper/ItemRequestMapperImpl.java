package ru.practicum.shareit.request.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-01T16:39:31+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class ItemRequestMapperImpl implements ItemRequestMapper {

    @Override
    public ItemRequestOutDto toItemRequestOutDto(ItemRequest itemRequest) {
        if ( itemRequest == null ) {
            return null;
        }

        ItemRequestOutDto.ItemRequestOutDtoBuilder itemRequestOutDto = ItemRequestOutDto.builder();

        itemRequestOutDto.id( itemRequest.getId() );
        itemRequestOutDto.description( itemRequest.getDescription() );
        itemRequestOutDto.requester( userToUserDto( itemRequest.getRequester() ) );
        itemRequestOutDto.created( itemRequest.getCreated() );

        return itemRequestOutDto.build();
    }

    @Override
    public List<ItemRequestOutDto> toItemRequestOutListDto(List<ItemRequest> itemRequests) {
        if ( itemRequests == null ) {
            return null;
        }

        List<ItemRequestOutDto> list = new ArrayList<ItemRequestOutDto>( itemRequests.size() );
        for ( ItemRequest itemRequest : itemRequests ) {
            list.add( toItemRequestOutDto( itemRequest ) );
        }

        return list;
    }

    protected UserDto userToUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.id( user.getId() );
        userDto.name( user.getName() );
        userDto.email( user.getEmail() );

        return userDto.build();
    }
}
