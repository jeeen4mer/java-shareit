package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ItemMapperTest {
    @InjectMocks
    private ItemMapperImpl itemMapper;

    @Test
    public void testToShortOutputDTO_ReturnNull() {

        assertNull(itemMapper.toItemResponseDto(null));
    }

    @Test
    public void testToShortOutputDTOs_ReturnNull() {

        assertNull(itemMapper.toItemResponseListDto(null));
    }
}

