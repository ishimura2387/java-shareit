package ru.practicum.shareit.requestTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

@DataJpaTest
public class RequestRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    User user = new User(1L, "user1", "user1@mail.ru");
    ItemRequest itemRequest = new ItemRequest(1L, "description request 1", user, LocalDateTime.now());

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void findAllByRequestorIdNotZeroSizeTest() {
        userRepository.save(user);
        requestRepository.save(itemRequest);
        TypedQuery<ItemRequest> query = em.getEntityManager().createQuery("select r from ItemRequest r where " +
                "r.requestor.id != 2", ItemRequest.class);
        ItemRequest itemRequestFromDb = query.getSingleResult();
        Assertions.assertEquals(itemRequestFromDb.getId(), itemRequest.getId());
        Assertions.assertEquals(itemRequestFromDb.getDescription(), itemRequest.getDescription());
        Assertions.assertEquals(itemRequestFromDb.getRequestor().getName(), itemRequest.getRequestor().getName());
    }
}
