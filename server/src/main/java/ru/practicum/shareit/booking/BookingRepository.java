package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime start);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    @Query("SELECT b FROM Booking b JOIN FETCH b.booker JOIN FETCH b.item WHERE b.item.id = :itemId AND b.status = 'APPROVED' " +
            "AND b.end > :now ORDER BY b.start ASC")
    List<Booking> findNextBooking(Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b JOIN FETCH b.booker JOIN FETCH b.item WHERE b.item.id = :itemId AND b.status = 'APPROVED' " +
            "AND b.end < :now ORDER BY b.end DESC")
    List<Booking> findLastBooking(Long itemId, LocalDateTime now);

    List<Booking> findByBookerIdAndItemIdAndEndBeforeAndStatus(
            Long bookerId, Long itemId, LocalDateTime end, BookingStatus status);
}