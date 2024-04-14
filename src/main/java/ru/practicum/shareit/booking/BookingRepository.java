package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long id);

    List<Booking> findAllByBookerId(long id, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long id, LocalDateTime endTime);

    List<Booking> findAllByBookerIdAndEndBefore(long id, LocalDateTime endTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long id, LocalDateTime startTime);

    List<Booking> findAllByBookerIdAndStartAfter(long id, LocalDateTime startTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusIsOrderByStartDesc(long id, Status status);

    List<Booking> findAllByBookerIdAndStatusIs(long id, Status status, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start ")
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long id, LocalDateTime dateTime);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start ")
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterWithPagination(long id, LocalDateTime dateTime, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.start desc")
    List<Booking> findAllByOwnerIdOrderByStartDesc(long id);

    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.start")
    List<Booking> findAllByOwnerId(long id, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < ?2 order by b.start desc")
    List<Booking> findAllByOwnerIdAndEndBeforeOrderByStartDesc(long id, LocalDateTime dateTime);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < ?2 order by b.start")
    List<Booking> findAllByOwnerIdAndEndBefore(long id, LocalDateTime dateTime, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > ?2 order by b.start desc")
    List<Booking> findAllByOwnerIdAndStartAfterOrderByStartDesc(long id, LocalDateTime dateTime); //+

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > ?2 order by b.start")
    List<Booking> findAllByOwnerIdAndStartAfter(long id, LocalDateTime dateTime, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2 order by b.start desc")
    List<Booking> findAllByOwnerIdAndStatusIsOrderByStartDesc(long id, Status status);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2 order by b.start")
    List<Booking> findAllByOwnerIdAndStatusIs(long id, Status status, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start desc")
    List<Booking> findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(long id, LocalDateTime dateTime);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start")
    List<Booking> findAllByOwnerIdAndStartBeforeAndEndAfterWithPagination(long id, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByItemIdAndStatusIsOrderByStartDesc(long itemId, Status status);

    List<Booking> findAllByItemIdAndBookerIdAndEndBefore(long itemId, long userId, LocalDateTime localDateTime);

    @Query("select b from Booking b where b.item.id = ?1 and (b.start <= ?3 and b.end >= ?2)")
    List<Booking> findAllByItemIdAndEndBetweenOrStartBetween(long itemId, LocalDateTime localDateStart,
                                                             LocalDateTime localDateEnd);
}
