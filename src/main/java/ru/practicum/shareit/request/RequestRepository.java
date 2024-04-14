package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorIdOrderByCreated(long id);

    Page<ItemRequest> findAllByRequestorIdNot(long userId, Pageable pageable);

    @Query("select r from ItemRequest r where r.requestor.id != ?1")
    List<ItemRequest> findAllByRequestorIdNotZeroSize(long userId);
}
