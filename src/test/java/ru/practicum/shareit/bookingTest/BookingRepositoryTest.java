package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy,M,d,H,m");
    LocalDateTime start = LocalDateTime.of(2030, 12, 25, 12, 00, 00);
    LocalDateTime end = LocalDateTime.of(2031, 12, 25, 12, 00, 00);
    User user = new User(1, "user1", "user1@mail.ru");

    User user2 = new User(2, "user2", "user2@mail.ru");
    Item item = new Item(1, "item1", "description item 1", true, user, null);

    Booking booking = new Booking(1, start, end, item, user2, Status.WAITING);

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDescTest() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        bookingRepository.save(booking);
        TypedQuery<Booking> query = em.getEntityManager().createQuery("select b from Booking b where b.booker.id = :id " +
                "and b.start < :start and b.end > :end order by b.start", Booking.class);
        query.setParameter("id", booking.getBooker().getId());
        query.setParameter("start", booking.getStart().plusHours(2));
        query.setParameter("end", booking.getEnd().minusHours(2));
        Booking bookingFromDb = query.getSingleResult();
        Assertions.assertEquals(bookingFromDb.getId(), booking.getId());
        Assertions.assertEquals(bookingFromDb.getStart(), booking.getStart());
        Assertions.assertEquals(bookingFromDb.getEnd(), booking.getEnd());
        Assertions.assertEquals(bookingFromDb.getItem().getId(), booking.getItem().getId());
        Assertions.assertEquals(bookingFromDb.getBooker().getId(), booking.getBooker().getId());
        Assertions.assertEquals(bookingFromDb.getStatus(), booking.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void findAllByOwnerIdOrderByStartDescTest() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        bookingRepository.save(booking);
        TypedQuery<Booking> query = em.getEntityManager().createQuery("select b from Booking b where b.item.owner.id = :id " +
                "order by b.start desc", Booking.class);
        query.setParameter("id", booking.getItem().getOwner().getId());
        Booking bookingFromDb = query.getSingleResult();
        Assertions.assertEquals(bookingFromDb.getId(), booking.getId());
        Assertions.assertEquals(bookingFromDb.getStart(), booking.getStart());
        Assertions.assertEquals(bookingFromDb.getEnd(), booking.getEnd());
        Assertions.assertEquals(bookingFromDb.getItem().getId(), booking.getItem().getId());
        Assertions.assertEquals(bookingFromDb.getBooker().getId(), booking.getBooker().getId());
        Assertions.assertEquals(bookingFromDb.getStatus(), booking.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void findAllByOwnerIdTest() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        bookingRepository.save(booking);
        TypedQuery<Booking> query = em.getEntityManager().createQuery("select b from Booking b where b.item.owner.id = :id " +
                "order by b.start", Booking.class);
        query.setParameter("id", booking.getItem().getOwner().getId());
        Booking bookingFromDb = query.getSingleResult();
        Assertions.assertEquals(bookingFromDb.getId(), booking.getId());
        Assertions.assertEquals(bookingFromDb.getStart(), booking.getStart());
        Assertions.assertEquals(bookingFromDb.getEnd(), booking.getEnd());
        Assertions.assertEquals(bookingFromDb.getItem().getId(), booking.getItem().getId());
        Assertions.assertEquals(bookingFromDb.getBooker().getId(), booking.getBooker().getId());
        Assertions.assertEquals(bookingFromDb.getStatus(), booking.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void findAllByOwnerIdAndEndBeforeOrderByStartDescTest() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        bookingRepository.save(booking);
        TypedQuery<Booking> query = em.getEntityManager().createQuery("select b from Booking b where b.item.owner.id = :id " +
                "and b.end < :end order by b.start desc", Booking.class);
        query.setParameter("id", booking.getItem().getOwner().getId());
        query.setParameter("end", booking.getEnd().plusHours(2));
        Booking bookingFromDb = query.getSingleResult();
        Assertions.assertEquals(bookingFromDb.getId(), booking.getId());
        Assertions.assertEquals(bookingFromDb.getStart(), booking.getStart());
        Assertions.assertEquals(bookingFromDb.getEnd(), booking.getEnd());
        Assertions.assertEquals(bookingFromDb.getItem().getId(), booking.getItem().getId());
        Assertions.assertEquals(bookingFromDb.getBooker().getId(), booking.getBooker().getId());
        Assertions.assertEquals(bookingFromDb.getStatus(), booking.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void findAllByOwnerIdAndEndBefore() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        bookingRepository.save(booking);
        TypedQuery<Booking> query = em.getEntityManager().createQuery("select b from Booking b where b.item.owner.id = :id " +
                "and b.end < :end order by b.start", Booking.class);
        query.setParameter("id", booking.getItem().getOwner().getId());
        query.setParameter("end", booking.getEnd().plusHours(2));
        Booking bookingFromDb = query.getSingleResult();
        Assertions.assertEquals(bookingFromDb.getId(), booking.getId());
        Assertions.assertEquals(bookingFromDb.getStart(), booking.getStart());
        Assertions.assertEquals(bookingFromDb.getEnd(), booking.getEnd());
        Assertions.assertEquals(bookingFromDb.getItem().getId(), booking.getItem().getId());
        Assertions.assertEquals(bookingFromDb.getBooker().getId(), booking.getBooker().getId());
        Assertions.assertEquals(bookingFromDb.getStatus(), booking.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void findAllByOwnerIdAndStartAfterOrderByStartDescTest() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        bookingRepository.save(booking);
        TypedQuery<Booking> query = em.getEntityManager().createQuery("select b from Booking b where b.item.owner.id = :id " +
                "and b.start > :start order by b.start desc", Booking.class);
        query.setParameter("id", booking.getItem().getOwner().getId());
        query.setParameter("start", booking.getStart().minusHours(2));
        Booking bookingFromDb = query.getSingleResult();
        Assertions.assertEquals(bookingFromDb.getId(), booking.getId());
        Assertions.assertEquals(bookingFromDb.getStart(), booking.getStart());
        Assertions.assertEquals(bookingFromDb.getEnd(), booking.getEnd());
        Assertions.assertEquals(bookingFromDb.getItem().getId(), booking.getItem().getId());
        Assertions.assertEquals(bookingFromDb.getBooker().getId(), booking.getBooker().getId());
        Assertions.assertEquals(bookingFromDb.getStatus(), booking.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void findAllByOwnerIdAndStartAfterTest() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        bookingRepository.save(booking);
        TypedQuery<Booking> query = em.getEntityManager().createQuery("select b from Booking b where b.item.owner.id = :id " +
                "and b.start > :start order by b.start", Booking.class);
        query.setParameter("id", booking.getItem().getOwner().getId());
        query.setParameter("start", booking.getStart().minusHours(2));
        Booking bookingFromDb = query.getSingleResult();
        Assertions.assertEquals(bookingFromDb.getId(), booking.getId());
        Assertions.assertEquals(bookingFromDb.getStart(), booking.getStart());
        Assertions.assertEquals(bookingFromDb.getEnd(), booking.getEnd());
        Assertions.assertEquals(bookingFromDb.getItem().getId(), booking.getItem().getId());
        Assertions.assertEquals(bookingFromDb.getBooker().getId(), booking.getBooker().getId());
        Assertions.assertEquals(bookingFromDb.getStatus(), booking.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void findAllByOwnerIdAndStatusIsOrderByStartDescTest() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        bookingRepository.save(booking);
        TypedQuery<Booking> query = em.getEntityManager().createQuery("select b from Booking b where b.item.owner.id = :id " +
                "and b.status = :status order by b.start desc", Booking.class);
        query.setParameter("id", booking.getItem().getOwner().getId());
        query.setParameter("status", booking.getStatus());
        Booking bookingFromDb = query.getSingleResult();
        Assertions.assertEquals(bookingFromDb.getId(), booking.getId());
        Assertions.assertEquals(bookingFromDb.getStart(), booking.getStart());
        Assertions.assertEquals(bookingFromDb.getEnd(), booking.getEnd());
        Assertions.assertEquals(bookingFromDb.getItem().getId(), booking.getItem().getId());
        Assertions.assertEquals(bookingFromDb.getBooker().getId(), booking.getBooker().getId());
        Assertions.assertEquals(bookingFromDb.getStatus(), booking.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void findAllByOwnerIdAndStatusIsTest() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        bookingRepository.save(booking);
        TypedQuery<Booking> query = em.getEntityManager().createQuery("select b from Booking b where b.item.owner.id = :id " +
                "and b.status = :status order by b.start", Booking.class);
        query.setParameter("id", booking.getItem().getOwner().getId());
        query.setParameter("status", booking.getStatus());
        Booking bookingFromDb = query.getSingleResult();
        Assertions.assertEquals(bookingFromDb.getId(), booking.getId());
        Assertions.assertEquals(bookingFromDb.getStart(), booking.getStart());
        Assertions.assertEquals(bookingFromDb.getEnd(), booking.getEnd());
        Assertions.assertEquals(bookingFromDb.getItem().getId(), booking.getItem().getId());
        Assertions.assertEquals(bookingFromDb.getBooker().getId(), booking.getBooker().getId());
        Assertions.assertEquals(bookingFromDb.getStatus(), booking.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDescTest() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        bookingRepository.save(booking);
        TypedQuery<Booking> query = em.getEntityManager().createQuery("select b from Booking b where b.item.owner.id = :id " +
                "and b.start < :start and b.end > :end order by b.start desc", Booking.class);
        query.setParameter("id", booking.getItem().getOwner().getId());
        query.setParameter("start", booking.getStart().plusHours(2));
        query.setParameter("end", booking.getStart().minusHours(2));
        Booking bookingFromDb = query.getSingleResult();
        Assertions.assertEquals(bookingFromDb.getId(), booking.getId());
        Assertions.assertEquals(bookingFromDb.getStart(), booking.getStart());
        Assertions.assertEquals(bookingFromDb.getEnd(), booking.getEnd());
        Assertions.assertEquals(bookingFromDb.getItem().getId(), booking.getItem().getId());
        Assertions.assertEquals(bookingFromDb.getBooker().getId(), booking.getBooker().getId());
        Assertions.assertEquals(bookingFromDb.getStatus(), booking.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void findAllByOwnerIdAndStartBeforeAndEndAfterTest() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        bookingRepository.save(booking);
        TypedQuery<Booking> query = em.getEntityManager().createQuery("select b from Booking b where b.item.owner.id = :id " +
                "and b.start < :start and b.end > :end order by b.start", Booking.class);
        query.setParameter("id", booking.getItem().getOwner().getId());
        query.setParameter("start", booking.getStart().plusHours(2));
        query.setParameter("end", booking.getStart().minusHours(2));
        Booking bookingFromDb = query.getSingleResult();
        Assertions.assertEquals(bookingFromDb.getId(), booking.getId());
        Assertions.assertEquals(bookingFromDb.getStart(), booking.getStart());
        Assertions.assertEquals(bookingFromDb.getEnd(), booking.getEnd());
        Assertions.assertEquals(bookingFromDb.getItem().getId(), booking.getItem().getId());
        Assertions.assertEquals(bookingFromDb.getBooker().getId(), booking.getBooker().getId());
        Assertions.assertEquals(bookingFromDb.getStatus(), booking.getStatus());
    }
}