package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.exception.UnsupportedStateException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState fromString(String state) {
        if (state == null) return ALL;
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("Неизвестный статус: " + state);
        }
    }
}
