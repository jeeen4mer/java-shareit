package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ItemStorageTest {

    @Autowired
    UserStorage userStorage;

    @Autowired
    ItemStorage itemStorage;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    public void contextLoads() {
        assertNotNull(testEntityManager);
    }

    @Test
    void searchItemsTest() {
        User user = userStorage.save(User.builder()
                .id(1L)
                .name("vvvvvvvv")
                .email("vvvvv@vvvvvvvv.ru")
                .build());

        Item item = itemStorage.save(Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .owner(user)
                .requestId(null)
                .build());

        List<Item> result = new ArrayList<>(itemStorage.searchItem("scRiPti".toLowerCase()));
        assertEquals(item, result.get(0));
        assertEquals(user, result.get(0).getOwner());
        result = new ArrayList<>(itemStorage.searchItem("iTe".toLowerCase()));
        assertEquals(item, result.get(0));
        assertEquals(user, result.get(0).getOwner());
        result = new ArrayList<>(itemStorage.searchItem("SomeWords".toLowerCase()));
        assertEquals(0, result.size());
        userStorage.deleteAll();
        itemStorage.deleteAll();
    }
}
