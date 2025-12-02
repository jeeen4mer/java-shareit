package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceIntegrationTest {

    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final ItemService itemService;

    private UserDto user1;
    private UserDto user2;

    private void initUsers() {
        user1 = userService.create(UserDto.builder()
                .name("Анна")
                .email("anna@yandex.ru")
                .build());

        user2 = userService.create(UserDto.builder()
                .name("Борис")
                .email("boris@gmail.com")
                .build());
    }


    @Test
    void shouldReturnOwnRequestsWithItemsTest() {
        initUsers();

        ItemRequestCreateDto requestDto = new ItemRequestCreateDto();
        requestDto.setDescription("Хочу газонокосилку");
        ItemRequestDto request = itemRequestService.create(user1.getId(), requestDto);

        ItemDto itemDto = ItemDto.builder()
                .name("Газонокосилка Bosch")
                .description("Новая")
                .available(true)
                .requestId(request.getId())
                .build();

        itemService.create(user2.getId(), itemDto);


        List<ItemRequestDto> ownRequests = itemRequestService.getOwnRequests(user1.getId());

        assertThat(ownRequests).hasSize(1);
        assertThat(ownRequests.getFirst().getItems()).hasSize(1);
        assertThat(ownRequests.getFirst().getItems().getFirst().getName()).isEqualTo("Газонокосилка Bosch");
        assertThat(ownRequests.getFirst().getItems().getFirst().getRequestId()).isEqualTo(request.getId());
    }

    @Test
    void shouldReturnAllOtherUsersRequestsWithPaginationTest() {
        initUsers();

        itemRequestService.create(user1.getId(), new ItemRequestCreateDto("Ищу молоток"));
        itemRequestService.create(user1.getId(), new ItemRequestCreateDto("Нужен шуруповёрт"));
        itemRequestService.create(user1.getId(), new ItemRequestCreateDto("Хочу дрель"));

        List<ItemRequestDto> allRequests = itemRequestService.getAllRequests(user2.getId(), 0, 10);

        assertThat(allRequests).hasSize(3);
        assertThat(allRequests)
                .extracting(ItemRequestDto::getDescription)
                .containsExactlyInAnyOrder("Ищу молоток", "Нужен шуруповёрт", "Хочу дрель");
    }

    @Test
    void shouldReturnEmptyListWhenNoOtherRequestsTest() {
        initUsers();

        List<ItemRequestDto> requests = itemRequestService.getAllRequests(user2.getId(), 0, 10);

        assertThat(requests).isEmpty();
    }

    @Test
    void shouldReturnRequestByIdWithItemsTest() {
        initUsers();

        ItemRequestDto request = itemRequestService.create(user1.getId(),
                new ItemRequestCreateDto("Нужна пила"));

        itemService.create(user2.getId(), ItemDto.builder()
                .name("Циркулярная пила")
                .description("Мощная")
                .available(true)
                .requestId(request.getId())
                .build());

        ItemRequestDto found = itemRequestService.getById(user2.getId(), request.getId());

        assertThat(found.getId()).isEqualTo(request.getId());
        assertThat(found.getDescription()).isEqualTo("Нужна пила");
        assertThat(found.getItems()).hasSize(1);
        assertThat(found.getItems().getFirst().getName()).isEqualTo("Циркулярная пила");
    }

    @Test
    void shouldThrowWhenUserNotFoundTest() {
        assertThatThrownBy(() -> itemRequestService.create(999L, new ItemRequestCreateDto("что-то")))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void shouldThrowWhenRequestNotFoundTest() {
        initUsers();

        assertThatThrownBy(() -> itemRequestService.getById(user1.getId(), 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Запрос не найден");
    }

    @Test
    void shouldSortOwnRequestsByCreatedDescTest() {
        initUsers();

        itemRequestService.create(user1.getId(), new ItemRequestCreateDto("Первый"));
        sleep();
        itemRequestService.create(user1.getId(), new ItemRequestCreateDto("Второй"));
        sleep();
        itemRequestService.create(user1.getId(), new ItemRequestCreateDto("Третий"));

        List<ItemRequestDto> requests = itemRequestService.getOwnRequests(user1.getId());

        assertThat(requests).hasSize(3);
        assertThat(requests.get(0).getDescription()).isEqualTo("Третий");
        assertThat(requests.get(1).getDescription()).isEqualTo("Второй");
        assertThat(requests.get(2).getDescription()).isEqualTo("Первый");
    }

    @Test
    void shouldSortAllRequestsByCreatedDescTest() {
        initUsers();

        sleep();
        itemRequestService.create(user1.getId(), new ItemRequestCreateDto("Старый"));
        sleep();
        itemRequestService.create(user2.getId(), new ItemRequestCreateDto("Новый"));

        List<ItemRequestDto> requests = itemRequestService.getAllRequests(user1.getId(), 0, 10);

        assertThat(requests).hasSize(1);
        assertThat(requests.getFirst().getDescription()).isEqualTo("Новый");
    }

    private void sleep() {
        try {
            Thread.sleep((long) 10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}