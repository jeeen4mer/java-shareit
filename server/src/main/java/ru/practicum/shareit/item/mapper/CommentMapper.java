package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "itemId", target = "item.id")
    Comment toCommentFromRequest(CommentRequestDto commentRequestDto);

    @Mapping(source = "author.name", target = "authorName")
    CommentResponseDto toCommentResponseDto(Comment comment);

    List<CommentResponseDto> toCommentResponseListDto(List<Comment> comments);
}