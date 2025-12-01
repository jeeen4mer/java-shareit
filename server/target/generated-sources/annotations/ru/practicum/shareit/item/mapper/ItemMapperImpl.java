package ru.practicum.shareit.item.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-01T15:20:41+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class ItemMapperImpl implements ItemMapper {

    @Override
    public Item toItemFromRequest(ItemRequestDto requestDto) {
        if ( requestDto == null ) {
            return null;
        }

        Item.ItemBuilder item = Item.builder();

        item.owner( itemRequestDtoToUser( requestDto ) );
        item.id( requestDto.getId() );
        item.name( requestDto.getName() );
        item.description( requestDto.getDescription() );
        item.available( requestDto.getAvailable() );
        item.requestId( requestDto.getRequestId() );

        return item.build();
    }

    @Override
    public ItemResponseDto toItemResponseDto(Item item) {
        if ( item == null ) {
            return null;
        }

        ItemResponseDto.ItemResponseDtoBuilder itemResponseDto = ItemResponseDto.builder();

        itemResponseDto.id( item.getId() );
        itemResponseDto.name( item.getName() );
        itemResponseDto.description( item.getDescription() );
        itemResponseDto.available( item.getAvailable() );
        itemResponseDto.requestId( item.getRequestId() );

        return itemResponseDto.build();
    }

    @Override
    public List<ItemResponseDto> toItemResponseListDto(List<Item> items) {
        if ( items == null ) {
            return null;
        }

        List<ItemResponseDto> list = new ArrayList<ItemResponseDto>( items.size() );
        for ( Item item : items ) {
            list.add( toItemResponseDto( item ) );
        }

        return list;
    }

    @Override
    public ItemWithBookingsDto toItemWithBookingsDto(Item item) {
        if ( item == null ) {
            return null;
        }

        ItemWithBookingsDto.ItemWithBookingsDtoBuilder itemWithBookingsDto = ItemWithBookingsDto.builder();

        itemWithBookingsDto.id( item.getId() );
        itemWithBookingsDto.name( item.getName() );
        itemWithBookingsDto.description( item.getDescription() );
        if ( item.getAvailable() != null ) {
            itemWithBookingsDto.available( item.getAvailable() );
        }

        return itemWithBookingsDto.build();
    }

    @Override
    public List<ItemWithBookingsDto> toItemWithBookingsListDto(List<Item> items) {
        if ( items == null ) {
            return null;
        }

        List<ItemWithBookingsDto> list = new ArrayList<ItemWithBookingsDto>( items.size() );
        for ( Item item : items ) {
            list.add( toItemWithBookingsDto( item ) );
        }

        return list;
    }

    protected User itemRequestDtoToUser(ItemRequestDto itemRequestDto) {
        if ( itemRequestDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.id( itemRequestDto.getOwnerId() );

        return user.build();
    }
}
