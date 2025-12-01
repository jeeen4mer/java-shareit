package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemStorage extends JpaRepository<Item, Long> {
    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))" +
            " and i.available = true")
    List<Item> searchItem(String text);

    List<Item> findAllByOwnerIdOrderById(Long ownerId);

    boolean existsItemByIdAndOwner_Id(Long itemId, Long ownerId);

    List<Item> findAllByRequestId(Long requestId);

    List<Item> findAllByRequestIdIn(List<Long> requestIds);
}

