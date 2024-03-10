package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long id);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long id, LocalDateTime endTime);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long id, LocalDateTime startTime);

    List<Booking> findAllByBookerIdAndStatusIsOrderByStartDesc(long id, Status status);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start desc")
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long id, LocalDateTime dateTime);

    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.start desc")
    List<Booking> findAllByOwnerIdOrderByStartDesc(long id);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < ?2 order by b.start desc")
    List<Booking> findAllByOwnerIdAndEndBeforeOrderByStartDesc(long id, LocalDateTime dateTime);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > ?2 order by b.start desc")
    List<Booking> findAllByOwnerIdAndStartAfterOrderByStartDesc(long id, LocalDateTime dateTime);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2 order by b.start desc")
    List<Booking> findAllByOwnerIdAndStatusIsOrderByStartDesc(long id, Status status);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start desc")
    List<Booking> findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(long id, LocalDateTime dateTime);

    List<Booking> findAllByItemIdAndStatusIsOrderByStartDesc(long itemId, Status status);

    List<Booking> findAllByItemIdAndBookerIdAndEndBefore(long itemId, long userId, LocalDateTime localDateTime);
}
