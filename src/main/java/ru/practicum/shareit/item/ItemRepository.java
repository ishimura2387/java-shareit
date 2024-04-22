package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> getItemsByOwnerId(long id, Sort sort);

    List<Item> getItemsByOwnerId(long id, Pageable pageable);

    @Query("select i from Item i where upper(i.name) like upper(concat('%', ?1, '%')) or upper(i.description) " +
            "like upper(concat('%', ?1, '%')) and i.available = true")
    List<Item> getItemsForRent(String text);

    @Query("select i from Item i where upper(i.name) like upper(concat('%', ?1, '%')) or upper(i.description) " +
            "like upper(concat('%', ?1, '%')) and i.available = true")
    List<Item> getItemsForRentWithPagination(String text, Pageable pageable);

    Item findByIdAndOwnerId(long itemId, long ownerId);

    List<Item> findAllByRequestId(long requestId, Sort sort);

    List<Item> findAllByRequestIdIn(List<Long> ids);
}
