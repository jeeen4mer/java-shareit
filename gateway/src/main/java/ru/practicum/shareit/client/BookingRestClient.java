package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.BookingState; // Импортируем enum из shareit-dto

import java.util.List;

@Service
public class BookingRestClient implements BookingService {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public BookingRestClient(RestTemplateBuilder restTemplateBuilder,
                             @Value("${shareit-server.url}") String serverUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.serverUrl = serverUrl;
    }

    @Override
    public BookingDto createBooking(CreateBookingDto createBookingDto, Long userId) {
        String url = serverUrl + "/bookings";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<CreateBookingDto> requestEntity = new HttpEntity<>(createBookingDto, headers);
        ResponseEntity<BookingDto> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                BookingDto.class
        );
        return response.getBody();
    }

    @Override
    public BookingDto approveBooking(Long bookingId, Boolean approved, Long userId) {
        String url = serverUrl + "/bookings/{bookingId}";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("approved", approved);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<BookingDto> response = restTemplate.exchange(
                builder.buildAndExpand(bookingId).toUri(),
                HttpMethod.PATCH,
                requestEntity,
                BookingDto.class
        );
        return response.getBody();
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        String url = serverUrl + "/bookings/{bookingId}";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<BookingDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                BookingDto.class,
                bookingId
        );
        return response.getBody();
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, BookingState state, int from, int size) {
        String url = serverUrl + "/bookings";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("state", state)
                .queryParam("from", from)
                .queryParam("size", size);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<List<BookingDto>> response = restTemplate.exchange(
                builder.build().toUri(),
                HttpMethod.GET,
                requestEntity,
                (Class<List<BookingDto>>) (Class<?>) List.class
        );
        return response.getBody();
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long userId, BookingState state, int from, int size) {
        String url = serverUrl + "/bookings/owner";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("state", state)
                .queryParam("from", from)
                .queryParam("size", size);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<List<BookingDto>> response = restTemplate.exchange(
                builder.build().toUri(),
                HttpMethod.GET,
                requestEntity,
                (Class<List<BookingDto>>) (Class<?>) List.class
        );
        return response.getBody();
    }
}