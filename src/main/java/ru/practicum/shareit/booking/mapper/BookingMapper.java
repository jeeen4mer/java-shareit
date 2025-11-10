package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

@Component
public class BookingMapper {
    public BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingDto.Booker booker = new BookingDto.Booker(
                booking.getBooker().getId(),
                booking.getBooker().getName()
        );

        BookingDto.Item item = new BookingDto.Item(
                booking.getItem().getId(),
                booking.getItem().getName()
        );

        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                booker,
                item
        );
    }
}