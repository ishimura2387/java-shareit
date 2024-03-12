package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> getItemsByOwnerIdOrderById(long id);

    @Query(" select i from Item i where upper(i.name) like upper(concat('%', ?1, '%')) or upper(i.description) " +
            "like upper(concat('%', ?1, '%')) and i.available = true")
    List<Item> getItemsForRent(String text);
    Item findByIdAndOwnerId(long itemId, long ownerId);
}
