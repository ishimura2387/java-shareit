package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Sort;

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

    @Query("select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(long id, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterWithPagination(long id, LocalDateTime dateTime, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1")
    List<Booking> findAllByOwnerId(long id, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1")
    List<Booking> findAllByOwnerId(long id, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < ?2")
    List<Booking> findAllByOwnerIdAndEndBefore(long id, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < ?2")
    List<Booking> findAllByOwnerIdAndEndBefore(long id, LocalDateTime dateTime, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > ?2")
    List<Booking> findAllByOwnerIdAndStartAfter(long id, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > ?2")
    List<Booking> findAllByOwnerIdAndStartAfter(long id, LocalDateTime dateTime, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2")
    List<Booking> findAllByOwnerIdAndStatusIs(long id, Status status, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2")
    List<Booking> findAllByOwnerIdAndStatusIs(long id, Status status, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findAllByOwnerIdAndStartBeforeAndEndAfter(long id, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findAllByOwnerIdAndStartBeforeAndEndAfterWithPagination(long id, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByItemIdAndStatusIs(long itemId, Status status, Sort sort);

    List<Booking> findAllByItemIdAndBookerIdAndEndBefore(long itemId, long userId, LocalDateTime localDateTime);

    @Query("select b from Booking b where b.item.id = ?1 and (b.start <= ?3 and b.end >= ?2)")
    List<Booking> findAllByItemIdAndEndBetweenOrStartBetween(long itemId, LocalDateTime localDateStart,
                                                             LocalDateTime localDateEnd);
}
