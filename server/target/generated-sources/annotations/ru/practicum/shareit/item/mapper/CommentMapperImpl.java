package ru.practicum.shareit.item.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-01T16:39:31+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class CommentMapperImpl implements CommentMapper {

    @Override
    public Comment toCommentFromRequest(CommentRequestDto commentRequestDto) {
        if ( commentRequestDto == null ) {
            return null;
        }

        Comment.CommentBuilder comment = Comment.builder();

        comment.author( commentRequestDtoToUser( commentRequestDto ) );
        comment.item( commentRequestDtoToItem( commentRequestDto ) );
        comment.text( commentRequestDto.getText() );
        comment.created( commentRequestDto.getCreated() );

        return comment.build();
    }

    @Override
    public CommentResponseDto toCommentResponseDto(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        CommentResponseDto.CommentResponseDtoBuilder commentResponseDto = CommentResponseDto.builder();

        commentResponseDto.authorName( commentAuthorName( comment ) );
        commentResponseDto.id( comment.getId() );
        commentResponseDto.text( comment.getText() );
        commentResponseDto.created( comment.getCreated() );

        return commentResponseDto.build();
    }

    @Override
    public List<CommentResponseDto> toCommentResponseListDto(List<Comment> comments) {
        if ( comments == null ) {
            return null;
        }

        List<CommentResponseDto> list = new ArrayList<CommentResponseDto>( comments.size() );
        for ( Comment comment : comments ) {
            list.add( toCommentResponseDto( comment ) );
        }

        return list;
    }

    protected User commentRequestDtoToUser(CommentRequestDto commentRequestDto) {
        if ( commentRequestDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.id( commentRequestDto.getAuthorId() );

        return user.build();
    }

    protected Item commentRequestDtoToItem(CommentRequestDto commentRequestDto) {
        if ( commentRequestDto == null ) {
            return null;
        }

        Item.ItemBuilder item = Item.builder();

        item.id( commentRequestDto.getItemId() );

        return item.build();
    }

    private String commentAuthorName(Comment comment) {
        if ( comment == null ) {
            return null;
        }
        User author = comment.getAuthor();
        if ( author == null ) {
            return null;
        }
        String name = author.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
