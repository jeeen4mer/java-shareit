package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

public final class BookingMapper {
    private BookingMapper() {
    }

    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingDto.ItemDto itemDto = new BookingDto.ItemDto();
        itemDto.setId(booking.getItem().getId());
        itemDto.setName(booking.getItem().getName());

        BookingDto.UserDto bookerDto = new BookingDto.UserDto();
        bookerDto.setId(booking.getBooker().getId());

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(itemDto)
                .booker(bookerDto)
                .status(booking.getStatus())
                .build();
    }
}
