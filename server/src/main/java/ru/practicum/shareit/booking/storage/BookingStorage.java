package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constant.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByItem_Owner_IdAndEndIsBefore(Long ownerId, LocalDateTime now, Pageable pageRequest);

    List<Booking> findAllByItem_IdInAndStatusIsNot(List<Long> itemsIds, Status status);

    Page<Booking> findAllByItem_Owner_IdAndStartIsAfter(Long ownerId, LocalDateTime now, Pageable pageRequest);

    Page<Booking> findAllByItem_Owner_IdAndStatus(Long ownerId, Status status, Pageable pageRequest);

    boolean existsByItem_IdAndBooker_IdAndStatusAndEndIsBefore(Long itemId, Long bookerId, Status status, LocalDateTime now);

    Page<Booking> findAllByBooker_Id(Long bookerId, Pageable pageRequest);

    Page<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime now, LocalDateTime timeNow, Pageable pageRequest);

    Page<Booking> findAllByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime now, Pageable pageRequest);

    Page<Booking> findAllByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime now, Pageable pageRequest);

    Page<Booking> findAllByBooker_IdAndStatus(Long bookerId, Status status, Pageable pageRequest);

    Page<Booking> findAllByItem_Owner_Id(Long ownerId, Pageable pageRequest);

    List<Booking> findAllByItem_IdAndStatusIsNot(Long itemId, Status status);

    Page<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime now, LocalDateTime timeNow, Pageable pageRequest);

}
