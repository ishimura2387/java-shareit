package ru.practicum.shareit.itemTest;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;

    User user = new User(1L, "user1", "user1@mail.ru");
    Item item = new Item(1L, "item1", "description item 1", true, user, null);
    User user2 = new User(2L, "user2", "user2@mail.ru");
    LocalDateTime time = LocalDateTime.now();
    Booking booking = new Booking(1L, time.plusSeconds(1), time.plusSeconds(2), item, user2, Status.WAITING);
    Comment comment = new Comment(1L, "text", item, user2, LocalDateTime.now().plusSeconds(5));

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void findAllTest() throws InterruptedException {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        bookingRepository.save(booking);
        TypedQuery<Comment> query = em.getEntityManager().createQuery("select c from Comment c where c.item.id in :ids",
                Comment.class);
        TimeUnit.SECONDS.sleep(3);
        commentRepository.save(comment);
        List<Long> ids = new ArrayList<>();
        ids.add(booking.getItem().getId());
        query.setParameter("ids", ids);
        Comment commentFromDb = query.getSingleResult();
        Assertions.assertEquals(commentFromDb.getId(), comment.getId());
        Assertions.assertEquals(commentFromDb.getText(), comment.getText());
    }
}
