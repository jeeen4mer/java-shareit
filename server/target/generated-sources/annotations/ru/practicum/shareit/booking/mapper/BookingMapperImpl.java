package ru.practicum.shareit.booking.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-01T16:39:31+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class BookingMapperImpl implements BookingMapper {

    @Override
    public Booking toBooking(BookingRequestDto bookingRequestDto) {
        if ( bookingRequestDto == null ) {
            return null;
        }

        Booking.BookingBuilder booking = Booking.builder();

        booking.booker( bookingRequestDtoToUser( bookingRequestDto ) );
        booking.item( bookingRequestDtoToItem( bookingRequestDto ) );
        booking.id( bookingRequestDto.getId() );
        booking.start( bookingRequestDto.getStart() );
        booking.end( bookingRequestDto.getEnd() );
        booking.status( bookingRequestDto.getStatus() );

        return booking.build();
    }

    @Override
    public BookingResponseDto toBookingResponseDto(Booking booking) {
        if ( booking == null ) {
            return null;
        }

        BookingResponseDto.BookingResponseDtoBuilder bookingResponseDto = BookingResponseDto.builder();

        bookingResponseDto.id( booking.getId() );
        bookingResponseDto.start( booking.getStart() );
        bookingResponseDto.end( booking.getEnd() );
        bookingResponseDto.status( booking.getStatus() );
        bookingResponseDto.booker( userToUserDto( booking.getBooker() ) );
        bookingResponseDto.item( itemToItemResponseDto( booking.getItem() ) );

        return bookingResponseDto.build();
    }

    @Override
    public List<BookingResponseDto> toBookingResponseListDto(List<Booking> bookings) {
        if ( bookings == null ) {
            return null;
        }

        List<BookingResponseDto> list = new ArrayList<BookingResponseDto>( bookings.size() );
        for ( Booking booking : bookings ) {
            list.add( toBookingResponseDto( booking ) );
        }

        return list;
    }

    @Override
    public BookingShortDto toBookingShortDtoFromResponse(BookingResponseDto bookingResponseDto) {
        if ( bookingResponseDto == null ) {
            return null;
        }

        BookingShortDto.BookingShortDtoBuilder bookingShortDto = BookingShortDto.builder();

        bookingShortDto.bookerId( bookingResponseDtoBookerId( bookingResponseDto ) );
        bookingShortDto.id( bookingResponseDto.getId() );

        return bookingShortDto.build();
    }

    protected User bookingRequestDtoToUser(BookingRequestDto bookingRequestDto) {
        if ( bookingRequestDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.id( bookingRequestDto.getBookerId() );

        return user.build();
    }

    protected Item bookingRequestDtoToItem(BookingRequestDto bookingRequestDto) {
        if ( bookingRequestDto == null ) {
            return null;
        }

        Item.ItemBuilder item = Item.builder();

        item.id( bookingRequestDto.getItemId() );

        return item.build();
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

    protected ItemResponseDto itemToItemResponseDto(Item item) {
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

    private Long bookingResponseDtoBookerId(BookingResponseDto bookingResponseDto) {
        if ( bookingResponseDto == null ) {
            return null;
        }
        UserDto booker = bookingResponseDto.getBooker();
        if ( booker == null ) {
            return null;
        }
        Long id = booker.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
