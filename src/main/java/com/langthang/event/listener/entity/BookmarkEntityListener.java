package com.langthang.event.listener.entity;

import com.langthang.config.RabbitMqConfig;
import com.langthang.event.model.NotificationRequest;
import com.langthang.model.constraints.NotificationType;
import com.langthang.model.entity.BookmarkedPost;
import jakarta.persistence.PostPersist;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class BookmarkEntityListener {

    private static RabbitTemplate rabbitTemplate;

    @Autowired
    public void init(RabbitTemplate rabbitTemplate) {
        BookmarkEntityListener.rabbitTemplate = rabbitTemplate;
    }

    @PostPersist
    public void onNewBookmark(BookmarkedPost bookmarkedPost) {
        var notificationRequest = new NotificationRequest(
                bookmarkedPost.getAccount().getId(),
                bookmarkedPost.getPost().getAuthor().getId(),
                bookmarkedPost.getPost().getId(),
                NotificationType.BOOKMARK_POST
        );
        rabbitTemplate.convertAndSend(RabbitMqConfig.QK_NOTIFICATION_FACTORY_QUEUE, notificationRequest);
    }
}