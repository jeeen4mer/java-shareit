package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.dto.CommentCreateDto;
import ru.practicum.shareit.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
    }

    public ResponseEntity<Object> create(Long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> update(Long userId, Long itemId, ItemDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getById(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllByOwner(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> search(String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("/search?text={text}", null, parameters);
    }

    public ResponseEntity<Object> delete(Long userId, Long itemId) {
        return delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentCreateDto dto) {
        return post("/" + itemId + "/comment", userId, dto);
    }
}