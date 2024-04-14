package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.TypedQuery;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    User user = new User(1, "user1", "user1@mail.ru");
    Item item = new Item(1, "item1", "description item 1", true, user, null);


    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getItemsForRentTest() {
        userRepository.save(user);
        itemRepository.save(item);
        TypedQuery<Item> query = em.getEntityManager().createQuery("select i from Item i where upper(i.name) like " +
                "upper(concat('%', :text, '%')) or upper(i.description) like upper(concat('%', :text, '%')) and i.available = true", Item.class);
        query.setParameter("text", "description item 1");
        Item itemFromDb = query.getSingleResult();
        Assertions.assertEquals(itemFromDb.getId(), item.getId());
        Assertions.assertEquals(itemFromDb.getName(), item.getName());
        Assertions.assertEquals(itemFromDb.getDescription(), item.getDescription());
    }
}
