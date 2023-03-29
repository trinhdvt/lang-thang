package com.langthang.event.model;

import com.langthang.model.dto.response.CommentDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnNewCommentEvent extends ApplicationEvent {
    private final transient CommentDTO newComment;

    public OnNewCommentEvent(CommentDTO newComment) {
        super(newComment);
        this.newComment = newComment;
    }
}