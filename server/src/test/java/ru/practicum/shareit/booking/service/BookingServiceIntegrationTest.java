package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.practicum.shareit.booking.model.BookingStatus.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    private UserDto owner;
    private UserDto booker;
    private UserDto stranger;
    private ItemDto item;

    private void initData() {
        owner = userService.create(UserDto.builder().name("Владелец").email("owner@test.ru").build());
        booker = userService.create(UserDto.builder().name("Букер").email("booker@test.ru").build());
        stranger = userService.create(UserDto.builder().name("Чужой").email("stranger@test.ru").build());

        item = itemService.create(owner.getId(), ItemDto.builder()
                .name("Дрель")
                .description("Мощная")
                .available(true)
                .build());
    }

    @Test
    void shouldCreateBookingWithWaitingStatusTest() {
        initData();

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto created = bookingService.create(booker.getId(), dto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo(WAITING);
        assertThat(created.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(created.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void shouldApproveBookingTest() {
        initData();
        BookingDto booking = createWaitingBooking();

        BookingDto approved = bookingService.approve(owner.getId(), booking.getId(), true);

        assertThat(approved.getStatus()).isEqualTo(APPROVED);
    }

    @Test
    void shouldRejectBookingTest() {
        initData();
        BookingDto booking = createWaitingBooking();

        BookingDto rejected = bookingService.approve(owner.getId(), booking.getId(), false);

        assertThat(rejected.getStatus()).isEqualTo(REJECTED);
    }

    @Test
    void shouldThrowWhenNonOwnerTriesToApproveTest() {
        initData();
        BookingDto booking = createWaitingBooking();

        assertThatThrownBy(() -> bookingService.approve(stranger.getId(), booking.getId(), true))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Только владелец может подтвердить бронирование");
    }

    @Test
    void shouldThrowWhenOwnerBooksOwnItemTest() {
        initData();

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        assertThatThrownBy(() -> bookingService.create(owner.getId(), dto))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Владелец не может бронировать свою вещь");
    }

    @Test
    void shouldThrowWhenItemNotAvailableTest() {
        initData();

        itemService.update(owner.getId(), item.getId(), ItemDto.builder().available(false).build());

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        assertThatThrownBy(() -> bookingService.create(booker.getId(), dto))
                .isInstanceOf(ItemNotAvailableException.class);
    }

    @Test
    void shouldGetBookingByIdForBookerOrOwnerTest() {
        initData();
        BookingDto booking = createApprovedPastBooking();

        BookingDto foundByBooker = bookingService.getById(booker.getId(), booking.getId());
        BookingDto foundByOwner = bookingService.getById(owner.getId(), booking.getId());

        assertThat(foundByBooker.getId()).isEqualTo(booking.getId());
        assertThat(foundByOwner.getId()).isEqualTo(booking.getId());
    }

    @Test
    void shouldThrowWhenStrangerTriesToGetBookingTest() {
        initData();
        BookingDto booking = createWaitingBooking();

        assertThatThrownBy(() -> bookingService.getById(stranger.getId(), booking.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Доступ только автору или владельцу");
    }

    @Test
    void shouldReturnBookerPastBookingsTest() {
        initData();
        createApprovedPastBooking();

        List<BookingDto> past = bookingService.getAllByBooker(booker.getId(), BookingState.PAST);

        assertThat(past).hasSize(1);
        assertThat(past.getFirst().getStatus()).isEqualTo(APPROVED);
    }

    @Test
    void shouldReturnBookerFutureBookingsTest() {
        initData();
        createApprovedFutureBooking();

        List<BookingDto> future = bookingService.getAllByBooker(booker.getId(), BookingState.FUTURE);

        assertThat(future).hasSize(1);
    }

    @Test
    void shouldReturnOwnerCurrentBookingsTest() {
        initData();
        createApprovedCurrentBooking();

        List<BookingDto> current = bookingService.getAllByOwner(owner.getId(), BookingState.CURRENT);

        assertThat(current).hasSize(1);
    }

    @Test
    void shouldReturnWaitingBookingsTest() {
        initData();
        createWaitingBooking();

        List<BookingDto> waiting = bookingService.getAllByOwner(owner.getId(), BookingState.WAITING);

        assertThat(waiting).hasSize(1);
        assertThat(waiting.getFirst().getStatus()).isEqualTo(WAITING);
    }


    private BookingDto createWaitingBooking() {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        return bookingService.create(booker.getId(), dto);
    }

    private BookingDto createApprovedPastBooking() {
        BookingDto waiting = createWaitingBooking();
        bookingService.approve(owner.getId(), waiting.getId(), true);


        BookingCreateDto pastDto = new BookingCreateDto();
        pastDto.setItemId(item.getId());
        pastDto.setStart(LocalDateTime.now().minusDays(10));
        pastDto.setEnd(LocalDateTime.now().minusHours(3));
        BookingDto past = bookingService.create(booker.getId(), pastDto);
        bookingService.approve(owner.getId(), past.getId(), true);
        return past;
    }

    private void createApprovedFutureBooking() {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(5));
        dto.setEnd(LocalDateTime.now().plusDays(10));
        BookingDto future = bookingService.create(booker.getId(), dto);
        bookingService.approve(owner.getId(), future.getId(), true);
    }

    private void createApprovedCurrentBooking() {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().minusHours(1));
        dto.setEnd(LocalDateTime.now().plusHours(5));
        BookingDto current = bookingService.create(booker.getId(), dto);
        bookingService.approve(owner.getId(), current.getId(), true);
    }
}