package ru.practicum.shareit.item;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class CommentMapper {
    public Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        return comment;
    }

    public CommentDto fromComment(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setCreated(comment.getCreated());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        return commentDto;
    }
}
