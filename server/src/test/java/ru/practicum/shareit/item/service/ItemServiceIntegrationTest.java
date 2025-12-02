package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.CommentNotAllowedException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    private UserDto owner;
    private UserDto booker;
    private UserDto stranger;

    private void initUsers() {
        owner = userService.create(UserDto.builder().name("Владелец").email("owner@yandex.ru").build());
        booker = userService.create(UserDto.builder().name("Букер").email("booker@gmail.com").build());
        stranger = userService.create(UserDto.builder().name("Чужой").email("stranger@mail.ru").build());
    }

    @Test
    void shouldCreateAndGetItemTest() {
        initUsers();

        ItemDto dto = ItemDto.builder()
                .name("Дрель")
                .description("Аккумуляторная")
                .available(true)
                .build();

        ItemDto created = itemService.create(owner.getId(), dto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Дрель");
        assertThat(created.getDescription()).isEqualTo("Аккумуляторная");

        ItemDto found = itemService.getById(owner.getId(), created.getId());
        assertThat(found).isEqualTo(created);
    }

    @Test
    void shouldUpdateOnlyProvidedFieldsTest() {
        initUsers();

        ItemDto original = itemService.create(owner.getId(), ItemDto.builder()
                .name("Старая дрель")
                .description("Старая")
                .available(true)
                .build());

        ItemDto update = ItemDto.builder()
                .name("Новая дрель")
                .available(false)
                .build();

        ItemDto updated = itemService.update(owner.getId(), original.getId(), update);

        assertThat(updated.getName()).isEqualTo("Новая дрель");
        assertThat(updated.getDescription()).isEqualTo("Старая");
        assertThat(updated.getAvailable()).isFalse();
    }

    @Test
    void shouldThrowWhenNonOwnerUpdatesItemTest() {
        initUsers();

        ItemDto item = itemService.create(owner.getId(), ItemDto.builder()
                .name("Моя вещь")
                .description("Личная")
                .available(true)
                .build());

        ItemDto malicious = ItemDto.builder().name("Взломанная").build();

        assertThatThrownBy(() -> itemService.update(stranger.getId(), item.getId(), malicious))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Только владелец может редактировать вещь");
    }

    @Test
    void shouldSearchItemsByNameOrDescriptionIgnoreCaseTest() {
        initUsers();

        itemService.create(owner.getId(), ItemDto.builder().name("молоток").description("тяжелый").available(true).build());
        itemService.create(stranger.getId(), ItemDto.builder().name("Дрель Bosch").description("мощная").available(true).build());
        itemService.create(owner.getId(), ItemDto.builder().name("Пила").description("циркулярная пила").available(true).build());

        List<ItemDto> result = itemService.search("пилА");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Пила");
    }

    @Test
    void shouldReturnEmptyListWhenSearchTextBlank() {
        initUsers();
        assertThat(itemService.search("   ")).isEmpty();
        assertThat(itemService.search("")).isEmpty();
        assertThat(itemService.search(null)).isEmpty();
    }

    @Test
    void shouldAddCommentOnlyAfterCompletedApprovedBookingTest() {
        initUsers();

        ItemDto itemDto = ItemDto.builder().name("Газонокосилка").description("Электрическая").available(true).build();
        ItemDto item = itemService.create(owner.getId(), itemDto);


        BookingCreateDto pastBooking = new BookingCreateDto();
        pastBooking.setItemId(item.getId());
        pastBooking.setStart(LocalDateTime.now().minusDays(10));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));

        bookingService.create(booker.getId(), pastBooking);
        pastBooking.setEnd(LocalDateTime.now().minusHours(3));

        var createdBooking = bookingService.create(booker.getId(), pastBooking);

        bookingService.approve(owner.getId(), createdBooking.getId(), true);

        CommentDto comment = itemService.addComment(booker.getId(), item.getId(), new CommentCreateDto("Классная косилка!"));

        assertThat(comment.getText()).isEqualTo("Классная косилка!");
        assertThat(comment.getAuthorName()).isEqualTo("Букер");
    }

    @Test
    void shouldNotAllowCommentWithoutCompletedBookingTest() {
        initUsers();

        ItemDto item = itemService.create(owner.getId(), ItemDto.builder()
                .name("Лопата")
                .description("Снеговая")
                .available(true)
                .build());

        assertThatThrownBy(() -> itemService.addComment(booker.getId(), item.getId(), new CommentCreateDto("Не брал")))
                .isInstanceOf(CommentNotAllowedException.class)
                .hasMessage("Отзыв можно оставить только после завершения бронирования");
    }

    @Test
    void shouldReturnItemWithLastAndNextBookingOnlyForOwnerTest() {
        initUsers();

        ItemDto itemDto = ItemDto.builder().name("Триммер").description("Для травы").available(true).build();
        ItemDto item = itemService.create(owner.getId(), itemDto);

        BookingCreateDto past = new BookingCreateDto();
        past.setItemId(item.getId());
        past.setStart(LocalDateTime.now().minusDays(10));
        past.setEnd(LocalDateTime.now().minusDays(5));
        var pastBooking = bookingService.create(booker.getId(), past);
        bookingService.approve(owner.getId(), pastBooking.getId(), true);

        BookingCreateDto future = new BookingCreateDto();
        future.setItemId(item.getId());
        future.setStart(LocalDateTime.now().plusDays(5));
        future.setEnd(LocalDateTime.now().plusDays(10));
        var futureBooking = bookingService.create(booker.getId(), future);
        bookingService.approve(owner.getId(), futureBooking.getId(), true);

        ItemWithBookingsDto result = itemService.findByIdWithDetails(owner.getId(), item.getId());

        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getNextBooking()).isNotNull();
        assertThat(result.getLastBooking().getBookerId()).isEqualTo(booker.getId());
    }

    @Test
    void shouldNotShowBookingsToNonOwnerTest() {
        initUsers();

        ItemDto item = itemService.create(owner.getId(), ItemDto.builder().name("Секрет").available(true).description("Тайна").build());

        ItemWithBookingsDto result = itemService.findByIdWithDetails(stranger.getId(), item.getId());

        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
    }

    @Test
    void shouldReturnAllOwnerItemsWithBookingsAndCommentsTest() {
        initUsers();

        itemService.create(owner.getId(), ItemDto.builder().name("Вещь1").description("1").available(true).build());
        itemService.create(owner.getId(), ItemDto.builder().name("Вещь2").description("2").available(true).build());

        List<ItemWithBookingsDto> items = itemService.findByOwnerId(owner.getId());

        assertThat(items).hasSize(2);
        assertThat(items).extracting(ItemWithBookingsDto::getName)
                .containsExactlyInAnyOrder("Вещь1", "Вещь2");
    }
}