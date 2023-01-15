package com.langthang.event.listener.entity;

import com.langthang.config.RabbitMqConfig;
import com.langthang.event.model.NotificationRequest;
import com.langthang.event.model.OnNewCommentEvent;
import com.langthang.model.constraints.NotificationType;
import com.langthang.model.dto.response.CommentDTO;
import com.langthang.model.entity.Comment;
import com.langthang.utils.MyStringUtils;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;


@Component
public class CommentEntityListener {

    private static RabbitTemplate rabbitTemplate;
    private static ApplicationEventPublisher eventPublisher;

    @Autowired
    public void init(RabbitTemplate rabbitTemplate, ApplicationEventPublisher eventPublisher) {
        CommentEntityListener.eventPublisher = eventPublisher;
        CommentEntityListener.rabbitTemplate = rabbitTemplate;
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
        rabbitTemplate.convertAndSend(RabbitMqConfig.QK_NOTIFICATION_FACTORY_QUEUE, notificationRequest);
    }
}
