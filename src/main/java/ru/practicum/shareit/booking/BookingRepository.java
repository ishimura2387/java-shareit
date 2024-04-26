package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(long id, Sort sort);

    List<Booking> findAllByBookerId(long id, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBefore(long id, LocalDateTime endTime, Sort sort);

    List<Booking> findAllByBookerIdAndEndBefore(long id, LocalDateTime endTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfter(long id, LocalDateTime startTime, Sort sort);

    List<Booking> findAllByBookerIdAndStartAfter(long id, LocalDateTime startTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusIs(long id, Status status, Sort sort);

    List<Booking> findAllByBookerIdAndStatusIs(long id, Status status, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(long id, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(long id, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItem_OwnerId(long id, Sort sort);

    List<Booking> findAllByItem_OwnerId(long id, Pageable pageable);

    List<Booking> findAllByItem_OwnerIdAndAndEndBefore(long id, LocalDateTime dateTime, Sort sort);

    List<Booking> findAllByItem_OwnerIdAndAndEndBefore(long id, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByItem_OwnerIdAndStartAfter(long id, LocalDateTime dateTime, Sort sort);

    List<Booking> findAllByItem_OwnerIdAndStartAfter(long id, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByItem_OwnerIdAndStatusIs(long id, Status status, Sort sort);

    List<Booking> findAllByItem_OwnerIdAndStatusIs(long id, Status status, Pageable pageable);

    List<Booking> findAllByItem_OwnerIdAndStartBeforeAndEndAfter(long id, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByItem_OwnerIdAndStartBeforeAndEndAfter(long id, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByStatusIs(Status status, Sort sort);

    List<Booking> findAllByItemIdAndBookerIdAndEndBefore(long itemId, long userId, LocalDateTime localDateTime);

    @Query("select b from Booking b where b.item.id = ?1 and (b.start <= ?3 and b.end >= ?2)")
    List<Booking> findAllByItemIdAndEndBetweenOrStartBetween(long itemId, LocalDateTime localDateStart,
                                                             LocalDateTime localDateEnd);

}
