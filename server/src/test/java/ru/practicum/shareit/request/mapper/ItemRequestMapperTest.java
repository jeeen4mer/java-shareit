package ru.practicum.shareit.request.mapper;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ItemRequestMapperTest {

    @InjectMocks
    private ItemRequestMapperImpl itemRequestMapper;

    @Test
    public void testToOutputDTO_Mapping() {
        User requester = User.builder()
                .id(2L)
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Нужен диван.")
                .requester(requester)
                .build();

        ItemRequestOutDto outputDTO = itemRequestMapper.toItemRequestOutDto(itemRequest);

        assertThat(outputDTO).isNotNull();
        assertThat(outputDTO.getId()).isEqualTo(itemRequest.getId());
        assertThat(outputDTO.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(outputDTO.getRequester().getId()).isEqualTo(itemRequest.getRequester().getId());
    }

    @Test
    public void testToOutputDTOs_Mapping() {
        ItemRequest itemRequest1 = ItemRequest.builder().id(1L).build();
        ItemRequest itemRequest2 = ItemRequest.builder().id(2L).build();
        List<ItemRequest> entities = Arrays.asList(itemRequest1, itemRequest2);

        List<ItemRequestOutDto> result = itemRequestMapper.toItemRequestOutListDto(entities);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    public void testToOutputDTOs_ReturnNull() {

        assertNull(itemRequestMapper.toItemRequestOutListDto(null));
    }

}