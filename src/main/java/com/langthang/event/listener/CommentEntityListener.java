package com.langthang.event.listener;

import com.langthang.dto.CommentDTO;
import com.langthang.event.OnNewCommentEvent;
import com.langthang.model.Comment;
import com.langthang.services.INotificationServices;
import com.langthang.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@Component
public class CommentEntityListener {

    private static INotificationServices notificationServices;

    private static ApplicationEventPublisher eventPublisher;

    @Autowired
    public void init(INotificationServices notificationServices, ApplicationEventPublisher eventPublisher) {
        CommentEntityListener.notificationServices = notificationServices;
        CommentEntityListener.eventPublisher = eventPublisher;
    }

    @PrePersist
    @PreUpdate
    public void prePersist(Comment comment) {
        comment.setContent(Utils.escapeHtml(comment.getContent()));
    }


    @PostPersist
    public void postPersist(Comment comment) {
        eventPublisher.publishEvent(new OnNewCommentEvent(CommentDTO.toCommentDTO(comment)));

        notificationServices.addCommentNotification(comment);
    }
}
