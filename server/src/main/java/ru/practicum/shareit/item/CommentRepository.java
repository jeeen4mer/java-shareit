package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.item.id = :itemId ORDER BY c.created DESC")
    List<Comment> findByItemIdOrderByCreatedDesc(Long itemId);
}