package ru.practicum.shareit.exception;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResponseError {
    private String error;
}