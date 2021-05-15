package com.langthang.event;

import com.langthang.dto.CommentDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnNewCommentEvent extends ApplicationEvent {
    private final CommentDTO newComment;

    public OnNewCommentEvent(CommentDTO newComment) {
        super(newComment);
        this.newComment = newComment;
    }
}
