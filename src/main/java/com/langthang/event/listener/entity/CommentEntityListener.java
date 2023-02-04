package com.langthang.event.listener.entity;

import com.github.sonus21.rqueue.core.RqueueMessageEnqueuer;
import com.langthang.config.RQueueConfig;
import com.langthang.event.model.NotificationRequest;
import com.langthang.event.model.OnNewCommentEvent;
import com.langthang.model.constraints.NotificationType;
import com.langthang.model.dto.response.CommentDTO;
import com.langthang.model.entity.Comment;
import com.langthang.utils.MyStringUtils;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;


@Component
public class CommentEntityListener {

    private static RqueueMessageEnqueuer msgPublisher;
    private static ApplicationEventPublisher eventPublisher;

    @Autowired
    public void init(ApplicationEventPublisher eventPublisher, RqueueMessageEnqueuer msgPublisher) {
        CommentEntityListener.eventPublisher = eventPublisher;
        CommentEntityListener.msgPublisher = msgPublisher;
    }

    @PrePersist
    @PreUpdate
    public void prePersist(Comment comment) {
        comment.setContent(MyStringUtils.escapeHtml(comment.getContent()));
    }


    @PostPersist
    public void postPersist(Comment comment) {
        sendToNotificationFactory(comment);

        eventPublisher.publishEvent(new OnNewCommentEvent(CommentDTO.toCommentDTO(comment)));
    }

    private void sendToNotificationFactory(Comment comment) {
        var notificationRequest = new NotificationRequest(
                comment.getAccount().getId(),
                comment.getPost().getAuthor().getId(),
                comment.getPost().getId(),
                NotificationType.COMMENT_ON_POST
        );
        msgPublisher.enqueue(RQueueConfig.QK_NOTIFICATION_FACTORY_QUEUE, notificationRequest);
    }
}
