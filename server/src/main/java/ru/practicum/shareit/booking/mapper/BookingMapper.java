package ru.practicum.shareit.booking.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(source = "bookerId", target = "booker.id")
    @Mapping(source = "itemId", target = "item.id")
    Booking toBooking(BookingRequestDto bookingRequestDto);

    BookingResponseDto toBookingResponseDto(Booking booking);

    List<BookingResponseDto> toBookingResponseListDto(List<Booking> bookings);

    @Mapping(source = "booker.id", target = "bookerId")
    BookingShortDto toBookingShortDtoFromResponse(BookingResponseDto bookingResponseDto);
}
